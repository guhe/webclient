package com.guhe.portfolio;

import java.util.Date;
import java.util.List;

import org.jvnet.hk2.annotations.Contract;

import com.guhe.portfolio.TradeRecord.BuyOrSell;

@Contract
public interface PortfolioManager {

	List<Portfolio> getPortfolios();

	Portfolio getPortfolio(String id);

	void savePortfolio(Portfolio portfolio);

	void deletePortfolio(String id);

	void trade(String portfolioId, String stockCode, BuyOrSell buyOrSell, double price, long amount, double cost,
			Date date);

}
