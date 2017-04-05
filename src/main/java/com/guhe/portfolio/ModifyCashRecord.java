package com.guhe.portfolio;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.guhe.market.MoneyName;

@Entity
public class ModifyCashRecord {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	private Portfolio portfolio;
	
	private MoneyName target;
	private double newAmount;
	private double oldAmount;
	private Date date;

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

	public MoneyName getTarget() {
		return target;
	}

	public void setTarget(MoneyName target) {
		this.target = target;
	}

	public double getNewAmount() {
		return newAmount;
	}

	public void setNewAmount(double newAmount) {
		this.newAmount = newAmount;
	}

	public double getOldAmount() {
		return oldAmount;
	}

	public void setOldAmount(double oldAmount) {
		this.oldAmount = oldAmount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
