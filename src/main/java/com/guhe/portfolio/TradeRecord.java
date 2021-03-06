package com.guhe.portfolio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TradeRecord {

	@Id
	@GeneratedValue
	private int id;

	@ManyToOne
	private Stock stock;

	@ManyToOne
	private Portfolio portfolio;

	@Enumerated(EnumType.STRING)
	private BuyOrSell buyOrSell;

	private long amount;

	private double price;

	private double fee;

	@Column(name = "EXRATE", columnDefinition = "double default 1.0")
	private double exRate = 1.0;

	private Date date;

	private String note;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public BuyOrSell getBuyOrSell() {
		return buyOrSell;
	}

	public void setBuyOrSell(BuyOrSell buyOrSell) {
		this.buyOrSell = buyOrSell;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public double getExRate() {
		return exRate;
	}

	public void setExRate(double exRate) {
		this.exRate = exRate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public enum BuyOrSell {
		BUY, SELL
	}
}
