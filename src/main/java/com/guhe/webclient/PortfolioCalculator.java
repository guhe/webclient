package com.guhe.webclient;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guhe.portfolio.Holding;
import com.guhe.portfolio.Portfolio;
import com.guhe.portfolio.Stock.Exchange;

public class PortfolioCalculator {
	private Portfolio portfolio;
	private StockMarket market;

	public PortfolioCalculator(Portfolio portfolio, StockMarket market) {
		this.portfolio = portfolio;
		this.market = market;
	}

	private double getStockTotalWorth() {
		return getHoldingCalculatorStream().map(e -> e.getMarketWorth()).reduce(0.0, Double::sum);
	}

	public double getTotalWorth() {
		return getStockTotalWorth() + portfolio.getCash();
	}

	public double getProjectedLiabilities() {
		return getHoldingCalculatorStream().map(e -> e.getEstimatedTax() + e.getEstimatedCommission()).reduce(0.0,
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

	public double getPE() {
		return getStockTotalWorth()
				/ getHoldingCalculatorStream().map(e -> e.getMarketWorth() / e.getPE()).reduce(0.0, Double::sum);
	}

	public double getPB() {
		return getStockTotalWorth()
				/ getHoldingCalculatorStream().map(e -> e.getMarketWorth() / e.getPB()).reduce(0.0, Double::sum);
	}

	public List<HoldingCalculator> getHoldingCalculators() {
		return getHoldingCalculatorStream().collect(Collectors.toList());
	}

	private Stream<HoldingCalculator> getHoldingCalculatorStream() {
		return portfolio.getHoldings().stream().map(e -> new HoldingCalculator(e));
	}

	public List<TradeCalculator> getTradeRecords() {
		return null;
	}
	
	public class HoldingCalculator {
		private static final double RATE_TAX = 0.001;
		private static final double RATE_COMMISSION = 0.00025;
		private static final double RATE_SH_GUOHU = 0.00002;

		private Holding holding;
		private StockData data;

		HoldingCalculator(Holding holding) {
			this.holding = holding;
			this.data = market.getStockData(holding.getStock().getCode());
		}

		public double getPE() {
			return data.getPe();
		}

		public double getPB() {
			return data.getPb();
		}

		public Holding getHolding() {
			return holding;
		}

		public double getCurPrice() {
			return data.getPrice();
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
	
	public class TradeCalculator {

	}

}
