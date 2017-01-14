package com.guhe.portfolio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class PortfolioHolder {

	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	private Portfolio portfolio;
	
	@ManyToOne
	private Holder holder;
	
	private double share;
	
	private double totalInvestment;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
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

	public double getTotalInvestment() {
		return totalInvestment;
	}

	public void setTotalInvestment(double totalInvestment) {
		this.totalInvestment = totalInvestment;
	}
}
