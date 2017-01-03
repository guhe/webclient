package com.guhe.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class CommonUtil {

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
