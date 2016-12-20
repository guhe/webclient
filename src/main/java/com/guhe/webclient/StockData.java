package com.guhe.webclient;

import com.guhe.util.Reflector;

public class StockData {
	double price;
	double pe;
	double pb;

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPe() {
		return pe;
	}

	public void setPe(double pe) {
		this.pe = pe;
	}

	public double getPb() {
		return pb;
	}

	public void setPb(double pb) {
		this.pb = pb;
	}

	@Override
	public String toString() {
		return Reflector.toStringByAllFields(this);
	}

}