package com.guhe.webclient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.guhe.market.MoneyExchanger;
import com.guhe.market.MoneyName;
import com.guhe.market.StockMarket;
import com.guhe.portfolio.DailyData;
import com.guhe.portfolio.ExchangeMoneyRecord;
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

	@Inject
	private MoneyExchanger moneyExchanger;

	@GET
	public List<PortfolioViewData> getPortfolios() {
		List<Portfolio> portfolios = pm.getPortfolios();
		return portfolios.stream()
				.map(e -> createPortfolioViewData(e, new PortfolioCalculator(e, stockMarket, moneyExchanger)))
				.collect(Collectors.toList());
	}

	@GET
	@Path("{portfolio}")
	public PortfolioViewData getPortfolio(@PathParam("portfolio") String id) {
		Portfolio portfolio = pm.getPortfolio(id);
		if (portfolio == null) {
			return null;
		}

		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket, moneyExchanger);
		return createPortfolioViewData(portfolio, calculator);
	}

	private PortfolioViewData createPortfolioViewData(Portfolio portfolio, PortfolioCalculator calculator) {
		PortfolioViewData viewData = new PortfolioViewData();
		viewData.setId(portfolio.getId());
		viewData.setName(portfolio.getName());
		viewData.setCash(calculator.getCash());
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

	@DELETE
	@Path("{portfolio}")
	public Response deletePortfolio(@PathParam("portfolio") String id) {
		PortfolioResultViewData result;
		try {
			pm.deletePortfolio(id);
			result = new PortfolioResultViewData(0, "OK");
		} catch (Exception e) {
			e.printStackTrace();
			result = new PortfolioResultViewData(-1, e.getMessage());
		}
		return Response.ok(result).build();
	}

	@GET
	@Path("{portfolio}/HoldingStock")
	public List<HoldingStockViewData> getHoldingStocks(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = pm.getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket, moneyExchanger);
		return createHoldingStockVDs(calculator);
	}

	private List<HoldingStockViewData> createHoldingStockVDs(PortfolioCalculator calculator) {
		return calculator.getHoldingCalculatorStream().map(e -> createViewData(e)).collect(Collectors.toList());
	}

	private HoldingStockViewData createViewData(PortfolioCalculator.HoldingCalculator calculator) {
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
		vd.setTradeRecords(calculator.getTradeRecords().stream().sorted((e1, e2) -> {
			int rt = e2.getDate().compareTo(e1.getDate());
			if(rt != 0) {
				return rt;
			}
			return e2.getId() - e1.getId();
		}).limit(2).map(e -> createViewData(e)).collect(Collectors.toList()));
		if(!vd.getTradeRecords().isEmpty()) {
			double lastTradePrice = vd.getTradeRecords().get(0).getPrice();
			vd.setNextBuyPrice(lastTradePrice * (1 - PortfolioCalculator.GRID_SIZE));
			vd.setNextSellPrice(lastTradePrice * (1 + PortfolioCalculator.GRID_SIZE));
		}

		return vd;
	}

	@GET
	@Path("{portfolio}/Holder")
	public List<PortfolioHolderViewData> getHolders(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = pm.getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, stockMarket, moneyExchanger);
		return getHolderVDs(calculator);
	}

	private List<PortfolioHolderViewData> getHolderVDs(PortfolioCalculator calculator) {
		return calculator.getHolderCalculatorStream().map(e -> createViewData(e)).collect(Collectors.toList());
	}

	private PortfolioHolderViewData createViewData(PortfolioCalculator.HolderCalculator calculator) {
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
	public List<TradeRecordViewData> getTradeRecords(@PathParam("portfolio") String portfolioId, @QueryParam("start") String start) {
		Portfolio portfolio = pm.getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		Stream<TradeRecord> trStream = portfolio.getTradeRecords().stream();
		if(start != null) {
			Date startTime = CommonUtil.parseDate("yyyy-MM-dd", start);
			trStream = trStream.filter(e->e.getDate().after(startTime));
		}
		
		return trStream.map(e -> createViewData(e)).collect(Collectors.toList());
	}

	private TradeRecordViewData createViewData(TradeRecord record) {
		TradeRecordViewData vd = new TradeRecordViewData();
		vd.setBuyOrSell(record.getBuyOrSell().toString());
		vd.setStockCode(record.getStock().getCode());
		vd.setStockName(record.getStock().getName());
		vd.setAmount(record.getAmount());
		vd.setPrice(record.getPrice());
		vd.setFee(record.getFee());
		vd.setExRate(record.getExRate());
		vd.setDate(CommonUtil.formatDate("yyyy-MM-dd", record.getDate()));
		vd.setNote(record.getNote());
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
					viewData.getFee(), viewData.getExRate(), date, viewData.getNote());
			result = new PortfolioResultViewData(0, "OK");
		} catch (PortfolioException e) {
			LOGGER.warning("Failed to trade, portfolioId: " + portfolioId + ", trade: " + viewData + ", reason: "
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
			LOGGER.warning("Failed to purchase or redeem, portfolioId: " + portfolioId + ", purchaseRedeem: " + viewData
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
			LOGGER.warning(
					"Failed to supplement daily data, portfolioId: " + portfolioId + ", reason: " + e.getMessage());
			result = new PortfolioResultViewData(-1, e.getMessage());
		}
		return Response.ok(result).build();
	}

	@POST
	@Path("{portfolio}/ExchangeMoney")
	public Response exchangeMoney(@PathParam("portfolio") String portfolioId, ExchangeMoneyParam param) {
		PortfolioResultViewData result;
		try {
			Date date = CommonUtil.parseDate("yyyy-MM-dd", param.getDate());
			pm.exchangeMoney(portfolioId, MoneyName.valueOf(param.getTarget()), param.getTargetAmount(),
					param.getRmbAmount(), date);
			result = new PortfolioResultViewData(0, "OK");
		} catch (Exception e) {
			LOGGER.warning("Failed to convert money, portfolioId: " + portfolioId + ", param: " + param + ", reason: "
					+ e.getMessage());
			result = new PortfolioResultViewData(-1, e.getMessage());
		}
		return Response.ok(result).build();
	}

	@GET
	@Path("{portfolio}/ExchangeMoney")
	public List<ExchangeMoneyParam> getExchangeMoneyRecord(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = pm.getPortfolio(portfolioId);
		if (portfolio == null) {
			return null;
		}

		return portfolio.getExchangeMoneyRecords().stream().map(e -> createViewData(e)).collect(Collectors.toList());
	}

	private ExchangeMoneyParam createViewData(ExchangeMoneyRecord record) {
		ExchangeMoneyParam vd = new ExchangeMoneyParam();
		vd.setTarget(record.getTarget().name());
		vd.setTargetAmount(record.getTargetAmount());
		vd.setRmbAmount(record.getRmbAmount());
		vd.setRate(-record.getRmbAmount() / record.getTargetAmount());
		vd.setDate(CommonUtil.formatDate("yyyy-MM-dd", record.getDate()));
		return vd;
	}

	@GET
	@Path("{portfolio}/DailyData/NetWorthPerUnit")
	public List<DailyNWPUViewData> getDailyNWPU(@PathParam("portfolio") String portfolioId) {
		Calendar endDay = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
		if (endDay.get(Calendar.HOUR_OF_DAY) <= 15) {
			endDay.add(Calendar.DAY_OF_MONTH, -1);
		}

		Calendar startDay = (Calendar) endDay.clone();
		startDay.set(Calendar.MONTH, Calendar.JANUARY);
		startDay.set(Calendar.DAY_OF_MONTH, 1);
		startDay.add(Calendar.DAY_OF_MONTH, -1);
		
		List<DailyData> dailyDatas = pm.getDailyData(portfolioId, startDay.getTime(), endDay.getTime());
		return dailyDatas.stream().map(e -> {
			DailyNWPUViewData vd = new DailyNWPUViewData();
			vd.setDate(CommonUtil.formatDate("yyyy-MM-dd", e.getDate()));
			vd.setNetWorthPerUnit(e.getNetWorthPerUnit());
			return vd;
		}).collect(Collectors.toList());
	}

	@GET
	@Path("{portfolio}/CashDetail")
	public List<CashDetailViewData> getCashDetail(@PathParam("portfolio") String portfolioId) {
		Portfolio portfolio = pm.getPortfolio(portfolioId);

		List<CashDetailViewData> cashDetails = new ArrayList<>();

		CashDetailViewData rmb = new CashDetailViewData();
		rmb.setMoneyName(MoneyName.RMB.name());
		rmb.setAmount(portfolio.getRmbCash());
		rmb.setBuyPrice(1.0);
		rmb.setSellPrice(1.0);
		rmb.setRmbAmount(portfolio.getRmbCash());
		cashDetails.add(rmb);

		tryAddMoney(portfolio, MoneyName.HKD, cashDetails);
		tryAddMoney(portfolio, MoneyName.USD, cashDetails);

		return cashDetails;
	}

	private void tryAddMoney(Portfolio portfolio, MoneyName name, List<CashDetailViewData> cashDetails) {
		double cash = portfolio.getCashByName(name);
		if (CommonUtil.dCompare(cash, 0.0, 2) > 0) {
			CashDetailViewData cashDetail = new CashDetailViewData();
			cashDetail.setMoneyName(name.name());
			cashDetail.setAmount(cash);
			MoneyExchanger.MoneyPrice price = moneyExchanger.getMoneyPrice(name);
			cashDetail.setBuyPrice(price.getBuy());
			cashDetail.setSellPrice(price.getSell());
			cashDetail.setRmbAmount(cash * price.getBuy());
			cashDetails.add(cashDetail);
		}
	}

	@POST
	@Path("{portfolio}/ModifyCash")
	public Response modifyCash(@PathParam("portfolio") String portfolioId, ModifyCashViewData param) {
		PortfolioResultViewData result;
		try {
			Date date = CommonUtil.parseDate("yyyy-MM-dd", param.getDate());
			pm.modifyCash(portfolioId, MoneyName.valueOf(param.getTarget()), param.getNewAmount(), date);
			result = new PortfolioResultViewData(0, "OK");
		} catch (Exception e) {
			LOGGER.warning("Failed to modify cash, portfolioId: " + portfolioId + ", param: " + param + ", reason: "
					+ e.getMessage());
			result = new PortfolioResultViewData(-1, e.getMessage());
		}
		return Response.ok(result).build();
	}

	@GET
	@Path("{portfolio}/ModifyCash")
	public List<ModifyCashViewData> getModifyCashRecords(@PathParam("portfolio") String portfolioId) {
		return pm.getPortfolio(portfolioId).getModifyCashRecords().stream().map(e -> {
			ModifyCashViewData vd = new ModifyCashViewData();
			vd.setTarget(e.getTarget().name());
			vd.setOldAmount(e.getOldAmount());
			vd.setNewAmount(e.getNewAmount());
			vd.setDate(CommonUtil.formatDate("yyyy-MM-dd", e.getDate()));
			return vd;
		}).collect(Collectors.toList());
	}
}