package com.guhe.webclient;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface StockMarket {

	double getPrice(String stockCode);
}