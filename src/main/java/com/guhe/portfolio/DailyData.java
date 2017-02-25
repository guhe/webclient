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

	public Date getDate() {
		return date;
	}

	public double getNetWorthPerUnit() {
		return netWorthPerUnit;
	}

	public double getNetWorth() {
		return netWorth;
	}

	public double getShare() {
		return share;
	}

	public double getPb() {
		return pb;
	}

	public double getPe() {
		return pe;
	}

	public double getProportionOfStock() {
		return proportionOfStock;
	}

	public double getGrowthRate() {
		return growthRate;
	}
}
