package com.guhe.webclient;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.guhe.market.StockMarket;
import com.guhe.portfolio.DailyData;
import com.guhe.portfolio.Portfolio;
import com.guhe.portfolio.PortfolioCalculator;
import com.guhe.portfolio.PortfolioException;
import com.guhe.portfolio.PortfolioManager;
import com.guhe.portfolio.PurchaseRedeemRecord;
import com.guhe.portfolio.PurchaseRedeemRecord.PurchaseOrRedeem;
import com.guhe.portfolio.TradeRecord;
import com.guhe.portfolio.TradeRecord.BuyOrSell;
import com.guhe.util.CommonUtil;

@Path("/Portfolio")
public class PortfolioResource {

	private static final Logger LOGGER = Logger.getLogger(PortfolioResource.class.getName());

	@Inject
	private StockMarket stockMarket;

	@Inject
	private PortfolioManager pm;

	@GET
	public List<PortfolioViewData> getPortfolios() {
		List<Portfolio> portfolios = pm.getPortfolios();
		return portfolios.stream().map(e -> createPortfolioViewData(new PortfolioCalculator(e, stockMarket)))
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
		return createPortfolioViewData(calculator);
	}

	private PortfolioViewData createPortfolioViewData(PortfolioCalculator calculator) {
		PortfolioViewData viewData = new PortfolioViewData();
		viewData.setId(calculator.getPortfolio().getId());
		viewData.setName(calculator.getPortfolio().getName());
		viewData.setCash(calculator.getPortfolio().getCash());
		viewData.setTotalWorth(calculator.getTotalWorth());
		viewData.setProjectedLiabilities(calculator.getProjectedLiabilities());
		viewData.setStockNetWorth(calculator.getStockNetWorth());
		viewData.setNetWorth(calculator.getNetWorth());
		viewData.setProfit(viewData.getNetWorth() - calculator.getTotalInvestment());
		viewData.setNetWorthPerUnit(calculator.getNetWorthPerUnit());
		viewData.setProportionOfStock(calculator.getProportionOfStock());
		viewData.setRateOfReturnYear(calculator.getRateOfReturnYear());
		viewData.setPe(calculator.getPE());
		viewData.setPb(calculator.getPB());
		return viewData;
	}

