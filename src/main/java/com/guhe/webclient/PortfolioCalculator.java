package com.guhe.webclient;

import java.util.List;
import java.util.stream.Collectors;

public class PortfolioCalculator {
	private static final double RATE_TAX = 0.001;
	private static final double RATE_COMMISSION = 0.00025;

	private Portfolio portfolio;
	private StockMarket market;

	public PortfolioCalculator(Portfolio portfolio, StockMarket market) {
		this.portfolio = portfolio;
		this.market = market;
	}

	private double getStockTotalWorth() {
		double stockTotalWorth = 0;
		for (Holding holding : portfolio.getHoldings()) {
			stockTotalWorth += holding.getAmount() * market.getPrice(holding.getStock().getCode());
		}
		return stockTotalWorth;
	}

	public double getTotalWorth() {
		return getStockTotalWorth() + portfolio.getCash();
	}

	public double getProjectedLiabilities() {
		return getStockTotalWorth() * (RATE_COMMISSION + RATE_TAX);
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
			return getMarketWorth() * RATE_COMMISSION;
		}

		public double getEstimatedTax() {
			return getMarketWorth() * RATE_TAX;
		}

		public double getNetWorth() {
			return getMarketWorth() * (1 - RATE_COMMISSION - RATE_TAX);
		}

		public double getProportion() {
			return getNetWorth() / PortfolioCalculator.this.getNetWorth();
		}
	}
}
