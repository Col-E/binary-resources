package software.coley.android.xml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Splitting delegate model of {@link AndroidResourceProvider}.
 *
 * @author Matt Coley
 */
public class SplitAndroidResourceProvider implements AndroidResourceProvider {
	private final AndroidResourceProvider primary;
	private final AndroidResourceProvider secondary;

	/**
	 * @param primary
	 * 		Primary delegate.
	 * @param secondary
	 * 		Secondary delegate. Used when primary does not have a value.
	 */
	public SplitAndroidResourceProvider(@Nonnull AndroidResourceProvider primary,
										@Nonnull AndroidResourceProvider secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}

	/**
	 * @return Primary resource provider.
	 */
	@Nonnull
	public AndroidResourceProvider getPrimary() {
		return primary;
	}

	/**
	 * @return Secondary resource provider.
	 */
	@Nonnull
	public AndroidResourceProvider getSecondary() {
		return secondary;
	}

	@Override
	public boolean hasResName(int resId) {
		return primary.hasResName(resId) || secondary.hasResName(resId);
	}

	@Nullable
	@Override
	public String getResName(int resId) {
		if (primary.hasResName(resId))
			return primary.getResName(resId);
		return secondary.getResName(resId);
	}

	@Override
	public boolean hasResFlag(@Nonnull String resName) {
		return primary.hasResFlag(resName) || secondary.hasResFlag(resName);
	}

	@Nullable
	@Override
	public String getResFlagNames(String resName, long mask) {
		if (primary.hasResFlag(resName))
			return primary.getResFlagNames(resName, mask);
		return secondary.getResFlagNames(resName, mask);
	}

	@Override
	public boolean hasResEnum(@Nonnull String resName) {
		return primary.hasResEnum(resName) || secondary.hasResEnum(resName);
	}

	@Nullable
	@Override
	public String getResEnumName(String resName, long value) {
		if (primary.hasResEnum(resName))
			return primary.getResEnumName(resName, value);
		return secondary.getResEnumName(resName, value);
	}
}
