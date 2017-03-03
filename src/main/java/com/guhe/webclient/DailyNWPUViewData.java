package com.guhe.webclient;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dailyNWPU")
public class DailyNWPUViewData {
 
	private String date;
	private double netWorthPerUnit;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getNetWorthPerUnit() {
		return netWorthPerUnit;
	}
	public void setNetWorthPerUnit(double netWorthPerUnit) {
		this.netWorthPerUnit = netWorthPerUnit;
	}
}
