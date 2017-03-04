package com.guhe.webclient;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.guhe.market.StockMarket;

@Path("/Market")
public class MarketResource {

	@Inject
	private StockMarket stockMarket;

	@GET
	@Path("Stock/{stockCode}")
	public List<DailyNWPUViewData> getDailyNWPU(@PathParam("stockCode") String stockFullCode) {
		Calendar from = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
		from.set(2016, 11, 14);
		Calendar to = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
		return stockMarket.getDailyStockData(stockFullCode, from, to).entrySet().stream().sorted((e1, e2) -> {
			return e1.getKey().compareTo(e2.getKey());
		}).map(e -> {
			DailyNWPUViewData dailyNWPU = new DailyNWPUViewData();
			dailyNWPU.setDate(e.getKey());
			dailyNWPU.setNetWorthPerUnit(e.getValue().getPrice());
			return dailyNWPU;
		}).collect(Collectors.toList());
	}
}
