package com.guhe.dao;

import java.util.Date;

import org.jvnet.hk2.annotations.Contract;

import com.guhe.dao.TradeRecord.BuyOrSell;

@Contract
public interface Dao {

	Portfolio getPortfolio(String id);
	
	void createPortfolio(Portfolio portfolio);
	
	void deletePortfolio(String id);

	void trade(String portfolioId, String stockCode, BuyOrSell buyOrSell, double price, long amount, double cost, Date date);
}
