package com.guhe.portfolio.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.guhe.portfolio.Holding;
import com.guhe.portfolio.Portfolio;
import com.guhe.portfolio.PortfolioException;
import com.guhe.portfolio.PortfolioHolder;
import com.guhe.portfolio.Stock;
import com.guhe.portfolio.TradeRecord;
import com.guhe.portfolio.TradeRecord.BuyOrSell;
import com.guhe.util.CommonUtil;

public class TradeTest extends PortfolioTestBase {

	private Portfolio portfolio;

	@BeforeClass
	public static void beforeAll() throws IOException {
		PortfolioTestBase.beforeAll();

		EntityManager em = testEmf.createEntityManager();
		em.getTransaction().begin();
		em.persist(new Stock("000001", "平安银行"));
		em.getTransaction().commit();
		em.close();
	}

	@Before
	public void before() {
		super.before();

		portfolio = new Portfolio();
		portfolio.setId("ID001");
		portfolio.setName("Test Portfolio Name");
		portfolio.setCash(12300.4);
		portfolio.setNetWorthPerUnitLastYear(1.1);

		PortfolioHolder holder = new PortfolioHolder();
		holder.setShare(100.0);
		holder.setTotalInvestment(110);
		portfolio.add(holder);

		pm.savePortfolio(portfolio);
	}

	@After
	public void after() {
		pm.deletePortfolio("ID001");

		super.after();
	}

	@Test
	public void test_trade_buy_sell() {
		// buy 400
		pm.trade("ID001", "000001", BuyOrSell.BUY, 8.5, 400, 5.0, CommonUtil.parseDate("yyyy-MM-dd", "2016-12-14"));

		portfolio = pm.getPortfolio("ID001");
		assertEquals(8895.4, portfolio.getCash(), 0.000001);

		List<Holding> holdings = portfolio.getHoldings();
		assertEquals(1, holdings.size());
		assertThat(holdings.get(0), "ID001", "000001", 400);

		// sell 300
		pm.trade("ID001", "000001", BuyOrSell.SELL, 10, 300, 8.3, CommonUtil.parseDate("yyyy-MM-dd", "2016-12-15"));

		portfolio = pm.getPortfolio("ID001");
		assertEquals(11887.1, portfolio.getCash(), 0.000001);

		holdings = portfolio.getHoldings();
		assertEquals(1, holdings.size());
		assertThat(holdings.get(0), "ID001", "000001", 100);
		
		// sell 100
		pm.trade("ID001", "000001", BuyOrSell.SELL, 10, 100, 6.5, CommonUtil.parseDate("yyyy-MM-dd", "2016-12-16"));
		
		portfolio = pm.getPortfolio("ID001");
		assertEquals(12880.6, portfolio.getCash(), 0.000001);
		
		holdings = portfolio.getHoldings();
		assertEquals(0, holdings.size());

		List<TradeRecord> tradeRecords = portfolio.getTradeRecords();
		assertEquals(3, tradeRecords.size());
		assertThat(tradeRecords.get(0), "ID001", "000001", BuyOrSell.BUY, 400, 8.5, 5.0, "2016-12-14");
		assertThat(tradeRecords.get(1), "ID001", "000001", BuyOrSell.SELL, 300, 10.0, 8.3, "2016-12-15");
		assertThat(tradeRecords.get(2), "ID001", "000001", BuyOrSell.SELL, 100, 10.0, 6.5, "2016-12-16");
	}

	@Test(expected = PortfolioException.class)
	public void test_trade_with_nonexistent_portfolio() {
		pm.trade("ID002", "000001", BuyOrSell.BUY, 8.5, 400, 5, CommonUtil.parseDate("yyyy-MM-dd", "2016-12-14"));
	}

	@Test(expected = PortfolioException.class)
	public void test_trade_with_nonexistent_stock() {
		pm.trade("ID001", "000002", BuyOrSell.BUY, 8.5, 400, 5, CommonUtil.parseDate("yyyy-MM-dd", "2016-12-14"));
	}

	@Test(expected = PortfolioException.class)
	public void test_trade_with_no_enough_money_to_buy() {
		pm.trade("ID001", "000001", BuyOrSell.BUY, 8.5, 4000, 5, CommonUtil.parseDate("yyyy-MM-dd", "2016-12-14"));
	}

	@Test(expected = PortfolioException.class)
	public void test_trade_with_no_such_holding_to_sell() {
		pm.trade("ID001", "000001", BuyOrSell.SELL, 8.5, 400, 5, CommonUtil.parseDate("yyyy-MM-dd", "2016-12-14"));
	}

	@Test(expected = PortfolioException.class)
	public void test_trade_with_no_enough_holding_to_sell() {
		pm.getEm().getTransaction().begin();
		Holding holding = new Holding();
		holding.setPortfolio(portfolio);
		holding.setStock(pm.getStockByCode("000001"));
		holding.setAmount(300);
		pm.getEm().persist(holding);
		pm.getEm().getTransaction().commit();

		pm.trade("ID001", "000001", BuyOrSell.SELL, 8.5, 400, 5, CommonUtil.parseDate("yyyy-MM-dd", "2016-12-14"));
	}

	private void assertThat(Holding holding, String protfolioId, String stockCode, int amount) {
		assertEquals(protfolioId, holding.getPortfolio().getId());
		assertEquals(stockCode, holding.getStock().getCode());
		assertEquals(amount, holding.getAmount());
	}

	private void assertThat(TradeRecord tradeRecord, String protfolioId, String stockCode, BuyOrSell buyOrSell,
			long amount, double price, double fee, String date) {
		assertEquals(protfolioId, tradeRecord.getPortfolio().getId());
		assertEquals(stockCode, tradeRecord.getStock().getCode());
		assertEquals(buyOrSell, tradeRecord.getBuyOrSell());
		assertEquals(amount, tradeRecord.getAmount());
		assertEquals(price, tradeRecord.getPrice(), 0.000001);
		assertEquals(fee, tradeRecord.getFee(), 0.000001);
		assertEquals(CommonUtil.parseDate("yyyy-MM-dd", date), tradeRecord.getDate());
	}
}
