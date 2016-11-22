package com.guhe.webclient;

import java.util.LinkedHashMap;
import java.util.Map;

public enum PortfolioDao {
	instance;

	private Map<String, Portfolio> contentProvider = new LinkedHashMap<>();

	private PortfolioDao() {
		Portfolio portfolio = new Portfolio("P00000001", "·¶²ý»¢²âÊÔ×éºÏ1");
		portfolio.setNetWorthPerUnit(1.0235);
		portfolio.setRateOfReturnYear(0.3556);
		portfolio.setTotalWorth(96986);
		portfolio.setProjectedLiabilities(132.7);
		portfolio.setNetWorth(96853.3);
		portfolio.setStockNetWorth(73787.3);
		portfolio.setCash(23066);
		portfolio.setProportionOfStock(0.76);
		contentProvider.put("P00000001", portfolio);
	}

	public Map<String, Portfolio> getModel() {
		return contentProvider;
	}
}
