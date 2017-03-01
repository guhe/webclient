package com.guhe.portfolio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Portfolio implements Cloneable {

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
	private List<DailyData> dailyDatas = new ArrayList<>();

	public Portfolio() {
	}

	public Portfolio(String id) {
		this.id = id;
	}
	
	public void addCash(double newCash){
		cash += newCash;
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

	public void add(PortfolioHolder holder) {
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

	public void add(PurchaseRedeemRecord prRecord) {
		purchaseRedeemRecords.add(prRecord);
	}

	public List<PurchaseRedeemRecord> getPurchaseRedeemRecords() {
		return purchaseRedeemRecords;
	}
	
	public List<DailyData> getDailyDatas() {
		return dailyDatas;
	}

	@Override
	protected Portfolio clone() {
		try {
			Portfolio cloned = (Portfolio) super.clone();
			cloned.setCreatedTime((Date) createdTime.clone());

			cloned.holders = holders.stream().map(e -> {
				PortfolioHolder ph = e.clone();
				ph.setPortfolio(cloned);
				return ph;
			}).collect(Collectors.toList());

			cloned.holdings = holdings.stream().map(e -> {
				Holding hg = e.clone();
				hg.setPortfolio(cloned);
				return hg;
			}).collect(Collectors.toList());

			cloned.purchaseRedeemRecords = null;
			cloned.tradeRecords = null;

			return cloned;
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}
}
