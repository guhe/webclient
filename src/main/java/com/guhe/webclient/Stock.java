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
}
