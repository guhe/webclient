package com.guhe.webclient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Portfolio {
	public static final double RATE_TAX = 0.001;
	public static final double RATE_COMMISSION = 0.00025;

	private String id;
	private String name;

	private double numberOfShares;
	private double netWorthPerUnitLastYear;

	private double cash;
	private LinkedHashMap<Stock, Holding> holdings = new LinkedHashMap<>();

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

	public double getNumberOfShares() {
		return numberOfShares;
	}

	public void setNumberOfShares(double numberOfShares) {
		this.numberOfShares = numberOfShares;
	}

	public double getNetWorthPerUnitLastYear() {
		return netWorthPerUnitLastYear;
	}

	public void setNetWorthPerUnitLastYear(double netWorthPerUnitLastYear) {
		this.netWorthPerUnitLastYear = netWorthPerUnitLastYear;
	}

	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	public void add(Stock stock, Holding holding) {
		holdings.put(stock, holding);
	}

	public List<Holding> getHoldings() {
		return holdings.values().stream().collect(Collectors.toList());
	}
}
