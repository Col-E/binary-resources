package software.coley.android.xml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Delegating model of {@link AndroidResourceProvider}.
 *
 * @author Matt Coley
 */
public class DelegatingAndroidResourceProvider implements AndroidResourceProvider {
	private final AndroidResourceProvider delegate;

	/**
	 * @param delegate
	 * 		Delegate to pass to.
	 * 		Can be {@code null} to provide {@code null} and {@code false} values as a default.
	 */
	public DelegatingAndroidResourceProvider(@Nullable AndroidResourceProvider delegate) {
		this.delegate = delegate;
	}

	@Override
	public boolean hasResName(int resId) {
		if (delegate == null) return false;
		return delegate.hasResName(resId);
	}

	@Nullable
	@Override
	public String getResName(int resId) {
		if (delegate == null) return null;
		return delegate.getResName(resId);
	}

	@Override
	public boolean hasResFlag(@Nonnull String resName) {
		if (delegate == null) return false;
		return delegate.hasResFlag(resName);
	}

	@Nullable
	@Override
	public String getResFlagNames(String resName, long mask) {
		if (delegate == null) return null;
		return delegate.getResFlagNames(resName, mask);
	}

	@Override
	public boolean hasResEnum(@Nonnull String resName) {
		if (delegate == null) return false;
		return delegate.hasResEnum(resName);
	}

	@Nullable
	@Override
	public String getResEnumName(String resName, long value) {
		if (delegate == null) return null;
		return delegate.getResEnumName(resName, value);
	}
}
