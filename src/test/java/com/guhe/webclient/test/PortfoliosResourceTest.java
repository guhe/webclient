package com.guhe.webclient.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import com.guhe.webclient.PortfoliosResource;
import com.guhe.webclient.StockMarket;

public class PortfoliosResourceTest extends JerseyTest {

	private ObjectMapper mapper = new ObjectMapper();

	private StockMarket market;

	@Override
	protected Application configure() {
		market = mock(StockMarket.class);
		when(market.getPrice("000001")).thenReturn(8.88);
		when(market.getPrice("601318")).thenReturn(32.0);

		ResourceConfig config = new ResourceConfig(PortfoliosResource.class);
		config.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(market).to(StockMarket.class);
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
		expect.set("netWorthPerUnit", mapper.getNodeFactory().numberNode(1.263744));
		expect.set("rateOfReturnYear", mapper.getNodeFactory().numberNode(0.167755));
		expect.set("totalWorth", mapper.getNodeFactory().numberNode(189770.0));
		expect.set("projectedLiabilities", mapper.getNodeFactory().numberNode(208.38));
		expect.set("netWorth", mapper.getNodeFactory().numberNode(189561.62));
		expect.set("stockNetWorth", mapper.getNodeFactory().numberNode(166495.62));
		expect.set("cash", mapper.getNodeFactory().numberNode(23066.0));
		expect.set("proportionOfStock", mapper.getNodeFactory().numberNode(0.878319));

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
		obj1.set("proportion", mapper.getNodeFactory().numberNode(0.271361));
		expect.add(obj1);

		ObjectNode obj2 = mapper.createObjectNode();
		obj2.set("name", mapper.getNodeFactory().textNode("中国平安"));
		obj2.set("code", mapper.getNodeFactory().textNode("601318"));
		obj2.set("amount", mapper.getNodeFactory().numberNode(3600));
		obj2.set("curPrice", mapper.getNodeFactory().numberNode(32.0));
		obj2.set("marketWorth", mapper.getNodeFactory().numberNode(115200.0));
		obj2.set("estimatedCommission", mapper.getNodeFactory().numberNode(28.8));
		obj2.set("estimatedTax", mapper.getNodeFactory().numberNode(115.2));
		obj2.set("netWorth", mapper.getNodeFactory().numberNode(115056.0));
		obj2.set("proportion", mapper.getNodeFactory().numberNode(0.606958));
		expect.add(obj2);

		assertEquals(expect, actual);
	}
}
