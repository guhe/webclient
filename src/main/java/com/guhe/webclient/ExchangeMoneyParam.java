package com.guhe.webclient;

import javax.xml.bind.annotation.XmlRootElement;

import com.guhe.util.Reflector;

@XmlRootElement(name = "ExchangeMoneyParam")
public class ExchangeMoneyParam {
	private String target;
	private double targetAmount;
	private double rmbAmount;
	private String date;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public double getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(double targetAmount) {
		this.targetAmount = targetAmount;
	}

	public double getRmbAmount() {
		return rmbAmount;
	}

	public void setRmbAmount(double rmbAmount) {
		this.rmbAmount = rmbAmount;
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
