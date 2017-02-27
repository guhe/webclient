package com.guhe.portfolio;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"portfolio_id", "date"})
})
public class DailyData {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	private Portfolio portfolio;

	private Date date;

	private double netWorthPerUnit;

	private double netWorth;

	private double share;

	private double pb;

	private double pe;

	private double proportionOfStock;
	
	private double growthRate;

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getNetWorthPerUnit() {
		return netWorthPerUnit;
	}

	public void setNetWorthPerUnit(double netWorthPerUnit) {
		this.netWorthPerUnit = netWorthPerUnit;
	}

	public double getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(double netWorth) {
		this.netWorth = netWorth;
	}

	public double getShare() {
		return share;
	}

	public void setShare(double share) {
		this.share = share;
	}

	public double getPb() {
		return pb;
	}

	public void setPb(double pb) {
		this.pb = pb;
	}

	public double getPe() {
		return pe;
	}

	public void setPe(double pe) {
		this.pe = pe;
	}

	public double getProportionOfStock() {
		return proportionOfStock;
	}

	public void setProportionOfStock(double proportionOfStock) {
		this.proportionOfStock = proportionOfStock;
	}

	public double getGrowthRate() {
		return growthRate;
	}

	public void setGrowthRate(double growthRate) {
		this.growthRate = growthRate;
	}

}
