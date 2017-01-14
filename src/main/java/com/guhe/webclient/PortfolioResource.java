package com.guhe.webclient;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
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

import com.guhe.portfolio.Portfolio;
import com.guhe.portfolio.PortfolioException;
import com.guhe.portfolio.PortfolioManager;
import com.guhe.portfolio.PurchaseRedeemRecord;
import com.guhe.portfolio.TradeRecord;
import com.guhe.portfolio.TradeRecord.BuyOrSell;
import com.guhe.util.CommonUtil;
import com.guhe.util.Reflector;

@Path("/Portfolio")
public class PortfolioResource {

	private static final Logger LOGGER = Logger.getLogger(PortfolioResource.class.getName());

	@Inject
	private StockMarket stockMarket;

	@Context
	private HttpServletRequest httpRequest;

	@Inject
	private PortfolioManager pm;

	@GET
	public List<PortfolioViewData> getPortfolios() {
		List<Portfolio> portfolios = pm.getPortfolios();
		return portfolios.stream().map(e -> createViewData(e)).collect(Collectors.toList());
	}

	@GET
	@Path("{portfolio}")
	public PortfolioViewData getPortfolio(@PathParam("portfolio") String id) {
		Portfolio portfolio = pm.getPortfolio(id);
		if (portfolio == null) {
			return null;
		}
		return createViewData(portfolio);
	}

	@GET
	@Path("{portfolio}/HoldingStock")
	public List<HoldingStockViewData> getHoldingStocks(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = pm.getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket);
		return calculator.getHoldingCalculators().stream().map(e -> createViewData(e)).collect(Collectors.toList());
	}

	@GET
	@Path("{portfolio}/Holder")
	public List<PortfolioHolderViewData> getHolders(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = pm.getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket);
		return calculator.getHolderCalculators().stream().map(e -> e.getViewData()).collect(Collectors.toList());
	}

	@GET
	@Path("{portfolio}/Trade")
	public List<TradeRecordViewData> getTradeRecords(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = pm.getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		return portfolio.getTradeRecords().stream().map(e -> createViewData(e)).collect(Collectors.toList());
	}

	@POST
	@Path("{portfolio}/Trade")
	public Response addTradeRecord(@PathParam("portfolio") String portfolioId, TradeRecordViewData viewData) {
		TradeResultViewData result;
		try {
			BuyOrSell buyOrSell = BuyOrSell.valueOf(viewData.getBuyOrSell());
			Date date = CommonUtil.parseDate("yyyy-MM-dd", viewData.getDate());
			pm.trade(portfolioId, viewData.getStockCode(), buyOrSell, viewData.getPrice(), viewData.getAmount(),
					viewData.getFee(), date);
			result = new TradeResultViewData(0, "OK");
		} catch (PortfolioException e) {
			LOGGER.warning("Failed to trade, PortfolioId: " + portfolioId + ", Trade: " + viewData + ", reason: "
					+ e.getMessage());
			result = new TradeResultViewData(-1, e.getMessage());
		}
		return Response.ok(result).build();
	}

	@GET
	@Path("{portfolio}/PurchaseRedeem")
	public List<PurchaseRedeemViewData> getPurchaseRedeemRecords(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = pm.getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		return portfolio.getPurchaseRedeemRecords().stream().map(e -> createViewData(e)).collect(Collectors.toList());
	}

	private PurchaseRedeemViewData createViewData(PurchaseRedeemRecord record) {
		return null;
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

	private TradeRecordViewData createViewData(TradeRecord tradeRecord) {
		TradeRecordViewData viewDate = new TradeRecordViewData();
		viewDate.setBuyOrSell(tradeRecord.getBuyOrSell().toString());
		viewDate.setStockCode(tradeRecord.getStock().getCode());
		viewDate.setStockName(tradeRecord.getStock().getName());
		viewDate.setAmount(tradeRecord.getAmount());
		viewDate.setPrice(tradeRecord.getPrice());
		viewDate.setFee(tradeRecord.getFee());
		viewDate.setDate(CommonUtil.formatDate("yyyy-MM-dd", tradeRecord.getDate()));
		return viewDate;
	}

}

@XmlRootElement
class PortfolioHolderViewData {
	private String name;
	private double share;
	private double netWorth;
	private double totalInvestment;
	private double rateOfReturn;
	private double proportion;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getShare() {
		return share;
	}

	public void setShare(Double share) {
		this.share = share;
	}

	public Double getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(Double netWorth) {
		this.netWorth = netWorth;
	}

	public Double getTotalInvestment() {
		return totalInvestment;
	}

	public void setTotalInvestment(Double totalInvestment) {
		this.totalInvestment = totalInvestment;
	}

	public Double getRateOfReturn() {
		return rateOfReturn;
	}

	public void setRateOfReturn(Double rateOfReturn) {
		this.rateOfReturn = rateOfReturn;
	}

	public Double getProportion() {
		return proportion;
	}

	public void setProportion(Double proportion) {
		this.proportion = proportion;
	}
}

class PurchaseRedeemViewData {

}

@XmlRootElement
class TradeResultViewData {
	private int rltCode;
	private String message;

	public TradeResultViewData(int rltCode, String message) {
		this.rltCode = rltCode;
		this.message = message;
	}

	public int getRltCode() {
		return rltCode;
	}

	public void setRltCode(int rltCode) {
		this.rltCode = rltCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
	private double fee;
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

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return Reflector.toStringByAllFields(this);
	}
}