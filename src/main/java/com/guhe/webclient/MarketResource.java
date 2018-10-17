package com.guhe.webclient;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.guhe.market.StockMarket;

@Path("/Market")
public class MarketResource {

	@Inject
	private StockMarket stockMarket;

	@GET
	@Path("Stock/{stockCode}")
	public List<DailyNWPUViewData> getDailyNWPU(@PathParam("stockCode") String stockFullCode) {
		Calendar from = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
		from.set(2017, 11, 14);
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

	@GET
	@Path("Stock/{stockCode}/totalTradeFee")
	public Double getTotalTradeFee(@PathParam("stockCode") String stockCode, @QueryParam("bos") String buyOrSell,
			@QueryParam("count") int count, @QueryParam("price") double price) {
		if (buyOrSell.equals("BUY")) {
			return stockMarket.getTotalBuyFee(stockCode, count, price);
		} else if (buyOrSell.equals("SELL")) {
			return stockMarket.getTotalSellFee(stockCode, count, price);
		} else {
			throw new IllegalArgumentException("Value 'bos' must be 'BUY' or 'SELL'.");
		}
	}
}
