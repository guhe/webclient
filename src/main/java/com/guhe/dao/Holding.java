package com.guhe.dao;

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

	private int amount;

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

	public int getAmount() {
		return amount;
	}
}
