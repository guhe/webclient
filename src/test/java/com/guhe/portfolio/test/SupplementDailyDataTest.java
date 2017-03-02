package com.guhe.portfolio.test;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.guhe.market.StockData;
import com.guhe.market.StockMarket;
import com.guhe.portfolio.DailyData;
import com.guhe.portfolio.Holder;
import com.guhe.portfolio.Holding;
import com.guhe.portfolio.Portfolio;
import com.guhe.portfolio.PortfolioException;
import com.guhe.portfolio.PortfolioHolder;
import com.guhe.portfolio.Stock;
import com.guhe.portfolio.TradeRecord.BuyOrSell;
import com.guhe.util.CommonUtil;

public class SupplementDailyDataTest extends PortfolioTestBase {

	private StockMarket market;

	Portfolio portfolio;

	@BeforeClass
	public static void beforeAll() throws IOException {
		PortfolioTestBase.beforeAll();

		EntityManager em = testEmf.createEntityManager();
		em.getTransaction().begin();
		em.persist(new Stock("000001", "平安银行"));
		em.persist(new Holder("Tiger"));
		em.getTransaction().commit();
		em.close();
	}

	@Before
	public void before() {
		super.before();

		market = mock(StockMarket.class);
		pm.setMarket(market);

		portfolio = new Portfolio();
		portfolio.setId("ID001");
		portfolio.setName("Test Portfolio Name");
		portfolio.setCash(8000);
		portfolio.setNetWorthPerUnitLastYear(1.0);
		portfolio.setCreatedTime(CommonUtil.parseDate("yyyy-MM-dd", "2017-01-10"));

		PortfolioHolder holder = new PortfolioHolder();
		holder.setPortfolio(portfolio);
		holder.setHolder(pm.getHolderByName("Tiger"));
		holder.setShare(10000.0);
		holder.setTotalInvestment(10000);
		portfolio.add(holder);

		Holding holding = new Holding();
		holding.setPortfolio(portfolio);
		holding.setStock(pm.getStockByCode("000001"));
		holding.setAmount(500);
		portfolio.add(holding);

		pm.savePortfolio(portfolio);
	}

	@After
	public void after() {
		pm.deletePortfolio("ID001");

		super.after();
	}

	@Test
	public void save_get_remove_portfolio() {
		pm.trade("ID001", "000001", BuyOrSell.BUY, 5, 500, 50, CommonUtil.parseDate("yyyy-MM-dd", "2017-01-10"));
		pm.purchase("ID001", "Tiger", 10100, 1.0, 100, CommonUtil.parseDate("yyyy-MM-dd", "2017-01-10"));

		pm.trade("ID001", "000001", BuyOrSell.SELL, 8, 500, 50, CommonUtil.parseDate("yyyy-MM-dd", "2017-01-11"));
		pm.redeem("ID001", "Tiger", 10000, 1.0, 200, CommonUtil.parseDate("yyyy-MM-dd", "2017-01-11"));

		pm.trade("ID001", "000001", BuyOrSell.BUY, 5, 500, 50, CommonUtil.parseDate("yyyy-MM-dd", "2017-01-13"));
		pm.redeem("ID001", "Tiger", 5000, 1.0, 100, CommonUtil.parseDate("yyyy-MM-dd", "2017-01-13"));

		when(market.getStockData("000001", getDay("2017-01-10"))).thenReturn(new StockData(6, 0, 0));
		when(market.getStockData("000001", getDay("2017-01-11"))).thenReturn(new StockData(7, 0, 0));
		when(market.getStockData("000001", getDay("2017-01-12"))).thenReturn(new StockData(8, 0, 0));
		when(market.getStockData("000001", getDay("2017-01-13"))).thenReturn(new StockData(9, 0, 0));

		pm.supplementDailyData("ID001", CommonUtil.parseDate("yyyy-MM-dd", "2017-01-13"));

		List<DailyData> dailyDatas = pm.getDailyData("ID001", CommonUtil.parseDate("yyyy-MM-dd", "2017-01-10"),
				CommonUtil.parseDate("yyyy-MM-dd", "2017-01-13"));
		double[] actuals = dailyDatas.stream().sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate()))
				.mapToDouble(e -> e.getNetWorthPerUnit()).toArray();
		assertArrayEquals("actuals are: " + Arrays.toString(actuals), new double[] { 1.0720, 1.2892, 1.3391, 2.1672 },
				actuals, 0.0001);
	}

	@Test
	public void no_daily_need_be_supplemented() {
		pm.supplementDailyData("ID001", CommonUtil.parseDate("yyyy-MM-dd", "2017-01-09"));
	}

	@Test(expected = PortfolioException.class)
	public void supplement_when_portfolio_id_not_exists() {
		pm.supplementDailyData("ID002", CommonUtil.parseDate("yyyy-MM-dd", "2017-01-13"));
	}

	@Test(expected = PortfolioException.class)
	public void get_daily_data_when_start_date_after_end_data() {
		pm.getDailyData("ID001", CommonUtil.parseDate("yyyy-MM-dd", "2017-01-10"),
				CommonUtil.parseDate("yyyy-MM-dd", "2017-01-09"));
	}

	@Test(expected = PortfolioException.class)
	public void get_daily_data_when_portfolio_id_not_exists() {
		pm.getDailyData("ID002", CommonUtil.parseDate("yyyy-MM-dd", "2017-01-10"),
				CommonUtil.parseDate("yyyy-MM-dd", "2017-01-13"));
	}

	private Calendar getDay(String dayStr) {
		Calendar day = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
		day.setTime(CommonUtil.parseDate("yyyy-MM-dd", dayStr));
		return day;
	}
}
