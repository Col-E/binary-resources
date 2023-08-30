package software.coley.android.xml;

import com.android.xml.XmlBuilder;
import com.google.devrel.gmscore.tools.apk.arsc.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A utility for decoding XML contents from a {@link BinaryResourceFile} utilizing resources
 * fed from optional ARSC file inputs.
 *
 * @author Matt Coley
 */
public class XmlDecoder {
	private final XmlBuilder builder = new XmlBuilder();
	private final Map<String, String> namespaces = new HashMap<>();
	private final SplitAndroidResourceProvider resourceProvider;
	private boolean namespacesAdded;
	private StringPoolChunk stringPool;
	private XmlResourceMapChunk resourceMap;

	/**
	 * @param androidResources
	 * 		Core android resource model to provide information for decoding.
	 * @param arscResources
	 * 		Optional ARSC file model to provide additional information for decoding.
	 * 		Can be {@code null} to skip info, but output will be missing some details.
	 */
	public XmlDecoder(@Nonnull AndroidResourceProvider androidResources,
					  @Nullable AndroidResourceProvider arscResources) {
		resourceProvider = new SplitAndroidResourceProvider(new DelegatingAndroidResourceProvider(arscResources), androidResources);
	}

	/**
	 * @param binaryResource
	 * 		Binary XML resource to decode.
	 * @param androidResources
	 * 		Core android resource model to provide information for decoding.
	 * @param arscResources
	 * 		Optional ARSC file model to provide additional information for decoding.
	 * 		Can be {@code null} to skip info, but output will be missing some details.
	 *
	 * @return Decoded string from binary model.
	 */
	@Nonnull
	public static String decode(@Nonnull BinaryResourceFile binaryResource,
								@Nonnull AndroidResourceProvider androidResources,
								@Nullable AndroidResourceProvider arscResources) {
		StringBuilder out = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		binaryResource.getChunks().stream()
				.filter(c -> c instanceof XmlChunk)
				.map(c -> (XmlChunk) c).forEach(xmlChunk -> {
					XmlDecoder printer = new XmlDecoder(androidResources, arscResources);
					visitChunks(xmlChunk.getChunks(), printer);
					out.append(printer.getReconstructedXml());
				});
		return out.toString();
	}

	/**
	 * @param chunks
	 * 		Chunks to visit.
	 * @param handler
	 * 		XML printer implementation.
	 * 		Use {@link XmlDecoder#getReconstructedXml()} to see get the XML output.
	 */
	public static void visitChunks(@Nonnull Map<Integer, Chunk> chunks, @Nonnull XmlDecoder handler) {
		List<Chunk> contentChunks = sortByOffset(chunks);
		for (Chunk chunk : contentChunks) {
			if (chunk instanceof StringPoolChunk) {
				handler.stringPool((StringPoolChunk) chunk);
			} else if (chunk instanceof XmlResourceMapChunk) {
				handler.xmlResourceMap((XmlResourceMapChunk) chunk);
			} else if (chunk instanceof XmlNamespaceStartChunk) {
				handler.startNamespace((XmlNamespaceStartChunk) chunk);
			} else if (chunk instanceof XmlNamespaceEndChunk) {
				handler.endNamespace((XmlNamespaceEndChunk) chunk);
			} else if (chunk instanceof XmlStartElementChunk) {
				handler.startElement((XmlStartElementChunk) chunk);
			} else if (chunk instanceof XmlEndElementChunk) {
				handler.endElement((XmlEndElementChunk) chunk);
			} else {
				// logger.warn("Unhandled XML chunk type: {}", chunk.getClass().getSimpleName());
			}
		}
	}

	@Nonnull
	private static List<Chunk> sortByOffset(@Nonnull Map<Integer, Chunk> contentChunks) {
		return contentChunks.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.map(Map.Entry::getValue)
				.collect(Collectors.toList());
	}

	/**
	 * Sets the current string pool.
	 *
	 * @param chunk
	 * 		String chunk to visit.
	 */
	public void stringPool(@Nonnull StringPoolChunk chunk) {
		stringPool = chunk;
	}

	/**
	 * Associates the namespace URI to the namespace prefix.
	 *
	 * @param chunk
	 * 		Namespace chunk to visit.
	 */
	public void startNamespace(@Nonnull XmlNamespaceStartChunk chunk) {
		// Collect mapping of namespaces to prefixes
		// Used later when handling creation of elements referencing namespaces.
		namespaces.put(chunk.getUri(), chunk.getPrefix());
	}

	/**
	 * Appends the element open tag and attributes.
	 *
	 * @param chunk
	 * 		XML element chunk to visit.
	 */
	public void startElement(@Nonnull XmlStartElementChunk chunk) {
		builder.startTag(chunk.getName());

		// If this is the first tag, also print out the namespaces
		if (!namespacesAdded && !namespaces.isEmpty()) {
			namespacesAdded = true;
			for (Map.Entry<String, String> entry : namespaces.entrySet()) {
				builder.attribute("xmlns", entry.getValue(), entry.getKey());
			}
		}

		for (XmlAttribute xmlAttribute : chunk.getAttributes()) {
			String prefix = namespaces.get(xmlAttribute.namespace());
			if (prefix == null) prefix = "";
			builder.attribute(prefix, getAttributeName(xmlAttribute), getValue(xmlAttribute));
		}
	}

