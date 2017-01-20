package com.guhe.webclient;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "purchaseRedeem")
class PurchaseRedeemViewData {
	private String purchaseOrRedeem;
	private String holder;
	private double share;
	private double netWorth;
	private double money;
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

	public Double getShare() {
		return share;
	}

	public void setShare(Double share) {
		this.share = share;
	}

	public Double getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(Double netWorth) {
		this.netWorth = netWorth;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}