	@GET
	@Path("{portfolio}/HoldingStock")
	public List<HoldingStockViewData> getHoldingStocks(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = pm.getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket);
		return createHoldingStockVDs(calculator);
	}

	private List<HoldingStockViewData> createHoldingStockVDs(PortfolioCalculator calculator) {
		return calculator.getHoldingCalculatorStream().map(e -> createViewData(e)).collect(Collectors.toList());
	}

	public HoldingStockViewData createViewData(PortfolioCalculator.HoldingCalculator calculator) {
		HoldingStockViewData vd = new HoldingStockViewData();
		vd.setCode(calculator.getHolding().getStock().getCode());
		vd.setName(calculator.getHolding().getStock().getName());
		vd.setAmount(calculator.getHolding().getAmount());
		vd.setCurPrice(calculator.getPrice());
		vd.setMarketWorth(calculator.getMarketWorth());
		vd.setEstimatedCommission(calculator.getEstimatedCommission());
		vd.setEstimatedTax(calculator.getEstimatedTax());
		vd.setNetWorth(calculator.getNetWorth());
		vd.setProportion(calculator.getProportion());
		return vd;
	}

	@GET
	@Path("{portfolio}/Holder")
	public List<PortfolioHolderViewData> getHolders(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = pm.getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket);
		return getHolderVDs(calculator);
	}

	private List<PortfolioHolderViewData> getHolderVDs(PortfolioCalculator calculator) {
		return calculator.getHolderCalculatorStream().map(e -> createViewData(e)).collect(Collectors.toList());
	}

	public PortfolioHolderViewData createViewData(PortfolioCalculator.HolderCalculator calculator) {
		PortfolioHolderViewData vd = new PortfolioHolderViewData();
		vd.setName(calculator.getPortfolioHolder().getHolder().getName());
		vd.setShare(calculator.getPortfolioHolder().getShare());
		vd.setNetWorth(calculator.getNetWorth());
		vd.setProportion(calculator.getProportion());
		vd.setRateOfReturn(calculator.getRateOfReturn());
		vd.setTotalInvestment(calculator.getPortfolioHolder().getTotalInvestment());
		return vd;
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

	private TradeRecordViewData createViewData(TradeRecord record) {
		TradeRecordViewData vd = new TradeRecordViewData();
		vd.setBuyOrSell(record.getBuyOrSell().toString());
		vd.setStockCode(record.getStock().getCode());
		vd.setStockName(record.getStock().getName());
		vd.setAmount(record.getAmount());
		vd.setPrice(record.getPrice());
		vd.setFee(record.getFee());
		vd.setDate(CommonUtil.formatDate("yyyy-MM-dd", record.getDate()));
		return vd;
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

		return portfolio.getPurchaseRedeemRecords().stream().map(e -> createViewData(e)).collect(Collectors.toList());
	}

	private PurchaseRedeemViewData createViewData(PurchaseRedeemRecord record) {
		PurchaseRedeemViewData vd = new PurchaseRedeemViewData();
		vd.setPurchaseOrRedeem(record.getPurchaseOrRedeem().toString());
		vd.setHolder(record.getHolder().getName());
		vd.setShare(record.getShare());
		vd.setNetWorthPerUnit(record.getNetWorthPerUnit());
		vd.setFee(record.getFee());
		double money = record.getShare() * record.getNetWorthPerUnit();
		if (record.getPurchaseOrRedeem() == PurchaseOrRedeem.PURCHASE) {
			money += record.getFee();
		} else {
			money -= record.getFee();
		}
		vd.setMoney(money);
		vd.setDate(CommonUtil.formatDate("yyy-MM-dd", record.getDate()));
		return vd;
	}

	@POST
	@Path("{portfolio}/PurchaseRedeem")
	public Response addPurchaseRedeemRecord(@PathParam("portfolio") String portfolioId,
			PurchaseRedeemViewData viewData) {
		PortfolioResultViewData result;
		try {
			Date date = CommonUtil.parseDate("yyyy-MM-dd", viewData.getDate());
			if (PurchaseOrRedeem.valueOf(viewData.getPurchaseOrRedeem()) == PurchaseOrRedeem.PURCHASE) {
				pm.purchase(portfolioId, viewData.getHolder(), viewData.getMoney(), viewData.getNetWorthPerUnit(),
						viewData.getFee(), date);
			} else {
				pm.redeem(portfolioId, viewData.getHolder(), viewData.getShare(), viewData.getNetWorthPerUnit(),
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

	@POST
	@Path("{portfolio}/DailyData/Supplement")
	public Response supplementDailyData(@PathParam("portfolio") String portfolioId) {
		Calendar endDay = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
		if (endDay.get(Calendar.HOUR_OF_DAY) <= 15) {
			endDay.add(Calendar.DAY_OF_MONTH, -1);
		}

		PortfolioResultViewData result;
		try {
			pm.supplementDailyData(portfolioId, endDay.getTime());
			result = new PortfolioResultViewData(0, "OK");
		} catch (Exception e) {
			LOGGER.warning("Failed to supplement daily data, PortfolioId: " + portfolioId);
			result = new PortfolioResultViewData(-1, e.getMessage());
			e.printStackTrace();
		}
		return Response.ok(result).build();
	}

	@GET
	@Path("{portfolio}/DailyData/NetWorthPerUnit")
	public List<DailyNWPUViewData> getDailyNWPU(@PathParam("portfolio") String portfolioId) {
		Calendar endDay = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
		if (endDay.get(Calendar.HOUR_OF_DAY) <= 15) {
			endDay.add(Calendar.DAY_OF_MONTH, -1);
		}

		Calendar startDay = (Calendar) endDay.clone();
		startDay.add(Calendar.MONTH, -6);

		List<DailyData> dailyDatas = pm.getDailyData(portfolioId, startDay.getTime(), endDay.getTime());
		return dailyDatas.stream().map(e -> {
			DailyNWPUViewData vd = new DailyNWPUViewData();
			vd.setDate(CommonUtil.formatDate("yyyy-MM-dd", e.getDate()));
			vd.setNetWorthPerUnit(e.getNetWorthPerUnit());
			return vd;
		}).collect(Collectors.toList());
	}
}