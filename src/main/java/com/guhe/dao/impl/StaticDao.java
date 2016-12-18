package com.guhe.dao.impl;

import java.util.Arrays;
import java.util.List;

import com.guhe.dao.Dao;
import com.guhe.dao.Holding;
import com.guhe.dao.Portfolio;
import com.guhe.dao.Stock;

public class StaticDao implements Dao {

	private Portfolio portfolio;

	private Stock ZHONG_GUO_PING_AN = new Stock("601318", "中国平安");
	private Stock ZHAO_SHANG_YIN_HANG = new Stock("600036", "招商银行");
	private Stock CHANG_AN_QI_CHE = new Stock("000625", "长安汽车");
	private Stock SHUANG_LU_YAO_YE = new Stock("002038", "双鹭药业");
	private Stock PING_AN_YIN_HANG = new Stock("000001", "平安银行");

	public StaticDao() {
		portfolio = new Portfolio();
		portfolio.setId("P00000001");
		portfolio.setName("范昌虎测试组合1");
		portfolio.setCash(1160.11 + 1032.36);
		portfolio.setNumberOfShares(176350);
		portfolio.setNetWorthPerUnitLastYear(1);
		portfolio.add(new Holding(ZHONG_GUO_PING_AN, 1500));
		portfolio.add(new Holding(ZHAO_SHANG_YIN_HANG, 2500));
		portfolio.add(new Holding(CHANG_AN_QI_CHE, 3600));
		portfolio.add(new Holding(SHUANG_LU_YAO_YE, 1500));
		portfolio.add(new Holding(PING_AN_YIN_HANG, 1000));
	}

	public List<Portfolio> getPortfolios() {
		return Arrays.asList(portfolio);
	}

	public Portfolio getPortfolio(String id) {
		return portfolio.getId().equals(id) ? portfolio : null;
	}
}
