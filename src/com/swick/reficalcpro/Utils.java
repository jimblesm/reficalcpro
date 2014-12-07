package com.swick.reficalcpro;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {
	public static BigDecimal newBigDecimal(String arg) {
		return new BigDecimal(arg).setScale(2, RoundingMode.CEILING);
	}

	public static BigDecimal newBigDecimal(int arg) {
		return new BigDecimal(arg).setScale(2, RoundingMode.CEILING);
	}

	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
		return dividend.divide(divisor, 4, RoundingMode.CEILING);
	}
}
