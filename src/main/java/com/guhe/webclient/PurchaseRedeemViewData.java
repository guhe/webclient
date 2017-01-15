package com.guhe.webclient;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "purchaseRedeem")
class PurchaseRedeemViewData {
	private String purchaseOrRedeem;
	private String holder;
	private double share;
	private double netWorth;
	private double fee;
	private String date;

	public String getPurchaseOrRedeem() {
		return purchaseOrRedeem;
	}

	public void setPurchaseOrRedeem(String purchaseOrRedeem) {
		this.purchaseOrRedeem = purchaseOrRedeem;
	}

	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}