package com.guhe.market;

import java.util.Calendar;
import java.util.Map;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface StockMarket {

	StockData getStockData(String stockCode, Calendar day);

	boolean isOpen(Calendar day);
	
	Map<String, StockData> getDailyStockData(String stockFullCode, Calendar from, Calendar to);

	double getTotalBuyFee(String stockCode, int count, double price);

	double getTotalSellFee(String stockCode, int count, double price);
}
