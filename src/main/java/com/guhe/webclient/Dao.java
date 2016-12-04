package com.guhe.webclient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Dao {
	instance;

	private Map<String, PortfolioViewData> portfolios = new LinkedHashMap<>();
	private Map<String, List<HoldingStockViewData>> holdingstocks = new LinkedHashMap<>();

	private Dao() {
		PortfolioViewData portfolio = new PortfolioViewData();
		portfolio.setId("P00000001");
		portfolio.setName("范昌虎测试组合1");
		portfolio.setNetWorthPerUnit(1.0235);
		portfolio.setRateOfReturnYear(0.3556);
		portfolio.setTotalWorth(96986);
		portfolio.setProjectedLiabilities(132.7);
		portfolio.setNetWorth(96853.3);
		portfolio.setStockNetWorth(73787.3);
		portfolio.setCash(23066);
		portfolio.setProportionOfStock(0.76);
		portfolios.put("P00000001", portfolio);
		holdingstocks.put("P00000001", new ArrayList<>());

		PortfolioViewData portfolio2 = new PortfolioViewData();
		portfolio2.setId("P00000002");
		portfolio2.setName("范昌虎测试组合2");
		portfolio2.setNetWorthPerUnit(1.0235);
		portfolio2.setRateOfReturnYear(0.3556);
		portfolio2.setTotalWorth(96986);
		portfolio2.setProjectedLiabilities(132.7);
		portfolio2.setNetWorth(96853.3);
		portfolio2.setStockNetWorth(73787.3);
		portfolio2.setCash(23066);
		portfolio2.setProportionOfStock(0.76);
		portfolios.put("P00000002", portfolio2);
		holdingstocks.put("P00000002", new ArrayList<>());
		
		HoldingStockViewData stock1 = new HoldingStockViewData();
		stock1.setName("平安银行");
		stock1.setCode("000001");
		stock1.setAmount(4000);
		stock1.setCurPrice(8.88);
		stock1.setMarketWorth(35520);
		stock1.setEstimatedCommission(56.23);
		stock1.setEstimatedTax(10);
		stock1.setNetWorth(35453.77);
		stock1.setProportion(0.366);
		holdingstocks.get("P00000001").add(stock1);

		HoldingStockViewData stock2 = new HoldingStockViewData();
		stock2.setName("中国平安");
		stock2.setCode("601318");
		stock2.setAmount(1200);
		stock2.setCurPrice(32.00);
		stock2.setMarketWorth(38400);
		stock2.setEstimatedCommission(55.97);
		stock2.setEstimatedTax(10.5);
		stock2.setNetWorth(38333.53);
		stock2.setProportion(0.396);
		holdingstocks.get("P00000001").add(stock2);
	}

	public List<PortfolioViewData> getPortfolios() {
		return portfolios.values().stream().collect(Collectors.toList());
	}

	public PortfolioViewData getPortfolio(String id) {
		return portfolios.get(id);
	}

	public List<HoldingStockViewData> getHoldingStocks(String portfolioId) {
		return holdingstocks.get(portfolioId);
	}
}
