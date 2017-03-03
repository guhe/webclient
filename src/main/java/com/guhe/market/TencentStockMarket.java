package com.guhe.market;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.client.ClientBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guhe.util.CommonUtil;

public class TencentStockMarket implements StockMarket {
	private static final Logger LOGGER = Logger.getLogger(TencentStockMarket.class.getName());

	private Map<String, StockData> cache = new HashMap<>();
	private Map<String, StockData> hisCache = new HashMap<>();
	private Set<String> openDays = new HashSet<>();

	public TencentStockMarket() {
	}

	@Override
	public synchronized StockData getStockData(String stockCode, Calendar day) {
		if (day == null || CommonUtil.formatDate("yyyy-MM-dd", day.getTime()) == CommonUtil.formatDate("yyyy-MM-dd",
				new Date())) {
			return getCurrentStockData(stockCode);
		} else {
			return getHistoryStockData(stockCode, day);
		}
	}

	@Override
	public boolean isOpen(Calendar day) {
		if (openDays.isEmpty()) {
			String url = "http://web.ifzq.gtimg.cn/appstock/app/fqkline/get?param=sh000001,day,,,180,";
			try {
				String data = ClientBuilder.newClient().target(url).request().get(String.class);
				ObjectMapper mapper = new ObjectMapper();
				JsonNode dataNode = mapper.readTree(data);
				dataNode = dataNode.get("data").get("sh000001").get("day");
				for (JsonNode node : dataNode) {
					String dayStr = node.get(0).asText();
					openDays.add(dayStr);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return openDays.contains(CommonUtil.formatDate("yyyy-MM-dd", day.getTime()));
	}

	private StockData getHistoryStockData(String stockCode, Calendar day) {
		String dayStr = CommonUtil.formatDate("yyyy-MM-dd", day.getTime());
		String key = stockCode + "#" + dayStr;
		if (!hisCache.containsKey(key)) {
			String stockFullCode = toStockFullCode(stockCode);
			String url = toUrl(stockFullCode, day);

			String data = ClientBuilder.newClient().target(url).request().get(String.class);
			parseHisData(data, stockFullCode, stockCode);
		}
		return hisCache.get(key);
	}

	private void parseHisData(String data, String stockFullCode, String stockCode) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode dataNode = mapper.readTree(data);
			dataNode = dataNode.get("data").get(stockFullCode).get("day");
			for (JsonNode node : dataNode) {
				StockData sd = new StockData();
				String dayStr = node.get(0).asText();
				sd.price = node.get(2).asDouble();
				hisCache.put(stockCode + "#" + dayStr, sd);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private StockData getCurrentStockData(String stockCode) {
		if (!cache.containsKey(stockCode)) {
			String stockFullCode = toStockFullCode(stockCode);
			String url = toUrl(stockFullCode, null);

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

	private String toUrl(String stockFullCode, Calendar day) {
		int days = 1;
		if (day != null) {
			days = 180;
		}
		String urlPattern = "http://web.ifzq.gtimg.cn/appstock/app/fqkline/get?param={0},day,,,{1},";
		return MessageFormat.format(urlPattern, stockFullCode, days);
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

class StockDataAndHis {
	StockData current;
	Map<String, StockData> history = new HashMap<>();
}
