package com.guhe.portfolio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.guhe.market.Exchange;
import com.guhe.util.Reflector;

@Entity
public class Stock {

	@Id
	@GeneratedValue
	private int id;

	private String code;

	private String name;

	public Stock() {

	}

	public Stock(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}

	public Exchange getExchange() {
		return Exchange.instance(code);
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
