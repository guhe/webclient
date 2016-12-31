package com.guhe.portfolio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Holding {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	private Stock stock;
	
	@ManyToOne
	private Portfolio portfolio;

	private long amount;

	public Holding() {

	}

	public Holding(Stock stock, int amount) {
		super();
		this.stock = stock;
		this.amount = amount;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

}
