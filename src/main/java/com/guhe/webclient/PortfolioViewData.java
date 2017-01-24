package com.guhe.webclient;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "portfolio")
class PortfolioViewData {
	private String id;
	private String name;
	private double cash;
	private double totalWorth;
	private double projectedLiabilities;
	private double stockNetWorth;
	private double netWorth;
	private double profit;
	private double netWorthPerUnit;
	private double proportionOfStock;
	private double rateOfReturnYear;
	private double pe;
	private double pb;

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

	public Double getNetWorthPerUnit() {
		return netWorthPerUnit;
	}

	public void setNetWorthPerUnit(Double netWorthPerUnit) {
		this.netWorthPerUnit = netWorthPerUnit;
	}

	public Double getRateOfReturnYear() {
		return rateOfReturnYear;
	}

	public void setRateOfReturnYear(Double rateOfReturnYear) {
		this.rateOfReturnYear = rateOfReturnYear;
	}

	public Double getTotalWorth() {
		return totalWorth;
	}

	public void setTotalWorth(Double totalWorth) {
		this.totalWorth = totalWorth;
	}

	public Double getProjectedLiabilities() {
		return projectedLiabilities;
	}

	public void setProjectedLiabilities(Double projectedLiabilities) {
		this.projectedLiabilities = projectedLiabilities;
	}

	public Double getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(Double netWorth) {
		this.netWorth = netWorth;
	}

	public Double getProfit() {
		return profit;
	}

	public void setProfit(Double profit) {
		this.profit = profit;
	}

	public Double getStockNetWorth() {
		return stockNetWorth;
	}

	public void setStockNetWorth(Double stockNetWorth) {
		this.stockNetWorth = stockNetWorth;
	}

	public Double getCash() {
		return cash;
	}

	public void setCash(Double cash) {
		this.cash = cash;
	}

	public Double getProportionOfStock() {
		return proportionOfStock;
	}

	public void setProportionOfStock(Double proportionOfStock) {
		this.proportionOfStock = proportionOfStock;
	}

	public Double getPe() {
		return pe;
	}

	public void setPe(Double pe) {
		this.pe = pe;
	}

	public Double getPb() {
		return pb;
	}

	public void setPb(Double pb) {
		this.pb = pb;
	}

}