package com.guhe.webclient;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.xml.bind.annotation.XmlRootElement;

import com.guhe.dao.Dao;
import com.guhe.dao.Portfolio;

@Path("/Portfolios")
public class PortfoliosResource {

	@Inject
	private StockMarket stockMarket;

	@Inject
	private Dao dao;

	@GET
	@Path("{portfolio}")
	public PortfolioViewData getPortfolio(@PathParam("portfolio") String id) {
		Portfolio portfolio = dao.getPortfolio(id);
		if (portfolio == null) {
			return null;
		}
		return createViewData(dao.getPortfolio(id));
	}

	@GET
	@Path("{portfolio}/HoldingStocks")
	public List<HoldingStockViewData> getHoldingStocks(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = dao.getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}
		
		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket);
		return calculator.getHoldingCalculators().stream().map(e -> createViewData(e)).collect(Collectors.toList());
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
