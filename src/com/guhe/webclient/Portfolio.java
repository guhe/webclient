package com.guhe.webclient;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Portfolio {
	private String id;
	private String name;
	private double netWorthPerUnit;
	private double rateOfReturnYear;
	private double totalWorth;
	private double projectedLiabilities;
	private double netWorth;
	private double stockNetWorth;
	private double cash;
	private double proportionOfStock;

	public Portfolio() {

	}

	public Portfolio(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getNetWorthPerUnit() {
		return netWorthPerUnit;
	}

	public void setNetWorthPerUnit(double netWorthPerUnit) {
		this.netWorthPerUnit = netWorthPerUnit;
	}

	public double getRateOfReturnYear() {
		return rateOfReturnYear;
	}

	public void setRateOfReturnYear(double rateOfReturnYear) {
		this.rateOfReturnYear = rateOfReturnYear;
	}

	public double getTotalWorth() {
		return totalWorth;
	}

	public void setTotalWorth(double totalWorth) {
		this.totalWorth = totalWorth;
	}

	public double getProjectedLiabilities() {
		return projectedLiabilities;
	}

	public void setProjectedLiabilities(double projectedLiabilities) {
		this.projectedLiabilities = projectedLiabilities;
	}

	public double getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(double netWorth) {
		this.netWorth = netWorth;
	}

	public double getStockNetWorth() {
		return stockNetWorth;
	}

	public void setStockNetWorth(double stockNetWorth) {
		this.stockNetWorth = stockNetWorth;
	}

	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	public double getProportionOfStock() {
		return proportionOfStock;
	}

	public void setProportionOfStock(double proportionOfStock) {
		this.proportionOfStock = proportionOfStock;
	}

}
