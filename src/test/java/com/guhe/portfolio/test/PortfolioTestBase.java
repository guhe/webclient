package com.guhe.portfolio.test;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.guhe.market.MoneyExchanger;
import com.guhe.market.StockMarket;
import com.guhe.portfolio.JpaPortfolioManager;
import com.guhe.util.DerbyUtil;

public class PortfolioTestBase {

	protected static EntityManagerFactory testEmf;

	protected JpaPortfolioManager pm;
	
	protected StockMarket market;
	
	protected MoneyExchanger moneyExchanger;

	@BeforeClass
	public static void beforeAll() throws IOException {
		FileUtils.deleteDirectory(new File("test_temp"));
		System.setProperty("derby.stream.error.file", "test_temp/derby.log");

		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.url", "jdbc:derby:test_temp/guhe;create=true");
		properties.put("hibernate.hbm2ddl.auto", "update");
		testEmf = Persistence.createEntityManagerFactory("GUHE", properties);
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
		
		market = mock(StockMarket.class);
		pm.setMarket(market);
		
		moneyExchanger = mock(MoneyExchanger.class);
		pm.setMoneyExchanger(moneyExchanger);
	}

	@After
	public void after() {
		pm.getEm().close();
	}
}
