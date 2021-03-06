package com.guhe.portfolio;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.guhe.market.MoneyName;

@Entity
public class ExchangeMoneyRecord {

	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	private Portfolio portfolio;

	@Enumerated(EnumType.STRING)
	private MoneyName target;
	
	private double targetAmount;
	
	private double rmbAmount;
	
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

	public double getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(double targetAmount) {
		this.targetAmount = targetAmount;
	}

	public double getRmbAmount() {
		return rmbAmount;
	}

	public void setRmbAmount(double rmbAmount) {
		this.rmbAmount = rmbAmount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
