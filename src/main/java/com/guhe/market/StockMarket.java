package com.guhe.market;

import java.util.Calendar;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface StockMarket {

	StockData getStockData(String stockCode, Calendar day);

	boolean isOpen(Calendar day);
}
