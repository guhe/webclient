package com.guhe.webclient;

import java.util.List;
import java.util.stream.Collectors;

import com.guhe.webclient.Stock.Exchange;

public class PortfolioCalculator {
	private Portfolio portfolio;
	private StockMarket market;

	public PortfolioCalculator(Portfolio portfolio, StockMarket market) {
		this.portfolio = portfolio;
		this.market = market;
	}

	private double getStockTotalWorth() {
		return getHoldingCalculators().stream().map(e -> e.getMarketWorth()).reduce(0.0, Double::sum);
	}

	public double getTotalWorth() {
		return getStockTotalWorth() + portfolio.getCash();
	}

	public double getProjectedLiabilities() {
		return getHoldingCalculators().stream().map(e -> e.getEstimatedTax() + e.getEstimatedCommission()).reduce(0.0,
				Double::sum);
	}

	public double getNetWorth() {
		return getTotalWorth() - getProjectedLiabilities();
	}

	public double getStockNetWorth() {
		return getStockTotalWorth() - getProjectedLiabilities();
	}

	public double getNetWorthPerUnit() {
		return getNetWorth() / portfolio.getNumberOfShares();
	}

	public double getProportionOfStock() {
		return getStockNetWorth() / getNetWorth();
	}

	public double getRateOfReturnYear() {
		return getNetWorthPerUnit() / portfolio.getNetWorthPerUnitLastYear() - 1;
	}

	public List<HoldingCalculator> getHoldingCalculators() {
		return portfolio.getHoldings().stream().map(e -> new HoldingCalculator(e)).collect(Collectors.toList());
	}

	public class HoldingCalculator {
		private static final double RATE_TAX = 0.001;
		private static final double RATE_COMMISSION = 0.00025;
		private static final double RATE_SH_GUOHU = 0.00002;

		private Holding holding;

		HoldingCalculator(Holding holding) {
			this.holding = holding;
		}

		public Holding getHolding() {
			return holding;
		}

		public double getCurPrice() {
			return market.getPrice(holding.getStock().getCode());
		}

		public double getMarketWorth() {
			return holding.getAmount() * getCurPrice();
		}

		public double getEstimatedCommission() {
			return Math.max(5, getMarketWorth() * RATE_COMMISSION);
		}

		public double getEstimatedTax() {
			return getMarketWorth()
					* (RATE_TAX + (holding.getStock().getExchange() == Exchange.ShangHai ? RATE_SH_GUOHU : 0));
		}

		public double getNetWorth() {
			return getMarketWorth() - getEstimatedCommission() - getEstimatedTax();
		}

		public double getProportion() {
			return getNetWorth() / PortfolioCalculator.this.getNetWorth();
		}
	}
}
