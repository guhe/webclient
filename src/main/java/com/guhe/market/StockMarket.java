package com.guhe.market;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface StockMarket {

	StockData getStockData(String stockCode);
}
