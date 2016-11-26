package com.guhe.webclient.test;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.guhe.util.Reflector;
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
}
