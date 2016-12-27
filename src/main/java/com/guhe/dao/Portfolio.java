package com.guhe.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Portfolio {

	@Id
	private String id;
	private String name;

	private double numberOfShares;
	private double netWorthPerUnitLastYear;

	private double cash;

	@OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Holding> holdings = new ArrayList<>();

	@OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
	private List<TradeRecord> tradeRecords = new ArrayList<>();
	
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

	public void add(Holding holding) {
		holdings.add(holding);
	}

	public List<Holding> getHoldings() {
		return holdings;
	}

	public List<TradeRecord> getTradeRecords() {
		return tradeRecords;
	}
}
