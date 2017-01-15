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
	
	private double share;

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

	public double getShare() {
		return share;
	}

	public void setShare(double share) {
		this.share = share;
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

	public enum PurchaseOrRedeem {
		PURCHASE, REDEEM
	}
}
