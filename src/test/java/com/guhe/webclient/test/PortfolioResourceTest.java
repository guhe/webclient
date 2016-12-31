package com.guhe.webclient.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.guhe.portfolio.Holding;
import com.guhe.portfolio.Portfolio;
import com.guhe.portfolio.PortfolioException;
import com.guhe.portfolio.PortfolioManager;
import com.guhe.portfolio.Stock;
import com.guhe.portfolio.TradeRecord;
import com.guhe.portfolio.TradeRecord.BuyOrSell;
import com.guhe.util.CommonUtil;
import com.guhe.webclient.PortfolioResource;
import com.guhe.webclient.StockData;
import com.guhe.webclient.StockMarket;

public class PortfolioResourceTest extends JerseyTest {

	private ObjectMapper mapper = new ObjectMapper();

	private StockMarket market;

	private PortfolioManager pm;

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

		TradeRecord record1 = new TradeRecord();
		record1.setBuyOrSell(TradeRecord.BuyOrSell.BUY);
		record1.setAmount(500);
		record1.setDate(CommonUtil.formatDate("yyy-MM-dd", "2016-08-10 10:20:33"));
		record1.setPrice(8.65);
		record1.setStock(ping_an_yin_hang);
		portfolio.add(record1);
		TradeRecord record2 = new TradeRecord();
		record2.setBuyOrSell(TradeRecord.BuyOrSell.SELL);
		record2.setAmount(300);
		record2.setDate(CommonUtil.formatDate("yyy-MM-dd", "2016-12-22 09:50:55"));
		record2.setPrice(38.88);
		record2.setStock(zhong_guo_ping_an);
		portfolio.add(record2);

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

		pm = mock(PortfolioManager.class);
		when(pm.getPortfolio("P00000001")).thenReturn(createPortfolio());

		ResourceConfig config = new ResourceConfig(PortfolioResource.class);
		config.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(market).to(StockMarket.class);
				bind(pm).to(PortfolioManager.class);
			}
		});
		return config;
	}

	@Test
	public void test_get_portfolio_id() {
		JsonNode actual = target("/Portfolio/P00000001").request().accept(MediaType.APPLICATION_JSON)
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
	public void test_get_holdingstocks_by_portfolio_id() {
		JsonNode actual = target("/Portfolio/P00000001/HoldingStock").request().accept(MediaType.APPLICATION_JSON)
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

	@Test
	public void test_get_trade_record_by_portfolio_id() {
		JsonNode actual = target("/Portfolio/P00000001/Trade").request().accept(MediaType.APPLICATION_JSON)
				.get(JsonNode.class);

		ArrayNode expect = mapper.createArrayNode();
		ObjectNode obj1 = mapper.createObjectNode();
		obj1.set("buyOrSell", mapper.getNodeFactory().textNode("BUY"));
		obj1.set("stockCode", mapper.getNodeFactory().textNode("000001"));
		obj1.set("stockName", mapper.getNodeFactory().textNode("平安银行"));
		obj1.set("amount", mapper.getNodeFactory().numberNode(500));
		obj1.set("price", mapper.getNodeFactory().numberNode(8.65));
		obj1.set("date", mapper.getNodeFactory().textNode("2016-08-10"));
		expect.add(obj1);
		ObjectNode obj2 = mapper.createObjectNode();
		obj2.set("buyOrSell", mapper.getNodeFactory().textNode("SELL"));
		obj2.set("stockCode", mapper.getNodeFactory().textNode("601318"));
		obj2.set("stockName", mapper.getNodeFactory().textNode("中国平安"));
		obj2.set("amount", mapper.getNodeFactory().numberNode(300));
		obj2.set("price", mapper.getNodeFactory().numberNode(38.88));
		obj2.set("date", mapper.getNodeFactory().textNode("2016-12-22"));
		expect.add(obj2);

		assertEquals(expect, actual);
	}

	@Test
	public void test_post_trade_and_succ() {
		ObjectNode tradeObj = mapper.createObjectNode();
		tradeObj.set("buyOrSell", mapper.getNodeFactory().textNode("BUY"));
		tradeObj.set("stockCode", mapper.getNodeFactory().textNode("000001"));
		tradeObj.set("stockName", mapper.getNodeFactory().textNode("平安银行"));
		tradeObj.set("amount", mapper.getNodeFactory().numberNode(500));
		tradeObj.set("price", mapper.getNodeFactory().numberNode(8.65));
		tradeObj.set("date", mapper.getNodeFactory().textNode("2016-08-10"));
		Entity<String> entity = Entity.json(tradeObj.toString());
		Response response = target("/Portfolio/P00000001/Trade").request().accept(MediaType.APPLICATION_JSON)
				.post(entity);

		verify(pm).trade("P00000001", "000001", BuyOrSell.BUY, 8.65, 500, 0,
				CommonUtil.formatDate("yyy-MM-dd", "2016-08-10"));

		assertEquals(200, response.getStatus());

		ObjectNode expectObj = mapper.createObjectNode();
		expectObj.set("rltCode", mapper.getNodeFactory().numberNode(0));
		expectObj.set("message", mapper.getNodeFactory().textNode("OK"));
		assertEquals(expectObj, response.readEntity(ObjectNode.class));
	}

	@Test
	public void test_post_trade_and_fail() {
		doThrow(new PortfolioException("mock expection")).when(pm).trade("P00000001", "000001", BuyOrSell.BUY, 8.65,
				500, 0, CommonUtil.formatDate("yyy-MM-dd", "2016-08-10"));

		ObjectNode tradeObj = mapper.createObjectNode();
		tradeObj.set("buyOrSell", mapper.getNodeFactory().textNode("BUY"));
		tradeObj.set("stockCode", mapper.getNodeFactory().textNode("000001"));
		tradeObj.set("stockName", mapper.getNodeFactory().textNode("平安银行"));
		tradeObj.set("amount", mapper.getNodeFactory().numberNode(500));
		tradeObj.set("price", mapper.getNodeFactory().numberNode(8.65));
		tradeObj.set("date", mapper.getNodeFactory().textNode("2016-08-10"));
		Entity<String> entity = Entity.json(tradeObj.toString());
		Response response = target("/Portfolio/P00000001/Trade").request().accept(MediaType.APPLICATION_JSON)
				.post(entity);

		assertEquals(200, response.getStatus());

		ObjectNode expectObj = mapper.createObjectNode();
		expectObj.set("rltCode", mapper.getNodeFactory().numberNode(-1));
		expectObj.set("message", mapper.getNodeFactory().textNode("mock expection"));
		assertEquals(expectObj, response.readEntity(ObjectNode.class));
	}
}
