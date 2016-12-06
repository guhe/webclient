package com.guhe.webclient;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.xml.bind.annotation.XmlRootElement;

@Path("/Portfolios")
public class PortfoliosResource {

	private StockMarket market = new SinaStockMarket();

	@GET
	@Path("{portfolio}")
	public PortfolioViewData getPortfolio(@PathParam("portfolio") String id) {
		return createViewData(Dao.instance.getPortfolio(id));
	}

	@GET
	@Path("{portfolio}/HoldingStocks")
	public List<HoldingStockViewData> getHoldingStocks(@PathParam("portfolio") String portfolioId) {
		ArrayList<HoldingStockViewData> holdingStockVDs = new ArrayList<>();
		Portfolio portfolio = Dao.instance.getPortfolio(portfolioId);
		double portfolioNetWorth = getPortfolioStockTotalWorth(portfolio)
				* (1 - Portfolio.RATE_COMMISSION - Portfolio.RATE_TAX) + portfolio.getCash();
		for (Holding holding : portfolio.getHoldings()) {
			holdingStockVDs.add(createViewData(portfolio, holding, portfolioNetWorth));
		}
		return holdingStockVDs;
	}

	private double getPortfolioStockTotalWorth(Portfolio portfolio) {
		double stockTotalWorth = 0;
		for (Holding holding : portfolio.getHoldings()) {
			stockTotalWorth += holding.getAmount() * market.getPrice(holding.getStock().getCode());
		}
		return stockTotalWorth;
	}

	private PortfolioViewData createViewData(Portfolio portfolio) {
		PortfolioViewData viewData = new PortfolioViewData();
		viewData.setId(portfolio.getId());
		viewData.setName(portfolio.getName());
		viewData.setCash(portfolio.getCash());

		double stockTotalWorth = getPortfolioStockTotalWorth(portfolio);
		viewData.setTotalWorth(stockTotalWorth + portfolio.getCash());
		viewData.setProjectedLiabilities(stockTotalWorth * (Portfolio.RATE_COMMISSION + Portfolio.RATE_TAX));
		viewData.setStockNetWorth(stockTotalWorth - viewData.getProjectedLiabilities());
		viewData.setNetWorth(viewData.getTotalWorth() - viewData.getProjectedLiabilities());
		viewData.setNetWorthPerUnit(viewData.getNetWorth() / portfolio.getNumberOfShares());
		viewData.setProportionOfStock(viewData.getStockNetWorth() / viewData.getNetWorth());
		viewData.setRateOfReturnYear(viewData.getNetWorthPerUnit() / portfolio.getNetWorthPerUnitLastYear() - 1);

		return viewData;
	}

	private HoldingStockViewData createViewData(Portfolio portfolio, Holding holding, double portfolioNetWorth) {
		HoldingStockViewData viewData = new HoldingStockViewData();
		viewData.setCode(holding.getStock().getCode());
		viewData.setName(holding.getStock().getName());
		viewData.setAmount(holding.getAmount());
		viewData.setCurPrice(market.getPrice(holding.getStock().getCode()));
		viewData.setMarketWorth(viewData.getAmount() * viewData.getCurPrice());
		viewData.setEstimatedCommission(viewData.getMarketWorth() * Portfolio.RATE_COMMISSION);
		viewData.setEstimatedTax(viewData.getMarketWorth() * Portfolio.RATE_TAX);
		viewData.setNetWorth(
				viewData.getMarketWorth() - viewData.getEstimatedCommission() - viewData.getEstimatedTax());
		viewData.setProportion(viewData.getNetWorth() / portfolioNetWorth);
		return viewData;
	}

	public StockMarket getMarket() {
		return market;
	}

	public void setMarket(StockMarket market) {
		this.market = market;
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
