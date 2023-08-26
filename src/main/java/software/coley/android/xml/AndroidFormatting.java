package software.coley.android.xml;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

/**
 * Constants taken from
 * <a href="https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/util/TypedValue.java">
 * Android Platform: TypedValue.java</a>
 */
public class AndroidFormatting {
	// Where the unit type information is. This gives us 16 possible types, as defined below.
	public static final int COMPLEX_UNIT_SHIFT = 0;
	public static final int COMPLEX_UNIT_MASK = 0xf;
	// TYPE_DIMENSION: Value is raw pixels.
	public static final int COMPLEX_UNIT_PX = 0;
	// TYPE_DIMENSION: Value is Device Independent Pixels.
	public static final int COMPLEX_UNIT_DIP = 1;
	// TYPE_DIMENSION: Value is a Scaled device independent Pixels.
	public static final int COMPLEX_UNIT_SP = 2;
	// TYPE_DIMENSION: Value is in points.
	public static final int COMPLEX_UNIT_PT = 3;
	// TYPE_DIMENSION: Value is in inches.
	public static final int COMPLEX_UNIT_IN = 4;
	// TYPE_DIMENSION: Value is in millimeters.
	public static final int COMPLEX_UNIT_MM = 5;
	// TYPE_FRACTION: A basic fraction of the overall size.
	public static final int COMPLEX_UNIT_FRACTION = 0;
	// TYPE_FRACTION: A fraction of the parent size.
	public static final int COMPLEX_UNIT_FRACTION_PARENT = 1;
	// Where the radix information is, telling where the decimal place
	// appears in the mantissa. This give us 4 possible fixed point
	// representations as defined below.
	public static final int COMPLEX_RADIX_SHIFT = 4;
	public static final int COMPLEX_RADIX_MASK = 0x3;
	// Where the actual value is. This gives us 23 bits of
	// precision. The top bit is the sign.
	public static final int COMPLEX_MANTISSA_SHIFT = 8;
	public static final double MANTISSA_MULT = 1.0f / (1 << COMPLEX_MANTISSA_SHIFT);
	public static final double[] RADIX_MULTS = new double[]{
			1.0f * MANTISSA_MULT,
			1.0f / (1 << 7) * MANTISSA_MULT,
			1.0f / (1 << 15) * MANTISSA_MULT,
			1.0f / (1 << 23) * MANTISSA_MULT
	};
	public static final int COMPLEX_MANTISSA_MASK = 0xffffff;
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.0000");

	/**
	 * @param data
	 * 		Input packed data.
	 *
	 * @return Output human-readable dimension string.
	 */
	@Nonnull
	public static String toDimensionString(int data) {
		double value = getValue(data);
		int unitType = data & COMPLEX_UNIT_MASK;
		String unit = getComplexUnit(unitType);
		return getValueString(value) + unit;
	}

	/**
	 * @param data
	 * 		Input packed data.
	 *
	 * @return Output human-readable fraction string.
	 */
	public static String toFractionString(int data) {
		double value = getValue(data);
		int unitType = data & COMPLEX_UNIT_MASK;
		String unit = getFractionalUnit(unitType);


		return getValueString(value * 100) + unit;
	}

	/**
	 * @param unitType
	 * 		Unit type id.
	 *
	 * @return Format name to be used as suffix in dimension strings.
	 */
	@Nonnull
	public static String getComplexUnit(int unitType) {
		switch (unitType) {
			case COMPLEX_UNIT_PX:
				return "px";
			case COMPLEX_UNIT_DIP:
				return "dp";
			case COMPLEX_UNIT_SP:
				return "sp";
			case COMPLEX_UNIT_PT:
				return "pt";
			case COMPLEX_UNIT_IN:
				return "in";
			case COMPLEX_UNIT_MM:
				return "mm";
			default:
				return "?d" + Integer.toHexString(unitType);
		}
	}

	/**
	 * @param unitType
	 * 		Unit type id.
	 *
	 * @return Format name to be used as suffix in dimension strings.
	 */
	@Nonnull
	public static String getFractionalUnit(int unitType) {
		switch (unitType) {
			case COMPLEX_UNIT_FRACTION:
				return "%";
			case COMPLEX_UNIT_FRACTION_PARENT:
				return "%p";
			default:
				return "?f" + Integer.toHexString(unitType);
		}
	}

	private static double getValue(int data) {
		int multIndex = data >> COMPLEX_RADIX_SHIFT & COMPLEX_RADIX_MASK;
		return (data & COMPLEX_MANTISSA_MASK << COMPLEX_MANTISSA_SHIFT) * RADIX_MULTS[multIndex];
	}

	private static String getValueString(double value) {
		if (Double.compare(value, Math.floor(value)) == 0 && !Double.isInfinite(value))
			return Integer.toString((int) value);
		return DECIMAL_FORMAT.format(value);
	}
}
