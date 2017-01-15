package com.guhe.webclient;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guhe.portfolio.Holding;
import com.guhe.portfolio.Portfolio;
import com.guhe.portfolio.PortfolioHolder;
import com.guhe.portfolio.PurchaseRedeemRecord;
import com.guhe.portfolio.TradeRecord;
import com.guhe.portfolio.Stock.Exchange;
import com.guhe.util.CommonUtil;

public class PortfolioCalculator {
	private Portfolio portfolio;
	private StockMarket market;

	public PortfolioCalculator(Portfolio portfolio, StockMarket market) {
		this.portfolio = portfolio;
		this.market = market;
	}

	public PortfolioViewData getViewData() {
		PortfolioViewData viewData = new PortfolioViewData();
		viewData.setId(portfolio.getId());
		viewData.setName(portfolio.getName());
		viewData.setCash(portfolio.getCash());
		viewData.setTotalWorth(getTotalWorth());
		viewData.setProjectedLiabilities(getProjectedLiabilities());
		viewData.setStockNetWorth(getStockNetWorth());
		viewData.setNetWorth(getNetWorth());
		viewData.setNetWorthPerUnit(getNetWorthPerUnit());
		viewData.setProportionOfStock(getProportionOfStock());
		viewData.setRateOfReturnYear(getRateOfReturnYear());
		viewData.setPe(getPE());
		viewData.setPb(getPB());
		return viewData;
	}
	
	private double getNumberOfShares(){
		return portfolio.getHolders().stream().map(e->e.getShare()).reduce(0.0, Double::sum);
	}

	private double getStockTotalWorth() {
		return getHoldingCalculatorStream().map(e -> e.getMarketWorth()).reduce(0.0, Double::sum);
	}

	private double getTotalWorth() {
		return getStockTotalWorth() + portfolio.getCash();
	}

	private double getProjectedLiabilities() {
		return getHoldingCalculatorStream().map(e -> e.getEstimatedTax() + e.getEstimatedCommission()).reduce(0.0,
				Double::sum);
	}

	private double getNetWorth() {
		return getTotalWorth() - getProjectedLiabilities();
	}

	private double getStockNetWorth() {
		return getStockTotalWorth() - getProjectedLiabilities();
	}

	private double getNetWorthPerUnit() {
		return getNetWorth() / getNumberOfShares();
	}

	private double getProportionOfStock() {
		return getStockNetWorth() / getNetWorth();
	}

	private double getRateOfReturnYear() {
		return getNetWorthPerUnit() / portfolio.getNetWorthPerUnitLastYear() - 1;
	}

	private double getPE() {
		return getStockTotalWorth()
				/ getHoldingCalculatorStream().map(e -> e.getMarketWorth() / e.getPE()).reduce(0.0, Double::sum);
	}

	private double getPB() {
		return getStockTotalWorth()
				/ getHoldingCalculatorStream().map(e -> e.getMarketWorth() / e.getPB()).reduce(0.0, Double::sum);
	}

	public List<HoldingStockViewData> getHoldingStockVDs() {
		return getHoldingCalculatorStream().map(e -> e.getViewData()).collect(Collectors.toList());
	}

	private Stream<HoldingCalculator> getHoldingCalculatorStream() {
		return portfolio.getHoldings().stream().map(e -> new HoldingCalculator(e));
	}

	public List<PortfolioHolderViewData> getHolderVDs() {
		return getHolderCalculatorStream().map(e -> e.getViewData()).collect(Collectors.toList());
	}

	private Stream<HolderCalculator> getHolderCalculatorStream() {
		return portfolio.getHolders().stream().map(e -> new HolderCalculator(e));
	}

	public List<TradeRecordViewData> getTradeRecordVDs() {
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

	public List<PurchaseRedeemViewData> getPurchaseRedeemVDs() {
		return portfolio.getPurchaseRedeemRecords().stream().map(e -> createViewData(e)).collect(Collectors.toList());
	}

	private PurchaseRedeemViewData createViewData(PurchaseRedeemRecord record) {
		return null;
	}

	private class HoldingCalculator {
		private static final double RATE_TAX = 0.001;
		private static final double RATE_COMMISSION = 0.00025;
		private static final double RATE_SH_GUOHU = 0.00002;

		private Holding holding;
		private StockData data;

		public HoldingCalculator(Holding holding) {
			this.holding = holding;
			this.data = market.getStockData(holding.getStock().getCode());
		}

		public HoldingStockViewData getViewData() {
			HoldingStockViewData vd = new HoldingStockViewData();
			vd.setCode(holding.getStock().getCode());
			vd.setName(holding.getStock().getName());
			vd.setAmount(holding.getAmount());
			vd.setCurPrice(data.getPrice());
			vd.setMarketWorth(getMarketWorth());
			vd.setEstimatedCommission(getEstimatedCommission());
			vd.setEstimatedTax(getEstimatedTax());
			vd.setNetWorth(getNetWorth());
			vd.setProportion(getProportion());
			return vd;
		}

		public double getPE() {
			return data.getPe();
		}

		public double getPB() {
			return data.getPb();
		}

		private double getMarketWorth() {
			return holding.getAmount() * data.getPrice();
		}

		private double getEstimatedCommission() {
			return Math.max(5, getMarketWorth() * RATE_COMMISSION);
		}

		private double getEstimatedTax() {
			return getMarketWorth()
					* (RATE_TAX + (holding.getStock().getExchange() == Exchange.ShangHai ? RATE_SH_GUOHU : 0));
		}

		private double getNetWorth() {
			return getMarketWorth() - getEstimatedCommission() - getEstimatedTax();
		}

		private double getProportion() {
			return getNetWorth() / PortfolioCalculator.this.getNetWorth();
		}
	}

	private class HolderCalculator {
		private PortfolioHolder holder;

		public HolderCalculator(PortfolioHolder holder) {
			this.holder = holder;
		}

		public PortfolioHolderViewData getViewData() {
			PortfolioHolderViewData vd = new PortfolioHolderViewData();
			vd.setName(holder.getHolder().getName());
			vd.setShare(holder.getShare());
			vd.setNetWorth(holder.getShare() * getNetWorthPerUnit());
			vd.setProportion(holder.getShare() / getNumberOfShares());
			vd.setRateOfReturn(vd.getNetWorth() / holder.getTotalInvestment() - 1);
			vd.setTotalInvestment(holder.getTotalInvestment());
			return vd;
		}
	}
}
