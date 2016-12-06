package com.guhe.webclient;

public class Holding {
	private Stock stock;
	private int amount;

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
