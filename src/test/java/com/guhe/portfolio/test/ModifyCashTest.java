package com.guhe.portfolio.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.guhe.market.MoneyName;
import com.guhe.portfolio.ModifyCashRecord;
import com.guhe.portfolio.Portfolio;
import com.guhe.portfolio.PortfolioException;
import com.guhe.util.CommonUtil;

public class ModifyCashTest extends PortfolioTestBase {

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
	public void modify_rmb() {
		pm.modifyCash("ID001", MoneyName.RMB, 8002.0, CommonUtil.parseDate("yyyy-MM-dd", "2017-02-08"));

		portfolio = pm.getPortfolio("ID001");
		assertEquals(8002.0, portfolio.getRmbCash(), 0.000001);
		List<ModifyCashRecord> records = portfolio.getModifyCashRecords();
		assertEquals(1, records.size());
		assertThat(records.get(0), MoneyName.RMB, 8002.0, 8000.0, "2017-02-08");
	}

	@Test(expected = PortfolioException.class)
	public void portfolio_not_exists() {
		pm.modifyCash("ID002", MoneyName.RMB, -100.0, CommonUtil.parseDate("yyyy-MM-dd", "2017-02-08"));
	}
	
	@Test(expected = PortfolioException.class)
	public void modify_rmb_below_zero() {
		pm.modifyCash("ID001", MoneyName.RMB, -100.0, CommonUtil.parseDate("yyyy-MM-dd", "2017-02-08"));
	}

	private void assertThat(ModifyCashRecord record, MoneyName target, double newAmount, double oldAmount,
			String date) {
		assertEquals(target, record.getTarget());
		assertEquals(newAmount, record.getNewAmount(), 0.01);
		assertEquals(oldAmount, record.getOldAmount(), 0.01);
		assertEquals(CommonUtil.parseDate("yyyy-MM-dd", date), record.getDate());
	}

}
