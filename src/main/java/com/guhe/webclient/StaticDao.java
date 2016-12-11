package com.guhe.webclient;

import java.util.Arrays;
import java.util.List;

public class StaticDao implements Dao {

	private Portfolio portfolio;

	private Stock PING_AN_YIN_HANG = new Stock("000001", "平安银行");
	private Stock ZHONG_GUO_PING_AN = new Stock("601318", "中国平安");

	public StaticDao() {
		portfolio = new Portfolio();
		portfolio.setId("P00000001");
		portfolio.setName("范昌虎测试组合1");
		portfolio.setCash(23066);
		portfolio.setNumberOfShares(150000);
		portfolio.setNetWorthPerUnitLastYear(1.0822);
		portfolio.add(PING_AN_YIN_HANG, new Holding(PING_AN_YIN_HANG, 5800));
		portfolio.add(ZHONG_GUO_PING_AN, new Holding(ZHONG_GUO_PING_AN, 3600));
	}

	public List<Portfolio> getPortfolios() {
		return Arrays.asList(portfolio);
	}

	public Portfolio getPortfolio(String id) {
		return portfolio.getId().equals(id) ? portfolio : null;
	}
}
