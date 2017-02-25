package com.guhe.portfolio;

import java.util.ArrayList;
import java.util.Date;
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

	private double netWorthPerUnitLastYear;

	private double cash;
	
	private Date createdTime;

	@OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Holding> holdings = new ArrayList<>();

	@OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PortfolioHolder> holders = new ArrayList<>();

	@OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TradeRecord> tradeRecords = new ArrayList<>();

	@OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PurchaseRedeemRecord> purchaseRedeemRecords = new ArrayList<>();
	
	@OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DailyData> historyNetWorthPerUnits = new ArrayList<>();

	public Portfolio() {
	}

	public Portfolio(String id) {
		this.id = id;
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

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public void add(Holding holding) {
		holdings.add(holding);
	}

	public List<Holding> getHoldings() {
		return holdings;
	}
	
	public void add(PortfolioHolder holder){
		holders.add(holder);
	}

	public List<PortfolioHolder> getHolders() {
		return holders;
	}

	public void add(TradeRecord tradeRecord) {
		tradeRecords.add(tradeRecord);
	}

	public List<TradeRecord> getTradeRecords() {
		return tradeRecords;
	}
	
	public void add(PurchaseRedeemRecord prRecord){
		purchaseRedeemRecords.add(prRecord);
	}

	public List<PurchaseRedeemRecord> getPurchaseRedeemRecords() {
		return purchaseRedeemRecords;
	}

	public List<DailyData> getHistoryNetWorthPerUnits() {
		return historyNetWorthPerUnits;
	}
}
