package com.guhe.portfolio;

import java.util.Date;
import java.util.List;

import org.jvnet.hk2.annotations.Contract;

import com.guhe.market.MoneyName;
import com.guhe.portfolio.TradeRecord.BuyOrSell;

@Contract
public interface PortfolioManager {

	List<Portfolio> getPortfolios();

	Portfolio getPortfolio(String id);

	void savePortfolio(Portfolio portfolio);

	void deletePortfolio(String id);

	void trade(String portfolioId, String stockCode, BuyOrSell buyOrSell, double price, long amount, double fee,
			Date date);

	void purchase(String portfolioId, String holderName, double money, double netWorth, double fee, Date date);

	void redeem(String portfolioId, String holderName, double share, double netWorth, double fee, Date date);

	List<DailyData> getDailyData(String portfolioId, Date startDate, Date endDate);

	void supplementDailyData(String portfolioId, Date endDate);

	void exchangeMoney(String portfolioId, MoneyName target, double exchangeRate, double amount, Date date);
}
