package com.guhe.webclient.test;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.guhe.webclient.PortfoliosResource;

public class PortfoliosResourceTest extends JerseyTest {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected Application configure() {
		return new ResourceConfig(PortfoliosResource.class);
	}

	@Test
	public void test_get_portfolio_id() {
		JsonNode actual = target("/Portfolios/P00000001").request().accept(MediaType.APPLICATION_JSON)
				.get(JsonNode.class);

		ObjectNode expect = mapper.createObjectNode();
		expect.set("id", mapper.getNodeFactory().textNode("P00000001"));
		expect.set("name", mapper.getNodeFactory().textNode("范昌虎测试组合1"));
		expect.set("netWorthPerUnit", mapper.getNodeFactory().numberNode(1.0235));
		expect.set("rateOfReturnYear", mapper.getNodeFactory().numberNode(0.3556));
		expect.set("totalWorth", mapper.getNodeFactory().numberNode(96986.0));
		expect.set("projectedLiabilities", mapper.getNodeFactory().numberNode(132.7));
		expect.set("netWorth", mapper.getNodeFactory().numberNode(96853.3));
		expect.set("stockNetWorth", mapper.getNodeFactory().numberNode(73787.3));
		expect.set("cash", mapper.getNodeFactory().numberNode(23066.0));
		expect.set("proportionOfStock", mapper.getNodeFactory().numberNode(0.76));

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
		obj1.set("amount", mapper.getNodeFactory().numberNode(4000));
		obj1.set("curPrice", mapper.getNodeFactory().numberNode(8.88));
		obj1.set("marketWorth", mapper.getNodeFactory().numberNode(35520.0));
		obj1.set("estimatedCommission", mapper.getNodeFactory().numberNode(56.23));
		obj1.set("estimatedTax", mapper.getNodeFactory().numberNode(10.0));
		obj1.set("netWorth", mapper.getNodeFactory().numberNode(35453.77));
		obj1.set("proportion", mapper.getNodeFactory().numberNode(0.366));
		expect.add(obj1);

		ObjectNode obj2 = mapper.createObjectNode();
		obj2.set("name", mapper.getNodeFactory().textNode("中国平安"));
		obj2.set("code", mapper.getNodeFactory().textNode("601318"));
		obj2.set("amount", mapper.getNodeFactory().numberNode(1200));
		obj2.set("curPrice", mapper.getNodeFactory().numberNode(32.0));
		obj2.set("marketWorth", mapper.getNodeFactory().numberNode(38400.0));
		obj2.set("estimatedCommission", mapper.getNodeFactory().numberNode(55.97));
		obj2.set("estimatedTax", mapper.getNodeFactory().numberNode(10.5));
		obj2.set("netWorth", mapper.getNodeFactory().numberNode(38333.53));
		obj2.set("proportion", mapper.getNodeFactory().numberNode(0.396));
		expect.add(obj2);

		assertEquals(expect, actual);
	}
}