	/**
	 * Does nothing.
	 *
	 * @param chunk
	 * 		Resource map chunk to visit.
	 */
	public void xmlResourceMap(@Nonnull XmlResourceMapChunk chunk) {
		resourceMap = chunk;
	}

	/**
	 * Does nothing.
	 *
	 * @param chunk
	 * 		Namespace end chunk to visit.
	 */
	public void endNamespace(@Nonnull XmlNamespaceEndChunk chunk) {
		// no-op
	}

	/**
	 * Appends the closing element tag.
	 *
	 * @param chunk
	 * 		Element end chunk to visit.
	 */
	public void endElement(@Nonnull XmlEndElementChunk chunk) {
		builder.endTag(chunk.getName());
	}

	/**
	 * @return XML output.
	 */
	@Nonnull
	public String getReconstructedXml() {
		return builder.toString();
	}

	/**
	 * Attempts to get the name of the attribute, first from the {@link StringPoolChunk} otherwise fallback
	 * to the {@link XmlResourceMapChunk}
	 *
	 * @param attribute Current XML attribute.
	 * @return The name of the attribute, empty if resourceMap is null or the raw resource id
	 */
	@Nonnull
	private String getAttributeName(@Nonnull XmlAttribute attribute) {
		String name = attribute.name();
		if (!(name == null || name.isEmpty()))
			return name;

		if (resourceMap == null)
			return "";

		BinaryResourceIdentifier resourceId = resourceMap.getResourceId(attribute.nameIndex());
		name = resourceProvider.getResName(resourceId.id());
		// See res-map.txt
		if (name == null)
			return String.format("(%s)", resourceId);

		return name.replace("attr/", "android:");
	}

	@Nonnull
	private String getValue(@Nonnull XmlAttribute attribute) {
		String rawValue = attribute.rawValue();
		if (!(rawValue == null || rawValue.isEmpty()))
			return rawValue;

		BinaryResourceValue resValue = attribute.typedValue();
		return formatValue(resValue, attribute.name());
	}

	/**
	 * @param resValue
	 * 		The value to format into a string representation.
	 * @param elementName
	 * 		The name of the element holding the value.
	 *
	 * @return Formatted string.
	 */
	@Nonnull
	public String formatValue(@Nonnull BinaryResourceValue resValue,
							  @Nonnull String elementName) {
		int data = resValue.data();
		switch (resValue.type()) {
			case UNKNOWN:
				return "?";
			case NULL:
				return "null";
			case ATTRIBUTE: {
				String resName = resourceProvider.getPrimary().getResName(data);
				if (resName != null)
					return "?" + resName;
				resName = resourceProvider.getSecondary().getResName(data);
				if (resName != null)
					return "?android:" + resName;
				return String.format(Locale.US, "?0x%1$x", data);
			}
			case STRING:
				return stringPool != null && stringPool.getStringCount() < data
						? stringPool.getString(data)
						: String.format(Locale.US, "@string/0x%1$x", data);
			case FLOAT:
				return String.format(Locale.US, "%f", (float) data);
			case FRACTION:
				return AndroidFormatting.toFractionString(data);
			case DIMENSION:
				return AndroidFormatting.toDimensionString(data);
			case REFERENCE:
			case DYNAMIC_REFERENCE: {
				if (data == 0)
					return "0";
				String resName = resourceProvider.getPrimary().getResName(data);
				if (resName != null)
					return "@" + resName;
				resName = resourceProvider.getSecondary().getResName(data);
				if (resName != null)
					return "@android:" + resName;
				return String.format(Locale.US, "@ref/0x%1$08x", data);
			}
			case DYNAMIC_ATTRIBUTE:
				// TODO: Google's XmlPrinter has no reference implementation,
				//   so I'm not sure how we're supposed to represent this yet.
				break;
			case INT_DEC: {
				String rep = null;
				if (resourceProvider.hasResFlag(elementName))
					rep = resourceProvider.getResFlagNames(elementName, data);
				else if (resourceProvider.hasResEnum(elementName))
					rep = resourceProvider.getResEnumName(elementName, data);
				if (rep == null)
					rep = Integer.toString(data);
				return rep;
			}
			case INT_HEX: {
				String rep = null;
				if (resourceProvider.hasResFlag(elementName))
					rep = resourceProvider.getResFlagNames(elementName, data);
				else if (resourceProvider.hasResEnum(elementName))
					rep = resourceProvider.getResEnumName(elementName, data);
				if (rep == null)
					rep = "0x" + Integer.toHexString(data);
				return rep;
			}
			case INT_BOOLEAN:
				return Boolean.toString(data != 0);
			case INT_COLOR_ARGB8:
				return String.format("argb8(0x%x)", data);
			case INT_COLOR_RGB8:
				return String.format("rgb8(0x%x)", data);
			case INT_COLOR_ARGB4:
				return String.format("argb4(0x%x)", data);
			case INT_COLOR_RGB4:
				return String.format("rgb4(0x%x)", data);
		}

		return String.format("@res/0x%x", data);
	}
}