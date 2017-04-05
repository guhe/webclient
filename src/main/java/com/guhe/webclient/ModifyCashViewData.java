package com.guhe.webclient;

import javax.xml.bind.annotation.XmlRootElement;

import com.guhe.util.Reflector;

@XmlRootElement(name = "modifyCash")
class ModifyCashViewData {

	private String target;
	private double newAmount;
	private double oldAmount;
	private String date;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public double getNewAmount() {
		return newAmount;
	}

	public void setNewAmount(double newAmount) {
		this.newAmount = newAmount;
	}

	public double getOldAmount() {
		return oldAmount;
	}

	public void setOldAmount(double oldAmount) {
		this.oldAmount = oldAmount;
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
