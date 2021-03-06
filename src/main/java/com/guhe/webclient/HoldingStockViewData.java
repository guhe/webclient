package com.guhe.webclient;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "holdingStock")
class HoldingStockViewData {
	private String name;
	private String code;
	private long amount;
	private double curPrice;
	private double marketWorth;
	private double estimatedCommission;
	private double estimatedTax;
	private double netWorth;
	private double proportion;
	private List<TradeRecordViewData> tradeRecords;
	private double nextBuyPrice;
	private double nextSellPrice;

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

	public Double getCurPrice() {
		return curPrice;
	}

	public void setCurPrice(Double curPrice) {
		this.curPrice = curPrice;
	}

	public Double getMarketWorth() {
		return marketWorth;
	}

	public void setMarketWorth(Double marketWorth) {
		this.marketWorth = marketWorth;
	}

	public Double getEstimatedCommission() {
		return estimatedCommission;
	}

	public void setEstimatedCommission(Double estimatedCommission) {
		this.estimatedCommission = estimatedCommission;
	}

	public Double getEstimatedTax() {
		return estimatedTax;
	}

	public void setEstimatedTax(Double estimatedTax) {
		this.estimatedTax = estimatedTax;
	}

	public Double getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(Double netWorth) {
		this.netWorth = netWorth;
	}

	public Double getProportion() {
		return proportion;
	}

	public void setProportion(Double proportion) {
		this.proportion = proportion;
	}

	public List<TradeRecordViewData> getTradeRecords() {
		return tradeRecords;
	}

	public void setTradeRecords(List<TradeRecordViewData> tradeRecords) {
		this.tradeRecords = tradeRecords;
	}

	public Double getNextBuyPrice() {
		return nextBuyPrice;
	}

	public void setNextBuyPrice(Double nextBuyPrice) {
		this.nextBuyPrice = nextBuyPrice;
	}

	public Double getNextSellPrice() {
		return nextSellPrice;
	}

	public void setNextSellPrice(Double nextSellPrice) {
		this.nextSellPrice = nextSellPrice;
	}
}