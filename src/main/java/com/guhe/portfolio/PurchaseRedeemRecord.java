package com.guhe.portfolio;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class PurchaseRedeemRecord {

	@Id
	@GeneratedValue
	private int id;

	@ManyToOne
	private Portfolio portfolio;

	@Enumerated(EnumType.STRING)
	private PurchaseOrRedeem purchaseOrRedeem;
	
	@ManyToOne
	private Holder holder;
	
	private long amount;

	private double netWorth;

	private double fee;

	private Date date;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public PurchaseOrRedeem getPurchaseOrRedeem() {
		return purchaseOrRedeem;
	}

	public void setPurchaseOrRedeem(PurchaseOrRedeem purchaseOrRedeem) {
		this.purchaseOrRedeem = purchaseOrRedeem;
	}

	public Holder getHolder() {
		return holder;
	}

	public void setHolder(Holder holder) {
		this.holder = holder;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public double getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(double netWorth) {
		this.netWorth = netWorth;
	}

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	enum PurchaseOrRedeem {
		PURCHASE, REDEEM
	}
}
