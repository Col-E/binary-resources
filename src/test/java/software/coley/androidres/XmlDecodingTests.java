package software.coley.androidres;

import com.google.devrel.gmscore.tools.apk.arsc.BinaryResourceFile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import software.coley.android.xml.AndroidResourceProvider;
import software.coley.android.xml.XmlDecoder;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Tests showcasing XML decoding capabilities, even with tampered inputs.
 *
 * @see #testNormal(Path) Regular {@code AndroidManifext.xml} files.
 * @see #testJanky(Path) Tampered with versions of {@code AndroidManifext.xml} files.
 */
public class XmlDecodingTests {
	private static final AndroidResourceProviderImpl ANDROID_BASE = AndroidResourceProviderImpl.getAndroidBase();

	@ParameterizedTest
	@MethodSource("getNormalSamples")
	void testNormal(Path path) throws IOException {
		// Regular cases
		printDecodedXml(path);
	}

	@ParameterizedTest
	@MethodSource("getJankySamples")
	void testJanky(Path path) throws IOException {
		// These cases will not be parsable without this fork's bug fixes
		printDecodedXml(path);
	}

	private static void printDecodedXml(@Nonnull Path path) throws IOException {
		byte[] bytes = Files.readAllBytes(path);
		BinaryResourceFile binaryResource = new BinaryResourceFile(bytes);

		// If the associated 'arsc' file is present, load it as well so we can feed in values for a more complete XML decode.
		AndroidResourceProvider localResources = null;
		BinaryResourceFile arscBinaryResource = null;
		Path arscPath = path.resolveSibling(path.getFileName().toString().replace(".xml", ".arsc"));
		if (Files.exists(arscPath)) {
			bytes = Files.readAllBytes(arscPath);
			arscBinaryResource = new BinaryResourceFile(bytes);
			localResources = AndroidResourceProviderImpl.fromArsc(arscBinaryResource);
		}

		// We will have some '?' entries, as we only provide the XML model in most cases.
		String decoded = XmlDecoder.decode(binaryResource, ANDROID_BASE, localResources);
		System.out.println(decoded);
	}

	public static Stream<Arguments> getNormalSamples() throws IOException {
		return Files.walk(Paths.get("src/test/resources/normal"))
				.filter(Files::isRegularFile)
				.filter(p -> p.toString().endsWith(".xml"))
				.map(p -> () -> new Path[]{p});
	}

	public static Stream<Arguments> getJankySamples() throws IOException {
		return Files.walk(Paths.get("src/test/resources/janky"))
				.filter(Files::isRegularFile)
				.filter(p -> p.toString().endsWith(".xml"))
				.map(p -> () -> new Path[]{p});
	}
}
