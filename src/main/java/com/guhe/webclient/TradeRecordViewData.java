package com.guhe.webclient;

import javax.xml.bind.annotation.XmlRootElement;

import com.guhe.util.Reflector;

@XmlRootElement(name = "tradeRecord")
class TradeRecordViewData {
	private String buyOrSell;
	private String stockName;
	private String stockCode;
	private long amount;
	private double price;
	private double fee;
	private double exRate = 1.0;
	private String date;
	private String note;

	public String getBuyOrSell() {
		return buyOrSell;
	}

	public void setBuyOrSell(String buyOrSell) {
		this.buyOrSell = buyOrSell;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public double getExRate() {
		return exRate;
	}

	public void setExRate(double exRate) {
		this.exRate = exRate;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return Reflector.toStringByAllFields(this);
	}
}