package com.guhe.webclient;

import com.guhe.util.Reflector;

public class Stock {
	private String code;
	private String name;

	public Stock(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}

	public Exchange getExchange() {
		return Exchange.get(code);
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return Reflector.getHashCodeByAllFields(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Reflector.isAllFieldsEquals(obj, this);
	}

	public enum Exchange {
		ShangHai('6'), ShenZheng('0');

		private char first;

		private Exchange(char first) {
			this.first = first;
		}

		public static Exchange get(String code) {
			if (code == null || code.length() == 0) {
				throw new RuntimeException();
			}
			for (Exchange ex : values()) {
				if (code.charAt(0) == ex.first) {
					return ex;
				}
			}
			throw new RuntimeException();
		}
	}
}
