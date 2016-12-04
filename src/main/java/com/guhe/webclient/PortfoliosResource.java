package com.guhe.webclient;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.xml.bind.annotation.XmlRootElement;

@Path("/Portfolios")
public class PortfoliosResource {

	@GET
	public List<PortfolioViewData> getPortfolios() {
		List<PortfolioViewData> portfolios = new ArrayList<PortfolioViewData>();
		portfolios.addAll(Dao.instance.getPortfolios());
		return portfolios;
	}

	@GET
	@Path("{portfolio}")
	public PortfolioViewData getPortfolio(@PathParam("portfolio") String id) {
		return Dao.instance.getPortfolio(id);
	}

	@GET
	@Path("{portfolio}/HoldingStocks")
	public List<HoldingStockViewData> getHoldingStocks(@PathParam("portfolio") String portfolioId) {
		return Dao.instance.getHoldingStocks(portfolioId);
	}
}

@XmlRootElement(name = "portfolio")
class PortfolioViewData {
	private String id;
	private String name;
	private double netWorthPerUnit;
	private double rateOfReturnYear;
	private double totalWorth;
	private double projectedLiabilities;
	private double netWorth;
	private double stockNetWorth;
	private double cash;
	private double proportionOfStock;

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

	public double getNetWorthPerUnit() {
		return netWorthPerUnit;
	}

	public void setNetWorthPerUnit(double netWorthPerUnit) {
		this.netWorthPerUnit = netWorthPerUnit;
	}

	public double getRateOfReturnYear() {
		return rateOfReturnYear;
	}

	public void setRateOfReturnYear(double rateOfReturnYear) {
		this.rateOfReturnYear = rateOfReturnYear;
	}

	public double getTotalWorth() {
		return totalWorth;
	}

	public void setTotalWorth(double totalWorth) {
		this.totalWorth = totalWorth;
	}

	public double getProjectedLiabilities() {
		return projectedLiabilities;
	}

	public void setProjectedLiabilities(double projectedLiabilities) {
		this.projectedLiabilities = projectedLiabilities;
	}

	public double getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(double netWorth) {
		this.netWorth = netWorth;
	}

	public double getStockNetWorth() {
		return stockNetWorth;
	}

	public void setStockNetWorth(double stockNetWorth) {
		this.stockNetWorth = stockNetWorth;
	}

	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	public double getProportionOfStock() {
		return proportionOfStock;
	}

	public void setProportionOfStock(double proportionOfStock) {
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

	public double getCurPrice() {
		return curPrice;
	}

	public void setCurPrice(double curPrice) {
		this.curPrice = curPrice;
	}

	public double getMarketWorth() {
		return marketWorth;
	}

	public void setMarketWorth(double marketWorth) {
		this.marketWorth = marketWorth;
	}

	public double getEstimatedCommission() {
		return estimatedCommission;
	}

	public void setEstimatedCommission(double estimatedCommission) {
		this.estimatedCommission = estimatedCommission;
	}

	public double getEstimatedTax() {
		return estimatedTax;
	}

	public void setEstimatedTax(double estimatedTax) {
		this.estimatedTax = estimatedTax;
	}

	public double getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(double netWorth) {
		this.netWorth = netWorth;
	}

	public double getProportion() {
		return proportion;
	}

	public void setProportion(double proportion) {
		this.proportion = proportion;
	}
}
