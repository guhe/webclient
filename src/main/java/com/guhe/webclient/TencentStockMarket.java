package com.guhe.webclient;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;

public class TencentStockMarket implements StockMarket {

	private Map<String, Double> cache = new HashMap<>();

	@Override
	public double getPrice(String stockCode) {
		if (!cache.containsKey(stockCode)) {
			String url = "http://qt.gtimg.cn/q=";
			if (stockCode.charAt(0) == '6') {
				url += "sh" + stockCode;
			} else if (stockCode.charAt(0) == '0') {
				url += "sz" + stockCode;
			} else {
				throw new RuntimeException("unknown stock code : " + stockCode);
			}

			String data = ClientBuilder.newClient().target(url).request().get(String.class);
			String price = data.split("~")[3];
			cache.put(stockCode, Double.valueOf(price));
		}

		return cache.get(stockCode);
	}

}
