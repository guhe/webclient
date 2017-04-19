package com.guhe.market;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.client.ClientBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guhe.util.CommonUtil;

public class TencentStockMarket implements StockMarket {
	private static final Logger LOGGER = Logger.getLogger(TencentStockMarket.class.getName());

	private Map<String, LastLoadTimeMillis> lltms = new HashMap<>();
	private Map<String, StockData> cache = new HashMap<>();
	private Map<String, Map<String, StockData>> hisCache = new HashMap<>();

	public TencentStockMarket() {
	}

	@Override
	public synchronized StockData getStockData(String stockCode, Calendar day) {
		String stockFullCode = toStockFullCode(stockCode);
		if (day == null || CommonUtil.formatDate("yyyy-MM-dd", day.getTime()) == CommonUtil.formatDate("yyyy-MM-dd",
				new Date())) {
			return getCurrentStockData(stockFullCode);
		} else {
			return getHistoryStockData(stockFullCode, day);
		}
	}

	public synchronized Map<String, StockData> getDailyStockData(String stockFullCode, Calendar from, Calendar to) {
		tryLoadStockDatas(stockFullCode);

		String fromDayStr = CommonUtil.formatDate("yyyy-MM-dd", from.getTime());
		String toDayStr = CommonUtil.formatDate("yyyy-MM-dd", to.getTime());
		return hisCache.get(stockFullCode).entrySet().stream().filter(e -> {
			return e.getKey().compareTo(fromDayStr) >= 0 && e.getKey().compareTo(toDayStr) <= 0;
		}).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
	}

	@Override
	public boolean isOpen(Calendar day) {
		return getHistoryStockData("sh000001", day) != null;
	}

	private StockData getHistoryStockData(String stockFullCode, Calendar day) {
		tryLoadStockDatas(stockFullCode);

		String dayStr = CommonUtil.formatDate("yyyy-MM-dd", day.getTime());
		return hisCache.get(stockFullCode).get(dayStr);
	}

	private void tryLoadStockDatas(String stockFullCode) {
		LastLoadTimeMillis lltm = lltms.get(stockFullCode);
		if (lltm == null) {
			lltm = new LastLoadTimeMillis();
			lltms.put(stockFullCode, lltm);
		}
		long now = System.currentTimeMillis();
		if (Math.abs(now - lltm.current) > 10 * 1000) {
			hisCache.remove(stockFullCode);
		}

		Map<String, StockData> stockDatas = hisCache.get(stockFullCode);
		if (stockDatas == null) {
			stockDatas = new HashMap<>();
			hisCache.put(stockFullCode, stockDatas);

			String url = toUrl(stockFullCode, false);
			String data = ClientBuilder.newClient().target(url).request().get(String.class);
			parseHisData(data, stockFullCode, stockDatas);
			lltm.current = now;

			LOGGER.info("Loaded a stock data, stock : " + stockFullCode + ", data : " + stockDatas);
		}
	}

	private void parseHisData(String data, String stockFullCode, Map<String, StockData> stockDatas) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode dataNode = mapper.readTree(data);
			dataNode = dataNode.get("data").get(stockFullCode).get("day");
			for (JsonNode node : dataNode) {
				StockData sd = new StockData();
				String dayStr = node.get(0).asText();
				sd.price = node.get(2).asDouble();
				stockDatas.put(dayStr, sd);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private StockData getCurrentStockData(String stockFullCode) {
		LastLoadTimeMillis lltm = lltms.get(stockFullCode);
		if (lltm == null) {
			lltm = new LastLoadTimeMillis();
			lltms.put(stockFullCode, lltm);
		}
		long now = System.currentTimeMillis();
		if (Math.abs(now - lltm.current) > 60 * 60 * 1000) {
			cache.remove(stockFullCode);
		}

		if (!cache.containsKey(stockFullCode)) {
			String url = toUrl(stockFullCode, true);

			String data = ClientBuilder.newClient().target(url).request().get(String.class);
			StockData sd = buildStockData(data, stockFullCode);
			cache.put(stockFullCode, sd);
			lltm.current = now;

			LOGGER.info("Loaded a stock data, stock : " + stockFullCode + ", data : " + sd);
		}

		return cache.get(stockFullCode);
	}

	private String toStockFullCode(String stockCode) {
		if (stockCode.charAt(0) == '6' || stockCode.charAt(0) == '9') {
			return "sh" + stockCode;
		} else if (stockCode.charAt(0) == '0' || stockCode.charAt(0) == '2' || stockCode.charAt(0) == '3') {
			return "sz" + stockCode;
		} else {
			throw new RuntimeException("unknown stock code : " + stockCode);
		}
	}

	private String toUrl(String stockFullCode, boolean onlyCurrent) {
		int days = 1;
		if (!onlyCurrent) {
			days = 90;
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

class LastLoadTimeMillis {
	long current;
	long his;

}