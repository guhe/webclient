package com.guhe.portfolio;

import java.util.Date;

import org.jvnet.hk2.annotations.Contract;

import com.guhe.portfolio.TradeRecord.BuyOrSell;

@Contract
public interface PortfolioManager {

	Portfolio getPortfolio(String id);

	void savePortfolio(Portfolio portfolio);

	void deletePortfolio(String id);

	void saveStock(Stock stock);
	
	void trade(String portfolioId, String stockCode, BuyOrSell buyOrSell, double price, long amount, double cost,
			Date date);
}
