package com.guhe.portfolio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "portfolio_id", "stock_id" }) })
public class Holding implements Cloneable {

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
	
	public void addAmount(long newAmount){
		amount += newAmount;
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

	protected Holding clone() {
		try {
			return (Holding) super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}
}
