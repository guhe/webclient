package com.guhe.market;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.client.ClientBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TencentStockMarket implements StockMarket {
	private static final Logger LOGGER = Logger.getLogger(TencentStockMarket.class.getName());

	private Map<String, StockData> cache = new HashMap<>();

	public TencentStockMarket() {
	}

	@Override
	public synchronized StockData getStockData(String stockCode) {
		if (!cache.containsKey(stockCode)) {
			String stockFullCode = toStockFullCode(stockCode);
			String url = toUrl(stockFullCode);

			String data = ClientBuilder.newClient().target(url).request().get(String.class);
			StockData sd = buildStockData(data, stockFullCode);
			cache.put(stockCode, sd);

			LOGGER.info("Loaded a stock data, stock : " + stockCode + ", data : " + sd);
		}

		return cache.get(stockCode);
	}

	private String toStockFullCode(String stockCode) {
		if (stockCode.charAt(0) == '6') {
			return "sh" + stockCode;
		} else if (stockCode.charAt(0) == '0' || stockCode.charAt(0) == '3') {
			return "sz" + stockCode;
		} else {
			throw new RuntimeException("unknown stock code : " + stockCode);
		}
	}

	private String toUrl(String stockFullCode) {
		String urlPattern = "http://web.ifzq.gtimg.cn/appstock/app/fqkline/get?param={0},day,,,1,";
		return MessageFormat.format(urlPattern, stockFullCode);
	}

	private StockData buildStockData(String dataStr, String stockFullCode) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode dataNode = mapper.readTree(dataStr);
			dataNode = dataNode.get("data").get(stockFullCode).get("qt").get(stockFullCode);
			StockData sd = new StockData();
			sd.price = dataNode.get(3).asDouble();
			sd.pe = dataNode.get(39).asDouble();
			sd.pb = dataNode.get(46).asDouble();
			return sd;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
