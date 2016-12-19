package com.guhe.webclient.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.guhe.dao.Dao;
import com.guhe.dao.DaoManager;
import com.guhe.dao.Holding;
import com.guhe.dao.Portfolio;
import com.guhe.dao.Stock;
import com.guhe.webclient.PortfoliosResource;
import com.guhe.webclient.StockData;
import com.guhe.webclient.StockMarket;

public class PortfoliosResourceTest extends JerseyTest {

	private ObjectMapper mapper = new ObjectMapper();

	private StockMarket market;

	private DaoManager daoManager;

	private Dao dao;

	private Portfolio createPortfolio() {
		Portfolio portfolio = new Portfolio();
		portfolio.setId("P00000001");
		portfolio.setName("范昌虎测试组合1");
		portfolio.setCash(23066);
		portfolio.setNumberOfShares(150000);
		portfolio.setNetWorthPerUnitLastYear(1.0822);

		Stock ping_an_yin_hang = new Stock("000001", "平安银行");
		portfolio.add(new Holding(ping_an_yin_hang, 5800));
		Stock zhong_guo_ping_an = new Stock("601318", "中国平安");
		portfolio.add(new Holding(zhong_guo_ping_an, 3600));

		return portfolio;
	}

	@Override
	protected Application configure() {
		market = mock(StockMarket.class);
		StockData data1 = new StockData();
		data1.setPrice(8.88);
		data1.setPe(8);
		data1.setPb(1.11);
		when(market.getStockData("000001")).thenReturn(data1);

		StockData data2 = new StockData();
		data2.setPrice(32.0);
		data2.setPe(10);
		data2.setPb(2);
		when(market.getStockData("601318")).thenReturn(data2);

		dao = mock(Dao.class);
		when(dao.getPortfolio("P00000001")).thenReturn(createPortfolio());

		HttpServletRequest httpReq = mock(HttpServletRequest.class);

		daoManager = mock(DaoManager.class);
		when(daoManager.getDao(httpReq)).thenReturn(dao);

		ResourceConfig config = new ResourceConfig(PortfoliosResource.class);
		config.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(market).to(StockMarket.class);
				bind(daoManager).to(DaoManager.class);
				bind(httpReq).to(HttpServletRequest.class);
			}
		});
		return config;
	}

	@Test
	public void test_get_portfolio_id() {
		JsonNode actual = target("/Portfolios/P00000001").request().accept(MediaType.APPLICATION_JSON)
				.get(JsonNode.class);

		ObjectNode expect = mapper.createObjectNode();
		expect.set("id", mapper.getNodeFactory().textNode("P00000001"));
		expect.set("name", mapper.getNodeFactory().textNode("范昌虎测试组合1"));
		expect.set("netWorthPerUnit", mapper.getNodeFactory().numberNode(1.263729));
		expect.set("rateOfReturnYear", mapper.getNodeFactory().numberNode(0.167741));
		expect.set("totalWorth", mapper.getNodeFactory().numberNode(189770.0));
		expect.set("projectedLiabilities", mapper.getNodeFactory().numberNode(210.684));
		expect.set("netWorth", mapper.getNodeFactory().numberNode(189559.316));
		expect.set("stockNetWorth", mapper.getNodeFactory().numberNode(166493.316));
		expect.set("cash", mapper.getNodeFactory().numberNode(23066.0));
		expect.set("proportionOfStock", mapper.getNodeFactory().numberNode(0.878318));
		expect.set("pe", mapper.getNodeFactory().numberNode(9.282994));
		expect.set("pb", mapper.getNodeFactory().numberNode(1.602923));

		assertEquals(expect, actual);
	}

	@Test
	public void test_get_portfolio_id_holdingstocks() {
		JsonNode actual = target("/Portfolios/P00000001/HoldingStocks").request().accept(MediaType.APPLICATION_JSON)
				.get(JsonNode.class);

		ArrayNode expect = mapper.createArrayNode();
		ObjectNode obj1 = mapper.createObjectNode();
		obj1.set("name", mapper.getNodeFactory().textNode("平安银行"));
		obj1.set("code", mapper.getNodeFactory().textNode("000001"));
		obj1.set("amount", mapper.getNodeFactory().numberNode(5800));
		obj1.set("curPrice", mapper.getNodeFactory().numberNode(8.88));
		obj1.set("marketWorth", mapper.getNodeFactory().numberNode(51504.0));
		obj1.set("estimatedCommission", mapper.getNodeFactory().numberNode(12.876));
		obj1.set("estimatedTax", mapper.getNodeFactory().numberNode(51.504));
		obj1.set("netWorth", mapper.getNodeFactory().numberNode(51439.62));
		obj1.set("proportion", mapper.getNodeFactory().numberNode(0.271364));
		expect.add(obj1);

		ObjectNode obj2 = mapper.createObjectNode();
		obj2.set("name", mapper.getNodeFactory().textNode("中国平安"));
		obj2.set("code", mapper.getNodeFactory().textNode("601318"));
		obj2.set("amount", mapper.getNodeFactory().numberNode(3600));
		obj2.set("curPrice", mapper.getNodeFactory().numberNode(32.0));
		obj2.set("marketWorth", mapper.getNodeFactory().numberNode(115200.0));
		obj2.set("estimatedCommission", mapper.getNodeFactory().numberNode(28.8));
		obj2.set("estimatedTax", mapper.getNodeFactory().numberNode(117.504));
		obj2.set("netWorth", mapper.getNodeFactory().numberNode(115053.696));
		obj2.set("proportion", mapper.getNodeFactory().numberNode(0.606954));
		expect.add(obj2);

		assertEquals(expect, actual);
	}
}
