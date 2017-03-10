package com.guhe.portfolio.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.guhe.market.MoneyName;
import com.guhe.portfolio.ExchangeMoneyRecord;
import com.guhe.portfolio.Portfolio;
import com.guhe.portfolio.PortfolioException;
import com.guhe.util.CommonUtil;

public class ExchangeMoneyTest extends PortfolioTestBase {

	private Portfolio portfolio;

	@Before
	public void before() {
		super.before();

		portfolio = new Portfolio();
		portfolio.setId("ID001");
		portfolio.setName("Test Portfolio Name");
		portfolio.setRmbCash(8000.0);
		portfolio.setHkdCash(10000.0);
		portfolio.setUsdCash(1000.0);
		portfolio.setNetWorthPerUnitLastYear(1.0);
		portfolio.setCreatedTime(CommonUtil.parseDate("yyyy-MM-dd", "2017-01-10"));

		pm.savePortfolio(portfolio);
	}

	@After
	public void after() {
		pm.deletePortfolio("ID001");

		super.after();
	}

	@Test
	public void exchange_hkd_to_rmb() {
		pm.exchangeMoney("ID001", MoneyName.HKD, -5000, 4000, CommonUtil.parseDate("yyyy-MM-dd", "2017-02-08"));

		portfolio = pm.getPortfolio("ID001");
		assertEquals(12000.0, portfolio.getRmbCash(), 0.000001);
		assertEquals(5000.0, portfolio.getHkdCash(), 0.000001);
		List<ExchangeMoneyRecord> records = portfolio.getExchangeMoneyRecords();
		assertEquals(1, records.size());
		assertThat(records.get(0), MoneyName.HKD, -5000.0, 4000.0, "2017-02-08");
	}

	@Test
	public void exchange_rmb_to_usd() {
		pm.exchangeMoney("ID001", MoneyName.USD, 1000, -7000, CommonUtil.parseDate("yyyy-MM-dd", "2017-02-08"));

		portfolio = pm.getPortfolio("ID001");
		assertEquals(1000.0, portfolio.getRmbCash(), 0.000001);
		assertEquals(2000.0, portfolio.getUsdCash(), 0.000001);
		List<ExchangeMoneyRecord> records = portfolio.getExchangeMoneyRecords();
		assertEquals(1, records.size());
		assertThat(records.get(0), MoneyName.USD, 1000.0, -7000.0, "2017-02-08");
	}

	@Test
	public void exchange_rmb_to_usd_but_rmb_is_not_enough() {
		try {
			pm.exchangeMoney("ID001", MoneyName.USD, 1000, -10000, CommonUtil.parseDate("yyyy-MM-dd", "2017-02-08"));
			fail();
		} catch (PortfolioException e) {
		}

		portfolio = pm.getPortfolio("ID001");
		assertEquals(8000.0, portfolio.getRmbCash(), 0.000001);
		assertEquals(1000.0, portfolio.getUsdCash(), 0.000001);
		List<ExchangeMoneyRecord> records = portfolio.getExchangeMoneyRecords();
		assertEquals(0, records.size());
	}

	@Test
	public void exchange_usd_to_rmb_but_usd_is_not_enough() {
		try {
			pm.exchangeMoney("ID001", MoneyName.USD, -1200, 8000, CommonUtil.parseDate("yyyy-MM-dd", "2017-02-08"));
			fail();
		} catch (PortfolioException e) {
		}

		portfolio = pm.getPortfolio("ID001");
		assertEquals(8000.0, portfolio.getRmbCash(), 0.000001);
		assertEquals(1000.0, portfolio.getUsdCash(), 0.000001);
		List<ExchangeMoneyRecord> records = portfolio.getExchangeMoneyRecords();
		assertEquals(0, records.size());
	}

	@Test(expected = PortfolioException.class)
	public void exchange_but_both_money_is_minus() {
		pm.exchangeMoney("ID001", MoneyName.USD, -100, -800, CommonUtil.parseDate("yyyy-MM-dd", "2017-02-08"));
	}

	@Test(expected = PortfolioException.class)
	public void exchange_but_both_money_is_positive() {
		pm.exchangeMoney("ID001", MoneyName.USD, 100, 800, CommonUtil.parseDate("yyyy-MM-dd", "2017-02-08"));
	}

	@Test(expected = PortfolioException.class)
	public void exchange_but_portfolio_not_exists() {
		pm.exchangeMoney("ID002", MoneyName.USD, -100, 800, CommonUtil.parseDate("yyyy-MM-dd", "2017-02-08"));
	}

	private void assertThat(ExchangeMoneyRecord record, MoneyName target, double targetAmount, double rmbAmount,
			String date) {
		assertEquals(target, record.getTarget());
		assertEquals(targetAmount, record.getTargetAmount(), 0.01);
		assertEquals(rmbAmount, record.getRmbAmount(), 0.01);
		assertEquals(target, record.getTarget());
		CommonUtil.parseDate("yyyy-MM-dd", date);
	}
}
