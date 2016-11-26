package com.guhe.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Reflector {

	public static boolean isAllFieldsEquals(Object obj1, Object obj2) {
		if (obj1 == obj2) {
			return true;
		}

		if (obj1 == null || obj2 == null) {
			return false;
		}

		if (obj1.getClass() != obj2.getClass()) {
			return false;
		}

		Class<?> clazz = obj1.getClass();
		Field[] fields = clazz.getDeclaredFields();

		try {
			for (Field field : fields) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				Object v1 = getFieldValue(field, obj1);
				Object v2 = getFieldValue(field, obj2);

				if ((v1 == null && v2 != null)
						|| (v1 != null && !v1.equals(v2))) {
					return false;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	public static int getHashCodeByAllFields(Object obj) {
		if (obj == null) {
			throw new NullPointerException();
		}

		final int prime = 31;
		int result = 1;
		Field[] fields = obj.getClass().getDeclaredFields();
		try {
			for (Field field : fields) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				Object v = getFieldValue(field, obj);
				result = prime * result + ((v == null) ? 0 : v.hashCode());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	public static String toStringByAllFields(Object obj) {
		if (obj == null) {
			throw new NullPointerException();
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append(obj.getClass().getSimpleName());
		buffer.append("[");
		Field[] fields = obj.getClass().getDeclaredFields();
		try {
			for (Field field : fields) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				Object v = getFieldValue(field, obj);
				buffer.append(field.getName()).append("=").append(v)
						.append(",");
			}
			if (buffer.charAt(buffer.length() - 1) != '[') {
				buffer.deleteCharAt(buffer.length() - 1);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		buffer.append("]");

		return buffer.toString();
	}
	
	public static Object getFieldValue(Field field, Object instance) throws IllegalArgumentException, IllegalAccessException{
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		Object rlt = field.get(instance);
		field.setAccessible(accessible);
		return rlt;
	}
}
