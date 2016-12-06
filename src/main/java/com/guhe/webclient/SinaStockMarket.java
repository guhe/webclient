package com.guhe.webclient;

public class SinaStockMarket implements StockMarket {

	@Override
	public double getPrice(String stockCode) {
		if (stockCode.equals("000001")) {
			return 8.88;
		}
		if (stockCode.equals("601318")) {
			return 32.0;
		}
		return 0;
	}

}
