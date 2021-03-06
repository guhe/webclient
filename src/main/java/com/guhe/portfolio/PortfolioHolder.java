package com.guhe.portfolio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "portfolio_id", "holder_id" }) })
public class PortfolioHolder implements Cloneable {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	private Portfolio portfolio;

	@ManyToOne
	private Holder holder;

	private double share;

	private double totalInvestment;

	public void addShare(double newShare) {
		share += newShare;
	}

	public void addInvestment(double newInvestment) {
		totalInvestment += newInvestment;
	}

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

	protected PortfolioHolder clone() {
		try {
			return (PortfolioHolder) super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}
}
