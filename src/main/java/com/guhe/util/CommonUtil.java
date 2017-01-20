package com.guhe.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class CommonUtil {

	public static boolean dEquals(double num1, double num2, int n) {
		return Math.abs(num1 - num2) < Math.pow(10, -n);
	}

	public static int dCompare(double num1, double num2, int n) {
		if (dEquals(num1, num2, n)) {
			return 0;
		}
		return num1 < num2 ? -1 : 1;
	}

	public static double dRound(double num, int n) {
		double pow = Math.pow(10, n);
		return Math.round(num * pow) / pow;
	}

	public static Date parseDate(String format, String source) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(source);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String formatDate(String format, Date source) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(source);
	}

}
