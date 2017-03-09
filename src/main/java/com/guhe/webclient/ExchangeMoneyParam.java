package com.guhe.webclient;

import javax.xml.bind.annotation.XmlRootElement;

import com.guhe.util.Reflector;

@XmlRootElement(name = "MoneyConvertParam")
public class ExchangeMoneyParam {
	private String target;
	private double exchangeRate;
	private double amount;
	private String date;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	@Override
	public String toString() {
		return Reflector.toStringByAllFields(this);
	}
}
