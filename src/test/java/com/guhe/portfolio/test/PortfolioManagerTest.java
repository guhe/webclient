package com.guhe.portfolio.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.guhe.portfolio.Portfolio;

public class PortfolioManagerTest extends PortfolioTestBase {

	@Test
	public void save_get_remove_portfolio() {
		Portfolio portfolio = new Portfolio();
		portfolio.setId("ID001");
		portfolio.setName("Test Portfolio Name");
		portfolio.setCash(123.4);
		portfolio.setNetWorthPerUnitLastYear(1.1);
		pm.savePortfolio(portfolio);

		Portfolio saved = pm.getPortfolio("ID001");
		assertEquals("ID001", saved.getId());
		assertEquals("Test Portfolio Name", saved.getName());
		assertEquals(123.4, saved.getCash(), 0.000001);
		assertEquals(1.1, saved.getNetWorthPerUnitLastYear(), 0.000001);

		pm.deletePortfolio("ID001");

		assertNull(pm.getPortfolio("ID001"));
	}

	@Test
	public void get_nonexistent_portfolio_by_id() {
		assertNull(pm.getPortfolio("ID001"));
	}
}
