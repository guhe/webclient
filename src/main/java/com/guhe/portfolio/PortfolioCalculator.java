package com.guhe.portfolio;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guhe.market.Exchange;
import com.guhe.market.MoneyExchanger;
import com.guhe.market.MoneyName;
import com.guhe.market.StockData;
import com.guhe.market.StockMarket;

public class PortfolioCalculator {
	private Portfolio portfolio;
	private StockMarket market;
	private MoneyExchanger moneyExchanger;
	private Calendar day;
	
	public static final double GRID_SIZE = 0.08;

	public PortfolioCalculator(Portfolio portfolio, StockMarket market, MoneyExchanger exchanger) {
		this(portfolio, market, exchanger, null);
	}

	public PortfolioCalculator(Portfolio portfolio, StockMarket market, MoneyExchanger moneyExchanger, Calendar day) {
		this.portfolio = portfolio;
		this.market = market;
		this.moneyExchanger = moneyExchanger;
		this.day = day;
	}

	public double getCash() {
		return portfolio.getRmbCash() + portfolio.getUsdCash() * moneyExchanger.getMoneyPrice(MoneyName.USD).getBuy()
				+ portfolio.getHkdCash() * moneyExchanger.getMoneyPrice(MoneyName.HKD).getBuy();
	}

	public double getNumberOfShares() {
		return portfolio.getHolders().stream().map(e -> e.getShare()).reduce(0.0, Double::sum);
	}

	public double getStockTotalWorth() {
		return getHoldingCalculatorStream().map(e -> e.getMarketWorth()).reduce(0.0, Double::sum);
	}

	public double getTotalWorth() {
		return getStockTotalWorth() + getCash();
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
		private Holding holding;
		private StockData data;

		public HoldingCalculator(Holding holding) {
			this.holding = holding;
			
			data = market.getStockData(holding.getStock().getCode(), day);
			if (data == null && day != null) {
				Calendar yesterday = (Calendar) day.clone();
				do {
					yesterday.add(Calendar.DAY_OF_YEAR, -1);
					data = market.getStockData(holding.getStock().getCode(), yesterday);
				} while (data == null);
			}
			if (data == null) {
				throw new RuntimeException("no stock data, stock: " + holding.getStock().getCode() + ", day: " + day);
			}
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
			return holding.getAmount() * data.getPrice() * getMoneyPrice();
		}

		private double getMoneyPrice() {
			if (holding.getStock().getExchange().getMoneyName() == MoneyName.RMB) {
				return 1;
			} else {
				return moneyExchanger.getMoneyPrice(holding.getStock().getExchange().getMoneyName()).getBuy();
			}
		}

		public double getEstimatedCommission() {
			return holding.getStock().getExchange().getBrokerage(getMarketWorth());
		}

		public double getEstimatedTax() {
			Exchange ex = holding.getStock().getExchange();
			double amount = getMarketWorth();
			return ex.getTax(amount) + ex.getTransferFee(amount)+ex.getSettlementFee(amount);
		}

		public double getNetWorth() {
			return getMarketWorth() - getEstimatedCommission() - getEstimatedTax();
		}

		public double getProportion() {
			return getNetWorth() / PortfolioCalculator.this.getNetWorth();
		}

		public List<TradeRecord> getTradeRecords() {
			return holding.getPortfolio().getTradeRecords().stream()
					.filter(e -> e.getStock().getCode().equals(holding.getStock().getCode()))
					.collect(Collectors.toList());
		}
	}

	public class HolderCalculator {
		private PortfolioHolder holder;

		public HolderCalculator(PortfolioHolder holder) {
			this.holder = holder;
		}

		public PortfolioHolder getPortfolioHolder() {
			return holder;
		}

		public double getNetWorth() {
			return holder.getShare() * getNetWorthPerUnit();
		}

		public double getProportion() {
			return holder.getShare() / getNumberOfShares();
		}

		public double getRateOfReturn() {
			return getNetWorth() / holder.getTotalInvestment() - 1;
		}
	}
}
