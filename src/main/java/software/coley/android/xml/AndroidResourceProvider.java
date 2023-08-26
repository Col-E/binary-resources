package software.coley.android.xml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Outline of a service providing values from the Android base, or application's ARSC file model.
 *
 * @author Matt Coley
 */
public interface AndroidResourceProvider {
	/**
	 * @param resId Resource ID.
	 * @return {@code true} when the resource identifier has a name.
	 */
	boolean hasResName(int resId);

	/**
	 * @param resId
	 * 		Resource ID.
	 *
	 * @return Name of resource, or {@code null} if not present.
	 */
	@Nullable
	String getResName(int resId);

	/**
	 * @param resName
	 * 		Resource name.
	 *
	 * @return {@code true} when the resource is a known flag.
	 */
	boolean hasResFlag(@Nonnull String resName);

	/**
	 * @param resName
	 * 		Resource name.
	 * @param mask
	 * 		Flag value mask.
	 *
	 * @return Flag names <i>(Separated by {@code |})</i> for the associated value if known, otherwise {@code null}.
	 */
	@Nullable
	String getResFlagNames(String resName, long mask);

	/**
	 * @param resName
	 * 		Resource name.
	 *
	 * @return {@code true} when the resource is a known enum.
	 */
	boolean hasResEnum(@Nonnull String resName);

	/**
	 * @param resName
	 * 		Resource name.
	 * @param value
	 * 		Enum value key.
	 *
	 * @return Enum name for the associated value if known, otherwise {@code null}.
	 */
	@Nullable
	String getResEnumName(String resName, long value);
}
