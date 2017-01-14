package com.guhe.webclient;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "portfolioHolder")
class PortfolioHolderViewData {
	private String name;
	private double share;
	private double netWorth;
	private double totalInvestment;
	private double rateOfReturn;
	private double proportion;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Double getTotalInvestment() {
		return totalInvestment;
	}

	public void setTotalInvestment(Double totalInvestment) {
		this.totalInvestment = totalInvestment;
	}

	public Double getRateOfReturn() {
		return rateOfReturn;
	}

	public void setRateOfReturn(Double rateOfReturn) {
		this.rateOfReturn = rateOfReturn;
	}

	public Double getProportion() {
		return proportion;
	}

	public void setProportion(Double proportion) {
		this.proportion = proportion;
	}
}