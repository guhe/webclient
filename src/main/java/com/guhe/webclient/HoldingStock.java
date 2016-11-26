package com.guhe.webclient;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HoldingStock {
	private String name;
	private String code;
	private long amount;
	private double curPrice;
	private double marketWorth;
	private double estimatedCommission;
	private double estimatedTax;
	private double netWorth;
	private double proportion;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public double getCurPrice() {
		return curPrice;
	}

	public void setCurPrice(double curPrice) {
		this.curPrice = curPrice;
	}

	public double getMarketWorth() {
		return marketWorth;
	}

	public void setMarketWorth(double marketWorth) {
		this.marketWorth = marketWorth;
	}

	public double getEstimatedCommission() {
		return estimatedCommission;
	}

	public void setEstimatedCommission(double estimatedCommission) {
		this.estimatedCommission = estimatedCommission;
	}

	public double getEstimatedTax() {
		return estimatedTax;
	}

	public void setEstimatedTax(double estimatedTax) {
		this.estimatedTax = estimatedTax;
	}

	public double getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(double netWorth) {
		this.netWorth = netWorth;
	}

	public double getProportion() {
		return proportion;
	}

	public void setProportion(double proportion) {
		this.proportion = proportion;
	}
}
