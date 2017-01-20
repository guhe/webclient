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

import com.guhe.portfolio.Portfolio;
import com.guhe.portfolio.PortfolioException;
import com.guhe.portfolio.PortfolioManager;
import com.guhe.portfolio.PurchaseRedeemRecord.PurchaseOrRedeem;
import com.guhe.portfolio.TradeRecord.BuyOrSell;
import com.guhe.util.CommonUtil;

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
		return portfolios.stream().map(e -> new PortfolioCalculator(e, stockMarket).getViewData())
				.collect(Collectors.toList());
	}

	@GET
	@Path("{portfolio}")
	public PortfolioViewData getPortfolio(@PathParam("portfolio") String id) {
		Portfolio portfolio = pm.getPortfolio(id);
		if (portfolio == null) {
			return null;
		}

		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket);
		return calculator.getViewData();
	}

	@GET
	@Path("{portfolio}/HoldingStock")
	public List<HoldingStockViewData> getHoldingStocks(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = pm.getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket);
		return calculator.getHoldingStockVDs();
	}

	@GET
	@Path("{portfolio}/Holder")
	public List<PortfolioHolderViewData> getHolders(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = pm.getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket);
		return calculator.getHolderVDs();
	}

	@GET
	@Path("{portfolio}/Trade")
	public List<TradeRecordViewData> getTradeRecords(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = pm.getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket);
		return calculator.getTradeRecordVDs();
	}

	@POST
	@Path("{portfolio}/Trade")
	public Response addTradeRecord(@PathParam("portfolio") String portfolioId, TradeRecordViewData viewData) {
		PortfolioResultViewData result;
		try {
			BuyOrSell buyOrSell = BuyOrSell.valueOf(viewData.getBuyOrSell());
			Date date = CommonUtil.parseDate("yyyy-MM-dd", viewData.getDate());
			pm.trade(portfolioId, viewData.getStockCode(), buyOrSell, viewData.getPrice(), viewData.getAmount(),
					viewData.getFee(), date);
			result = new PortfolioResultViewData(0, "OK");
		} catch (PortfolioException e) {
			LOGGER.warning("Failed to trade, PortfolioId: " + portfolioId + ", Trade: " + viewData + ", reason: "
					+ e.getMessage());
			result = new PortfolioResultViewData(-1, e.getMessage());
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

		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket);
		return calculator.getPurchaseRedeemVDs();
	}

	@POST
	@Path("{portfolio}/PurchaseRedeem")
	public Response addPurchaseRedeemRecord(@PathParam("portfolio") String portfolioId,
			PurchaseRedeemViewData viewData) {
		PortfolioResultViewData result;
		try {
			Date date = CommonUtil.parseDate("yyyy-MM-dd", viewData.getDate());
			if (PurchaseOrRedeem.valueOf(viewData.getPurchaseOrRedeem()) == PurchaseOrRedeem.PURCHASE) {
				pm.purchase(portfolioId, viewData.getHolder(), viewData.getMoney(), viewData.getNetWorth(),
						viewData.getFee(), date);
			} else {
				pm.redeem(portfolioId, viewData.getHolder(), viewData.getShare(), viewData.getNetWorth(),
						viewData.getFee(), date);
			}
			result = new PortfolioResultViewData(0, "OK");
		} catch (PortfolioException e) {
			LOGGER.warning("Failed to purchase or redeem, PortfolioId: " + portfolioId + ", PurchaseRedeem: " + viewData
					+ ", reason: " + e.getMessage());
			result = new PortfolioResultViewData(-1, e.getMessage());
		}
		return Response.ok(result).build();
	}
}