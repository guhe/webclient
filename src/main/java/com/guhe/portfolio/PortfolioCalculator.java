package com.guhe.portfolio;

import java.util.stream.Stream;

import com.guhe.market.StockData;
import com.guhe.market.StockMarket;

public class PortfolioCalculator {
	private Portfolio portfolio;
	private StockMarket market;

	public PortfolioCalculator(Portfolio portfolio, StockMarket market) {
		this.portfolio = portfolio;
		this.market = market;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public double getNumberOfShares() {
		return portfolio.getHolders().stream().map(e -> e.getShare()).reduce(0.0, Double::sum);
	}

	public double getStockTotalWorth() {
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

	public double getTotalInvestment() {
		return portfolio.getHolders().stream().map(e -> e.getTotalInvestment()).reduce(0.0, Double::sum);
	}

	public double getStockNetWorth() {
		return getStockTotalWorth() - getProjectedLiabilities();
	}

	public double getNetWorthPerUnit() {
		return getNetWorth() / getNumberOfShares();
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

	public Stream<HoldingCalculator> getHoldingCalculatorStream() {
		return portfolio.getHoldings().stream().map(e -> new HoldingCalculator(e));
	}

	public Stream<HolderCalculator> getHolderCalculatorStream() {
		return portfolio.getHolders().stream().map(e -> new HolderCalculator(e));
	}

	public class HoldingCalculator {
		private static final double RATE_TAX = 0.001;
		private static final double RATE_COMMISSION = 0.00025;
		private static final double RATE_SH_GUOHU = 0.00002;

		private Holding holding;
		private StockData data;

		public HoldingCalculator(Holding holding) {
			this.holding = holding;
			this.data = market.getStockData(holding.getStock().getCode());
		}

		public Holding getHolding() {
			return holding;
		}

		public double getPE() {
			return data.getPe();
		}

		public double getPB() {
			return data.getPb();
		}

		public double getPrice() {
			return data.getPrice();
		}

		public double getMarketWorth() {
			return holding.getAmount() * data.getPrice();
		}

		public double getEstimatedCommission() {
			return Math.max(5, getMarketWorth() * RATE_COMMISSION);
		}

		public double getEstimatedTax() {
			return getMarketWorth()
					* (RATE_TAX + (holding.getStock().getExchange() == Stock.Exchange.ShangHai ? RATE_SH_GUOHU : 0));
		}

		public double getNetWorth() {
			return getMarketWorth() - getEstimatedCommission() - getEstimatedTax();
		}

		public double getProportion() {
			return getNetWorth() / PortfolioCalculator.this.getNetWorth();
		}
	}

	public class HolderCalculator {
		private PortfolioHolder holder;

		public HolderCalculator(PortfolioHolder holder) {
			this.holder = holder;
		}
		
		public PortfolioHolder getPortfolioHolder(){
			return holder;
		}
		
		public double getNetWorth(){
			return holder.getShare() * getNetWorthPerUnit();
		}

		public double getProportion(){
			return holder.getShare() / getNumberOfShares();
		}
		
		public double getRateOfReturn(){
			return getNetWorth() / holder.getTotalInvestment() - 1;
		}
	}
}
