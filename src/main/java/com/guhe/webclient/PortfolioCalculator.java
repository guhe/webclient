package com.guhe.webclient;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guhe.portfolio.Holding;
import com.guhe.portfolio.Portfolio;
import com.guhe.portfolio.PortfolioHolder;
import com.guhe.portfolio.Stock.Exchange;

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
		return getNetWorth() / portfolio.getNumberOfShares();
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
		return getHoldingCalculatorStream().map(e->e.getViewData()).collect(Collectors.toList());
	}

	private Stream<HoldingCalculator> getHoldingCalculatorStream() {
		return portfolio.getHoldings().stream().map(e -> new HoldingCalculator(e));
	}

	public List<PortfolioHolderViewData> getHolderVDs() {
		return getHolderCalculatorStream().map(e->e.getViewData()).collect(Collectors.toList());
	}

	private Stream<HolderCalculator> getHolderCalculatorStream() {
		return portfolio.getHolders().stream().map(e -> new HolderCalculator(e));
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
			HoldingStockViewData viewData = new HoldingStockViewData();
			viewData.setCode(holding.getStock().getCode());
			viewData.setName(holding.getStock().getName());
			viewData.setAmount(holding.getAmount());
			viewData.setCurPrice(data.getPrice());
			viewData.setMarketWorth(getMarketWorth());
			viewData.setEstimatedCommission(getEstimatedCommission());
			viewData.setEstimatedTax(getEstimatedTax());
			viewData.setNetWorth(getNetWorth());
			viewData.setProportion(getProportion());
			return viewData;
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
			vd.setProportion(holder.getShare()/portfolio.getNumberOfShares());
			vd.setRateOfReturn(vd.getNetWorth() / holder.getTotalInvestment() - 1);
			vd.setTotalInvestment(holder.getTotalInvestment());
			return vd;
		}
	}
}
