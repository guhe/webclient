package com.guhe.webclient;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.client.ClientBuilder;

import org.jvnet.hk2.annotations.Service;

@Service
public class TencentStockMarket implements StockMarket {
	private static final Logger LOGGER = Logger.getLogger(TencentStockMarket.class.getName());

	private Map<String, StockData> cache = new HashMap<>();

	@Override
	public StockData getStockData(String stockCode) {
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
			StockData sd = buildStockData(data);
			cache.put(stockCode, sd);

			LOGGER.info("Already loaded stock data : " + sd);
		}

		return cache.get(stockCode);
	}

	private StockData buildStockData(String data) {
		data = data.substring(data.indexOf('"') + 1, data.lastIndexOf('"'));
		String[] substrs = data.split("~");

		StockData sd = new StockData();
		sd.price = Double.parseDouble(substrs[3]);
		sd.pe = Double.parseDouble(substrs[39]);
		sd.pb = Double.parseDouble(substrs[46]);
		return sd;
	}

}
