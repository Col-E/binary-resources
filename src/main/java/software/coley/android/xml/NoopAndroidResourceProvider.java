package software.coley.android.xml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Android resource provider that provides nothing.
 *
 * @author Matt Coley
 */
@SuppressWarnings("unused")
public class NoopAndroidResourceProvider implements AndroidResourceProvider {
	/**
	 * Singleton instance of this no-op provider.
	 */
	public static final NoopAndroidResourceProvider INSTANCE = new NoopAndroidResourceProvider();

	private NoopAndroidResourceProvider() {
	}

	@Override
	public boolean hasResName(int resId) {
		return false;
	}

	@Nullable
	@Override
	public String getResName(int resId) {
		return null;
	}

	@Override
	public boolean hasResFlag(@Nonnull String resName) {
		return false;
	}

	@Nullable
	@Override
	public String getResFlagNames(String resName, long mask) {
		return null;
	}

	@Override
	public boolean hasResEnum(@Nonnull String resName) {
		return false;
	}

	@Nullable
	@Override
	public String getResEnumName(String resName, long value) {
		return null;
	}
}
