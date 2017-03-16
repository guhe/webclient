package com.guhe.portfolio;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.guhe.market.MoneyName;
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
		ShangHai(MoneyName.RMB, '6'), ShenZheng(MoneyName.RMB, '0', '3'), ShangHai_B(MoneyName.USD,
				'9'), ShenZheng_B(MoneyName.HKD, '2');

		private MoneyName moneyName;
		private List<Character> prefixChars;

		private Exchange(MoneyName mn, Character... firsts) {
			moneyName = mn;
			prefixChars = Arrays.asList(firsts);
		}

		public MoneyName getMoneyName() {
			return moneyName;
		}

		private static Exchange get(String code) {
			if (code == null || code.length() == 0) {
				throw new RuntimeException();
			}
			for (Exchange ex : values()) {
				if (ex.prefixChars.contains(code.charAt(0))) {
					return ex;
				}
			}
			throw new RuntimeException();
		}
	}
}
