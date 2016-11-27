package com.guhe.webclient.test;

import static org.junit.Assert.*;

import java.util.List;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.guhe.util.Reflector;
import com.guhe.webclient.HoldingStock;
import com.guhe.webclient.Portfolio;
import com.guhe.webclient.PortfoliosResource;

public class PortfoliosResourceTest extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(PortfoliosResource.class);
	}

	@Test
	public void test_get_portfolio_id() {
		Portfolio actual = target("/Portfolios/P00000001").request().accept(MediaType.APPLICATION_JSON)
				.get(Portfolio.class);
		Portfolio expect = new Portfolio("P00000001", "范昌虎测试组合1");
		expect.setNetWorthPerUnit(1.0235);
		expect.setRateOfReturnYear(0.3556);
		expect.setTotalWorth(96986);
		expect.setProjectedLiabilities(132.7);
		expect.setNetWorth(96853.3);
		expect.setStockNetWorth(73787.3);
		expect.setCash(23066);
		expect.setProportionOfStock(0.76);
		assertTrue(Reflector.isAllFieldsEquals(expect, actual));
	}

	@Test
	public void test_get_portfolio_id_holdingstocks() {
		List<HoldingStock> actual = target("/Portfolios/P00000001/HoldingStocks").request()
				.accept(MediaType.APPLICATION_JSON).get(new GenericType<List<HoldingStock>>() {
				});

		assertEquals(2, actual.size());

		HoldingStock expect1 = new HoldingStock();
		expect1.setName("平安银行");
		expect1.setCode("000001");
		expect1.setAmount(4000);
		expect1.setCurPrice(8.88);
		expect1.setMarketWorth(35520);
		expect1.setEstimatedCommission(56.23);
		expect1.setEstimatedTax(10);
		expect1.setNetWorth(35453.77);
		expect1.setProportion(0.366);
		assertTrue(Reflector.isAllFieldsEquals(expect1, actual.get(0)));

		HoldingStock expect2 = new HoldingStock();
		expect2.setName("中国平安");
		expect2.setCode("601318");
		expect2.setAmount(1200);
		expect2.setCurPrice(32.00);
		expect2.setMarketWorth(38400);
		expect2.setEstimatedCommission(55.97);
		expect2.setEstimatedTax(10.5);
		expect2.setNetWorth(38333.53);
		expect2.setProportion(0.396);
		assertTrue(Reflector.isAllFieldsEquals(expect2, actual.get(1)));
	}
}
