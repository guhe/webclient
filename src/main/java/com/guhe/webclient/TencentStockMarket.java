package com.guhe.webclient;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;

import org.jvnet.hk2.annotations.Service;

@Service
public class TencentStockMarket implements StockMarket {

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
			cache.put(stockCode, buildStockData(data));
		}

		return cache.get(stockCode);
	}

	// v_sz000858="51~五粮液~000858~27.78~27.60~27.70~417909~190109~227800~27.78~492~27.77~332~27.76~202~27.75~334~27.74~291~27.79~305~27.80~570~27.81~269~27.82~448~27.83~127~15:00:13/27.78/4365/S/12124331/24602|14:56:55/27.80/14/S/38932/24395|14:56:52/27.81/116/B/322585/24392|14:56:49/27.80/131/S/364220/24385|14:56:46/27.81/5/B/13905/24381|14:56:43/27.80/31/B/86199/24375~20121221150355~0.18~0.65~28.11~27.55~27.80/413544/1151265041~417909~116339~1.10~10.14~~28.11~27.55~2.03~1054.39~1054.52~3.64~30.36~24.84~";
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
