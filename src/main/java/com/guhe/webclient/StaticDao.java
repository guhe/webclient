package com.guhe.webclient;

import java.util.Arrays;
import java.util.List;

public class StaticDao implements Dao {

	private Portfolio portfolio;

	private Stock ZHONG_GUO_PING_AN = new Stock("601318", "中国平安");
	private Stock HAI_LAN_ZHI_JIA = new Stock("600398", "海澜之家");
	private Stock XIN_LI_TAI = new Stock("002294", "信立泰");
	private Stock SHUANG_LU_YAO_YE = new Stock("002038", "双鹭药业");
	private Stock ZHAO_SHANG_YIN_HANG = new Stock("600036", "招商银行");
	private Stock CHANG_AN_QI_CHE = new Stock("000625", "长安汽车");
	private Stock PING_AN_YIN_HANG = new Stock("000001", "平安银行");

	public StaticDao() {
		portfolio = new Portfolio();
		portfolio.setId("P00000001");
		portfolio.setName("范昌虎测试组合1");
		portfolio.setCash(18888.05 + 78.48);
		portfolio.setNumberOfShares(166005);
		portfolio.setNetWorthPerUnitLastYear(1);
		portfolio.add(ZHONG_GUO_PING_AN, new Holding(ZHONG_GUO_PING_AN, 900));
		portfolio.add(HAI_LAN_ZHI_JIA, new Holding(HAI_LAN_ZHI_JIA, 1000));
		portfolio.add(XIN_LI_TAI, new Holding(XIN_LI_TAI, 400));
		portfolio.add(SHUANG_LU_YAO_YE, new Holding(SHUANG_LU_YAO_YE, 1100));
		portfolio.add(ZHAO_SHANG_YIN_HANG, new Holding(ZHAO_SHANG_YIN_HANG, 1500));
		portfolio.add(CHANG_AN_QI_CHE, new Holding(CHANG_AN_QI_CHE, 3600));
		portfolio.add(PING_AN_YIN_HANG, new Holding(PING_AN_YIN_HANG, 1000));
	}

	public List<Portfolio> getPortfolios() {
		return Arrays.asList(portfolio);
	}

	public Portfolio getPortfolio(String id) {
		return portfolio.getId().equals(id) ? portfolio : null;
	}
}
