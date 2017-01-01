package com.guhe.webclient.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.guhe.portfolio.Holding;
import com.guhe.portfolio.JpaPortfolioManager;
import com.guhe.portfolio.Portfolio;
import com.guhe.portfolio.Stock;
import com.guhe.portfolio.TradeRecord;
import com.guhe.portfolio.TradeRecord.BuyOrSell;
import com.guhe.util.CommonUtil;
import com.guhe.util.DerbyUtil;

public class PortfolioManagerTest {

	private static EntityManagerFactory testEmf;
	private static JpaPortfolioManager pm;

	@BeforeClass
	public static void beforeAll() throws IOException {
		FileUtils.deleteDirectory(new File("test_temp"));
		System.setProperty("derby.stream.error.file", "test_temp/derby.log");

		String jdbcUrl = "jdbc:derby:test_temp/guhe;create=true";
		testEmf = Persistence.createEntityManagerFactory("GUHE",
				Collections.singletonMap("javax.persistence.jdbc.url", jdbcUrl));
	}

	@AfterClass
	public static void afterAll() throws IOException {
		testEmf.close();

		DerbyUtil.closeEmbeddedDatebase("test_temp/guhe");

		FileUtils.deleteDirectory(new File("test_temp"));
	}

	@Before
	public void before() {
		pm = new JpaPortfolioManager(testEmf.createEntityManager());
	}

	@After
	public void after() {
		pm.getEm().close();
	}

	@Test
	public void get_nonexistent_portfolio_by_id() {
		assertNull(pm.getPortfolio("ID999"));
	}

	@Test
	public void save_get_remove_portfolio() {
		Portfolio portfolio = new Portfolio();
		portfolio.setId("ID001");
		portfolio.setName("Test Portfolio Name");
		portfolio.setCash(123.4);
		portfolio.setNumberOfShares(100.0);
		portfolio.setNetWorthPerUnitLastYear(1.1);
		pm.savePortfolio(portfolio);

		Portfolio saved = pm.getPortfolio("ID001");
		assertEquals("ID001", saved.getId());
		assertEquals("Test Portfolio Name", saved.getName());
		assertEquals(123.4, saved.getCash(), 0.000001);
		assertEquals(100.0, saved.getNumberOfShares(), 0.000001);
		assertEquals(1.1, saved.getNetWorthPerUnitLastYear(), 0.000001);

		pm.deletePortfolio("ID001");

		assertNull(pm.getPortfolio("ID001"));
	}

	@Test
	public void test_trade_buy_sell() {
		Portfolio portfolio = new Portfolio();
		portfolio.setId("ID001");
		portfolio.setName("Test Portfolio Name");
		portfolio.setCash(12300.4);
		portfolio.setNumberOfShares(100.0);
		portfolio.setNetWorthPerUnitLastYear(1.1);
		pm.savePortfolio(portfolio);

		pm.saveStock(new Stock("000001", "平安银行"));

		// buy
		pm.trade("ID001", "000001", BuyOrSell.BUY, 8.5, 400, 5, CommonUtil.formatDate("yyyy-MM-dd", "2016-12-14"));

		portfolio = pm.getPortfolio("ID001");
		assertEquals(8895.4, portfolio.getCash(), 0.000001);

		List<Holding> holdings = portfolio.getHoldings();
		assertEquals(1, holdings.size());
		assertEquals("ID001", holdings.get(0).getPortfolio().getId());
		assertEquals("000001", holdings.get(0).getStock().getCode());
		assertEquals(400, holdings.get(0).getAmount());

		List<TradeRecord> tradeRecords = portfolio.getTradeRecords();
		assertEquals(1, tradeRecords.size());
		assertEquals("ID001", tradeRecords.get(0).getPortfolio().getId());
		assertEquals("000001", tradeRecords.get(0).getStock().getCode());
		assertEquals(BuyOrSell.BUY, tradeRecords.get(0).getBuyOrSell());
		assertEquals(400, tradeRecords.get(0).getAmount());
		assertEquals(8.5, tradeRecords.get(0).getPrice(), 0.000001);
		assertEquals(CommonUtil.formatDate("yyyy-MM-dd", "2016-12-14"), tradeRecords.get(0).getDate());

		// sell
		pm.trade("ID001", "000001", BuyOrSell.SELL, 10, 300, 8.3, CommonUtil.formatDate("yyyy-MM-dd", "2016-12-15"));

		portfolio = pm.getPortfolio("ID001");
		assertEquals(11887.1, portfolio.getCash(), 0.000001);

		holdings = portfolio.getHoldings();
		assertEquals(1, holdings.size());
		assertEquals("ID001", holdings.get(0).getPortfolio().getId());
		assertEquals("000001", holdings.get(0).getStock().getCode());
		assertEquals(100, holdings.get(0).getAmount());

		tradeRecords = portfolio.getTradeRecords();
		assertEquals(2, tradeRecords.size());
		assertEquals("ID001", tradeRecords.get(0).getPortfolio().getId());
		assertEquals("000001", tradeRecords.get(0).getStock().getCode());
		assertEquals(BuyOrSell.BUY, tradeRecords.get(0).getBuyOrSell());
		assertEquals(400, tradeRecords.get(0).getAmount());
		assertEquals(8.5, tradeRecords.get(0).getPrice(), 0.000001);
		assertEquals(CommonUtil.formatDate("yyyy-MM-dd", "2016-12-14"), tradeRecords.get(0).getDate());

		assertEquals("ID001", tradeRecords.get(1).getPortfolio().getId());
		assertEquals("000001", tradeRecords.get(1).getStock().getCode());
		assertEquals(BuyOrSell.SELL, tradeRecords.get(1).getBuyOrSell());
		assertEquals(300, tradeRecords.get(1).getAmount());
		assertEquals(10, tradeRecords.get(1).getPrice(), 0.000001);
		assertEquals(CommonUtil.formatDate("yyyy-MM-dd", "2016-12-15"), tradeRecords.get(1).getDate());

		pm.deletePortfolio("ID001");
	}
}
