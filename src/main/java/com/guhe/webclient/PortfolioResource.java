package com.guhe.webclient;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import com.guhe.dao.DaoManager;
import com.guhe.dao.Portfolio;
import com.guhe.dao.TradeRecord;

@Path("/Portfolio")
public class PortfolioResource {

	@Inject
	private StockMarket stockMarket;

	@Context
	private HttpServletRequest httpRequest;

	@Inject
	private DaoManager daoManager;

	@GET
	@Path("{portfolio}")
	public PortfolioViewData getPortfolio(@PathParam("portfolio") String id) {
		Portfolio portfolio = daoManager.getDao(httpRequest).getPortfolio(id);
		if (portfolio == null) {
			return null;
		}
		return createViewData(portfolio);
	}

	@GET
	@Path("{portfolio}/HoldingStock")
	public List<HoldingStockViewData> getHoldingStocks(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = daoManager.getDao(httpRequest).getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket);
		return calculator.getHoldingCalculators().stream().map(e -> createViewData(e)).collect(Collectors.toList());
	}

	@GET
	@Path("{portfolio}/Trade")
	public List<TradeRecordViewData> getTradeRecords(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = daoManager.getDao(httpRequest).getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return portfolio.getTradeRecords().stream().map(e -> createViewData(e, sdf)).collect(Collectors.toList());
	}
	
	@POST
	@Path("{portfolio}/Trade")
	public Response addTradeRecord(@PathParam("portfolio") String portfolioId, TradeRecordViewData viewData){
		return Response.ok().build();
	}

	private PortfolioViewData createViewData(Portfolio portfolio) {
		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket);

		PortfolioViewData viewData = new PortfolioViewData();
		viewData.setId(portfolio.getId());
		viewData.setName(portfolio.getName());
		viewData.setCash(portfolio.getCash());
		viewData.setTotalWorth(calculator.getTotalWorth());
		viewData.setProjectedLiabilities(calculator.getProjectedLiabilities());
		viewData.setStockNetWorth(calculator.getStockNetWorth());
		viewData.setNetWorth(calculator.getNetWorth());
		viewData.setNetWorthPerUnit(calculator.getNetWorthPerUnit());
		viewData.setProportionOfStock(calculator.getProportionOfStock());
		viewData.setRateOfReturnYear(calculator.getRateOfReturnYear());
		viewData.setPe(calculator.getPE());
		viewData.setPb(calculator.getPB());
		return viewData;
	}

	private HoldingStockViewData createViewData(PortfolioCalculator.HoldingCalculator calculator) {
		HoldingStockViewData viewData = new HoldingStockViewData();
		viewData.setCode(calculator.getHolding().getStock().getCode());
		viewData.setName(calculator.getHolding().getStock().getName());
		viewData.setAmount(calculator.getHolding().getAmount());
		viewData.setCurPrice(calculator.getCurPrice());
		viewData.setMarketWorth(calculator.getMarketWorth());
		viewData.setEstimatedCommission(calculator.getEstimatedCommission());
		viewData.setEstimatedTax(calculator.getEstimatedTax());
		viewData.setNetWorth(calculator.getNetWorth());
		viewData.setProportion(calculator.getProportion());
		return viewData;
	}

	private TradeRecordViewData createViewData(TradeRecord tradeRecord, SimpleDateFormat sdf) {
		TradeRecordViewData viewDate = new TradeRecordViewData();
		viewDate.setBuyOrSell(tradeRecord.getBuyOrSell().toString());
		viewDate.setStockCode(tradeRecord.getStock().getCode());
		viewDate.setStockName(tradeRecord.getStock().getName());
		viewDate.setAmount(tradeRecord.getAmount());
		viewDate.setPrice(tradeRecord.getPrice());
		viewDate.setDate(sdf.format(tradeRecord.getDate()));
		return viewDate;
	}

}

@XmlRootElement(name = "portfolio")
class PortfolioViewData {
	private String id;
	private String name;
	private double cash;
	private double totalWorth;
	private double projectedLiabilities;
	private double stockNetWorth;
	private double netWorth;
	private double netWorthPerUnit;
	private double proportionOfStock;
	private double rateOfReturnYear;
	private double pe;
	private double pb;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getNetWorthPerUnit() {
		return netWorthPerUnit;
	}

	public void setNetWorthPerUnit(Double netWorthPerUnit) {
		this.netWorthPerUnit = netWorthPerUnit;
	}

	public Double getRateOfReturnYear() {
		return rateOfReturnYear;
	}

	public void setRateOfReturnYear(Double rateOfReturnYear) {
		this.rateOfReturnYear = rateOfReturnYear;
	}

	public Double getTotalWorth() {
		return totalWorth;
	}

	public void setTotalWorth(Double totalWorth) {
		this.totalWorth = totalWorth;
	}

	public Double getProjectedLiabilities() {
		return projectedLiabilities;
	}

	public void setProjectedLiabilities(Double projectedLiabilities) {
		this.projectedLiabilities = projectedLiabilities;
	}

	public Double getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(Double netWorth) {
		this.netWorth = netWorth;
	}

	public Double getStockNetWorth() {
		return stockNetWorth;
	}

	public void setStockNetWorth(Double stockNetWorth) {
		this.stockNetWorth = stockNetWorth;
	}

	public Double getCash() {
		return cash;
	}

	public void setCash(Double cash) {
		this.cash = cash;
	}

	public Double getProportionOfStock() {
		return proportionOfStock;
	}

	public void setProportionOfStock(Double proportionOfStock) {
		this.proportionOfStock = proportionOfStock;
	}

	public Double getPe() {
		return pe;
	}

	public void setPe(Double pe) {
		this.pe = pe;
	}

	public Double getPb() {
		return pb;
	}

	public void setPb(Double pb) {
		this.pb = pb;
	}

}

@XmlRootElement(name = "holdingStock")
class HoldingStockViewData {
	private String name;
	private String code;
	private long amount;
	private double curPrice;
	private double marketWorth;
	private double estimatedCommission;
	private double estimatedTax;
	private double netWorth;
	private double proportion;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public Double getCurPrice() {
		return curPrice;
	}

	public void setCurPrice(Double curPrice) {
		this.curPrice = curPrice;
	}

	public Double getMarketWorth() {
		return marketWorth;
	}

	public void setMarketWorth(Double marketWorth) {
		this.marketWorth = marketWorth;
	}

	public Double getEstimatedCommission() {
		return estimatedCommission;
	}

	public void setEstimatedCommission(Double estimatedCommission) {
		this.estimatedCommission = estimatedCommission;
	}

	public Double getEstimatedTax() {
		return estimatedTax;
	}

	public void setEstimatedTax(Double estimatedTax) {
		this.estimatedTax = estimatedTax;
	}

	public Double getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(Double netWorth) {
		this.netWorth = netWorth;
	}

	public Double getProportion() {
		return proportion;
	}

	public void setProportion(Double proportion) {
		this.proportion = proportion;
	}
}

@XmlRootElement(name = "tradeRecord")
class TradeRecordViewData {
	private String buyOrSell;
	private String stockName;
	private String stockCode;
	private long amount;
	private double price;
	private String date;

	public String getBuyOrSell() {
		return buyOrSell;
	}

	public void setBuyOrSell(String buyOrSell) {
		this.buyOrSell = buyOrSell;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}