package com.guhe.portfolio;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.BinaryOperator;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.guhe.market.MoneyExchanger;
import com.guhe.market.MoneyName;
import com.guhe.market.StockMarket;
import com.guhe.portfolio.PurchaseRedeemRecord.PurchaseOrRedeem;
import com.guhe.portfolio.TradeRecord.BuyOrSell;
import com.guhe.util.CommonUtil;

public class JpaPortfolioManager implements PortfolioManager {
	private static final Logger LOGGER = Logger.getLogger(JpaPortfolioManager.class.getName());

	private EntityManager em;

	private StockMarket market;

	private MoneyExchanger moneyExchanger;

	public JpaPortfolioManager(EntityManager em) {
		this.em = em;
	}

	public void setMarket(StockMarket market) {
		this.market = market;
	}

	public void setMoneyExchanger(MoneyExchanger moneyExchanger) {
		this.moneyExchanger = moneyExchanger;
	}

	public EntityManager getEm() {
		return em;
	}

	@Override
	public List<Portfolio> getPortfolios() {
		return em.createQuery("FROM Portfolio", Portfolio.class).getResultList();
	}

	@Override
	public Portfolio getPortfolio(String id) {
		Portfolio p = em.find(Portfolio.class, id);
		if (p != null) {
			em.refresh(p);
		}
		return p;
	}

	@Override
	public void savePortfolio(Portfolio portfolio) {
		doInTransaction(() -> {
			em.persist(portfolio);
		});
	}

	@Override
	public void deletePortfolio(String id) {
		doInTransaction(() -> {
			em.remove(em.find(Portfolio.class, id));
		});
	}

	public Stock getStockByCode(String stockCode) {
		TypedQuery<Stock> query = em.createQuery("FROM Stock WHERE code=:code", Stock.class);
		query.setParameter("code", stockCode);
		List<Stock> stocks = query.getResultList();
		return stocks.isEmpty() ? null : stocks.get(0);
	}

	public Holder getHolderByName(String holderName) {
		TypedQuery<Holder> query = em.createQuery("FROM Holder WHERE name=:name", Holder.class);
		query.setParameter("name", holderName);
		List<Holder> holders = query.getResultList();
		return holders.isEmpty() ? null : holders.get(0);
	}

	@Override
	public void trade(String portfolioId, String stockCode, BuyOrSell buyOrSell, double price, long amount, double fee,
			double exRate, Date date, String note) {
		LOGGER.info("trade: portfolioId:" + portfolioId + ", stockCode:" + stockCode + ", buyOrSell:" + buyOrSell
				+ ", price:" + price + ", amount:" + amount + ", fee:" + fee + ", exRate:" + exRate + ", date:"
				+ CommonUtil.formatDate("yyyy-MM-dd", date));
		doInTransaction(() -> {
			Portfolio portfolio = getPortfolio(portfolioId);
			if (portfolio == null) {
				throw new PortfolioException("no such portfolio.");
			}

			Stock stock = getStockByCode(stockCode);
			if (stock == null) {
				throw new PortfolioException("no such stock code.");
			}

			if (buyOrSell == BuyOrSell.BUY) {
				updatePortfolioWithBuy(portfolio, stock, price, amount, fee, exRate);
			} else {
				updatePortfolioWithSell(portfolio, stock, price, amount, fee, exRate);
			}

			TradeRecord tradeRecord = new TradeRecord();
			tradeRecord.setAmount(amount);
			tradeRecord.setBuyOrSell(buyOrSell);
			tradeRecord.setDate(date);
			tradeRecord.setPortfolio(portfolio);
			tradeRecord.setPrice(CommonUtil.dRound(price, 2));
			tradeRecord.setFee(CommonUtil.dRound(fee, 2));
			tradeRecord.setExRate(exRate);
			tradeRecord.setStock(getStockByCode(stockCode));
			tradeRecord.setNote(note);
			em.persist(tradeRecord);
		});
	}

	private void updatePortfolioWithBuy(Portfolio portfolio, Stock stock, double price, long amount, double fee,
			double exRate) {
		portfolio.addCashByName(stock.getExchange().getTradeMoneyName(),
				CommonUtil.dRound((-amount * price - fee) * exRate, 2));

		if (CommonUtil.dCompare(portfolio.getCashByName(stock.getExchange().getTradeMoneyName()), 0, 2) < 0) {
			throw new PortfolioException("no enough money to buy.");
		}

		Holding holding = portfolio.getHoldings().stream().filter(e -> e.getStock().getCode().equals(stock.getCode()))
				.findFirst().orElse(null);
		if (holding == null) {
			holding = new Holding();
			holding.setStock(stock);
			holding.setPortfolio(portfolio);
			holding.setAmount(amount);
			em.persist(holding);
		} else {
			holding.setAmount(holding.getAmount() + amount);
		}
	}

	private void updatePortfolioWithSell(Portfolio portfolio, Stock stock, double price, long amount, double fee,
			double exRate) {
		portfolio.addCashByName(stock.getExchange().getTradeMoneyName(),
				CommonUtil.dRound((amount * price - fee) * exRate, 2));

		List<Holding> holdings = portfolio.getHoldings();
		Holding holding = holdings.stream().filter(e -> e.getStock().getCode().equals(stock.getCode())).findFirst()
				.orElse(null);
		if (holding == null) {
			throw new PortfolioException("no such holding to sell.");
		}

		if (holding.getAmount() < amount) {
			throw new PortfolioException("No enough holdings to sell.");
		} else if (holding.getAmount() == amount) {
			holdings.remove(holding);
			em.remove(holding);
		} else {
			holding.setAmount(holding.getAmount() - amount);
		}
	}

	@Override
	public void purchase(String portfolioId, String holderName, double money, double netWorth, double fee, Date date) {
		LOGGER.info("purchase: portfolioId:" + portfolioId + ", holderName:" + holderName + ", money:" + money
				+ ", netWorth:" + netWorth + ", fee:" + fee + ", date:" + CommonUtil.formatDate("yyyy-MM-dd", date));
		doInTransaction(() -> {
			Portfolio portfolio = getPortfolio(portfolioId);
			if (portfolio == null) {
				throw new PortfolioException("no such portfolio.");
			}

			Holder holder = getHolderByName(holderName);
			if (holder == null) {
				throw new PortfolioException("no such holder name.");
			}

			portfolio.setRmbCash(CommonUtil.dRound(portfolio.getRmbCash() + money - fee, 2));

			PortfolioHolder ph = portfolio.getHolders().stream().filter(e -> e.getHolder().getName().equals(holderName))
					.findFirst().orElse(null);
			double share = (money - fee) / netWorth;
			if (ph == null) {
				ph = new PortfolioHolder();
				ph.setHolder(holder);
				ph.setPortfolio(portfolio);
				ph.setTotalInvestment(CommonUtil.dRound(money, 2));
				ph.setShare(CommonUtil.dRound(share, 2));
				em.persist(ph);
			} else {
				ph.setTotalInvestment(CommonUtil.dRound(ph.getTotalInvestment() + money, 2));
				ph.setShare(CommonUtil.dRound(ph.getShare() + share, 2));
			}

			savePurchaseRedeemRecord(portfolio, holder, PurchaseOrRedeem.PURCHASE, share, netWorth, fee, date);
		});
	}

	@Override
	public void redeem(String portfolioId, String holderName, double share, double netWorth, double fee, Date date) {
		LOGGER.info("redeem: portfolioId:" + portfolioId + ", holderName:" + holderName + ", share:" + share
				+ ", netWorth:" + netWorth + ", fee:" + fee + ", date:" + CommonUtil.formatDate("yyyy-MM-dd", date));
		doInTransaction(() -> {
			Portfolio portfolio = getPortfolio(portfolioId);
			if (portfolio == null) {
				throw new PortfolioException("no such portfolio.");
			}

			Holder holder = getHolderByName(holderName);
			if (holder == null) {
				throw new PortfolioException("no such holder name.");
			}

			double money = share * netWorth - fee;
			if (CommonUtil.dCompare(portfolio.getRmbCash(), money, 2) < 0) {
				throw new PortfolioException("No enough cash to redeem.");
			}
			portfolio.setRmbCash(CommonUtil.dRound(portfolio.getRmbCash() - share * netWorth, 2));

			PortfolioHolder ph = portfolio.getHolders().stream().filter(e -> e.getHolder().getName().equals(holderName))
					.findFirst().orElse(null);
			if (ph == null) {
				throw new PortfolioException("no such holder.");
			}

			if (CommonUtil.dCompare(ph.getShare(), share, 2) < 0) {
				throw new PortfolioException("No enough share to redeem.");
			} else if (CommonUtil.dCompare(ph.getShare(), share, 2) == 0) {
				portfolio.getHolders().remove(ph);
				em.remove(ph);
			} else {
				ph.setShare(CommonUtil.dRound(ph.getShare() - share, 2));
				ph.setTotalInvestment(ph.getTotalInvestment() - money);
			}

			savePurchaseRedeemRecord(portfolio, holder, PurchaseOrRedeem.REDEEM, share, netWorth, fee, date);
		});
	}

	private void savePurchaseRedeemRecord(Portfolio portfolio, Holder holder, PurchaseOrRedeem purchaseOrRedeem,
			double share, double netWorth, double fee, Date date) {
		PurchaseRedeemRecord record = new PurchaseRedeemRecord();
		record.setPortfolio(portfolio);
		record.setHolder(holder);
		record.setPurchaseOrRedeem(purchaseOrRedeem);
		record.setShare(CommonUtil.dRound(share, 2));
		record.setNetWorthPerUnit(CommonUtil.dRound(netWorth, 4));
		record.setFee(CommonUtil.dRound(fee, 4));
		record.setDate(date);
		em.persist(record);
	}

	public List<DailyData> getDailyData(String portfolioId, Date startDate, Date endDate) {
		String startDay = CommonUtil.formatDate("yyyy-MM-dd", startDate);
		String endDay = CommonUtil.formatDate("yyyy-MM-dd", endDate);

		if (startDay.compareTo(endDay) > 0) {
			throw new PortfolioException("start day is after end day.");
		}

		Portfolio portfolio = getPortfolio(portfolioId);
		if (portfolio == null) {
			throw new PortfolioException("no such portfolio.");
		}

		return portfolio.getDailyDatas().stream().filter(e -> {
			String curDay = CommonUtil.formatDate("yyyy-MM-dd", e.getDate());
			return curDay.compareTo(startDay) >= 0 && curDay.compareTo(endDay) <= 0;
		}).sorted((e1, e2) -> {
			return e1.getDate().compareTo(e2.getDate());
		}).collect(Collectors.toList());
	}

	@Override
	public void supplementDailyData(String portfolioId, Date endDate) {
		LOGGER.info("supplement daily data, portfolioId:" + portfolioId + ", endDate:" + endDate);
		doInTransaction(() -> {
			Portfolio portfolio = getPortfolio(portfolioId);
			if (portfolio == null) {
				throw new PortfolioException("no such portfolio.");
			}

			Calendar startDay = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
			startDay.setTime(portfolio.getCreatedTime());
			portfolio.getDailyDatas().stream().reduce(BinaryOperator.maxBy((e1, e2) -> {
				return e1.getDate().compareTo(e2.getDate());
			})).ifPresent(e -> {
				startDay.setTime(e.getDate());
				startDay.add(Calendar.DAY_OF_MONTH, 1);
			});
			CommonUtil.clearToDay(startDay);

			Calendar endDay = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
			endDay.setTime(endDate);
			CommonUtil.clearToDay(endDay);

			if (endDay.before(startDay)) {
				LOGGER.info("no daily data need be supplemented, portfolioId:" + portfolioId + ", endDate:" + endDate);
				return;
			}

			Map<Calendar, List<PurchaseRedeemRecord>> prRecords = portfolio.getPurchaseRedeemRecords().stream()
					.filter(e -> {
						Calendar day = date2CalendarDay(e.getDate());
						return day.after(startDay) && day.before(endDay) || day.equals(endDay);
					}).collect(Collectors.groupingBy(e -> {
						return date2CalendarDay(e.getDate());
					}));

			Map<Calendar, List<TradeRecord>> tradeRecords = portfolio.getTradeRecords().stream().filter(e -> {
				Calendar day = date2CalendarDay(e.getDate());
				return (day.after(startDay) && day.before(endDay)) || day.equals(endDay);
			}).collect(Collectors.groupingBy(e -> {
				return date2CalendarDay(e.getDate());
			}));

			Map<Calendar, List<ExchangeMoneyRecord>> emRecords = portfolio.getExchangeMoneyRecords().stream()
					.filter(e -> {
						Calendar day = date2CalendarDay(e.getDate());
						return (day.after(startDay) && day.before(endDay)) || day.equals(endDay);
					}).collect(Collectors.groupingBy(e -> {
						return date2CalendarDay(e.getDate());
					}));

			Map<Calendar, List<ModifyCashRecord>> mcRecords = portfolio.getModifyCashRecords().stream().filter(e -> {
				Calendar day = date2CalendarDay(e.getDate());
				return (day.after(startDay) && day.before(endDay)) || day.equals(endDay);
			}).collect(Collectors.groupingBy(e -> {
				return date2CalendarDay(e.getDate());
			}));

			Portfolio clonedPortfolio = portfolio.clone();

			for (Calendar day = endDay; day.after(startDay) || day.equals(startDay); day.add(Calendar.DAY_OF_MONTH,
					-1)) {
				if (market.isOpen(day)) {
					DailyData dd = calcDailyData(clonedPortfolio, day);
					em.persist(dd);
				}
				undoModifyCash(clonedPortfolio, mcRecords.get(day));
				undoPurchaseRedeem(clonedPortfolio, prRecords.get(day));
				undoExchangeMoney(clonedPortfolio, emRecords.get(day));
				undoTrade(clonedPortfolio, tradeRecords.get(day));
			}
		});
	}

	private void undoModifyCash(Portfolio portfolio, List<ModifyCashRecord> mcRecords) {
		if (mcRecords == null) {
			return;
		}

		mcRecords.forEach(e -> {
			portfolio.addCashByName(e.getTarget(), e.getOldAmount() - e.getNewAmount());
		});
	}

	private void undoExchangeMoney(Portfolio portfolio, List<ExchangeMoneyRecord> emRecords) {
		if (emRecords == null) {
			return;
		}

		emRecords.forEach(emr -> {
			portfolio.addCashByName(emr.getTarget(), -emr.getTargetAmount());
			portfolio.addRmbCash(-emr.getRmbAmount());
		});
	}

	private Calendar date2CalendarDay(Date date) {
		Calendar day = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
		day.setTime(date);
		CommonUtil.clearToDay(day);
		return day;
	}

	private void undoTrade(Portfolio portfolio, List<TradeRecord> tradeRecord) {
		if (tradeRecord == null) {
			return;
		}
		tradeRecord.forEach(tr -> {
			Holding holding = portfolio.getHoldings().stream().filter(hg -> hg.getStock().equals(tr.getStock()))
					.findAny().orElse(null);
			if (holding == null) {
				holding = new Holding();
				holding.setStock(tr.getStock());
				portfolio.getHoldings().add(holding);
			}

			if (tr.getBuyOrSell() == TradeRecord.BuyOrSell.BUY) {
				portfolio.addCashByName(tr.getStock().getExchange().getTradeMoneyName(),
						(tr.getAmount() * tr.getPrice() + tr.getFee()) * tr.getExRate());
				holding.addAmount(-tr.getAmount());
			} else {
				portfolio.addCashByName(tr.getStock().getExchange().getTradeMoneyName(),
						(tr.getFee() - tr.getAmount() * tr.getPrice()) * tr.getExRate());
				holding.addAmount(tr.getAmount());
			}

			if (holding.getAmount() == 0) {
				portfolio.getHoldings().remove(holding);
			}
		});
	}

	private void undoPurchaseRedeem(Portfolio portfolio, List<PurchaseRedeemRecord> prRecord) {
		if (prRecord == null) {
			return;
		}
		prRecord.forEach(prr -> {
			PortfolioHolder ph = portfolio.getHolders().stream().filter(h -> h.getHolder().equals(prr.getHolder()))
					.findAny().orElse(null);
			if (ph == null) {
				ph = new PortfolioHolder();
				ph.setHolder(prr.getHolder());
				portfolio.getHolders().add(ph);
			}

			if (prr.getPurchaseOrRedeem() == PurchaseRedeemRecord.PurchaseOrRedeem.PURCHASE) {
				portfolio.addRmbCash(-prr.getShare() * prr.getNetWorthPerUnit());
				ph.addShare(-prr.getShare());
				ph.addInvestment(-prr.getFee() - prr.getShare() * prr.getNetWorthPerUnit());
			} else {
				portfolio.addRmbCash(prr.getShare() * prr.getNetWorthPerUnit());
				ph.addShare(prr.getShare());
				ph.addInvestment(-prr.getFee() + prr.getShare() * prr.getNetWorthPerUnit());
			}
		});
	}

	private DailyData calcDailyData(Portfolio portfolio, Calendar day) {
		DailyData dd = new DailyData();
		dd.setPortfolio(portfolio);
		dd.setDate(day.getTime());
		PortfolioCalculator calculator = new PortfolioCalculator(portfolio, market, moneyExchanger, day);
		dd.setNetWorth(calculator.getNetWorth());
		dd.setNetWorthPerUnit(calculator.getNetWorthPerUnit());
		dd.setProportionOfStock(calculator.getProportionOfStock());
		dd.setShare(calculator.getNumberOfShares());
		dd.setGrowthRate(0);// TODO
		dd.setPb(0); // TODO
		dd.setPe(0); // TODO
		return dd;
	}

	@Override
	public void exchangeMoney(String portfolioId, MoneyName target, double targetAmount, double rmbAmount, Date date) {
		LOGGER.info("exchange money, portfolioId: " + portfolioId + ", target: " + target + "targetAmount: "
				+ targetAmount + ", rmbAmount: " + rmbAmount + ", date: " + date);

		if (target == MoneyName.RMB) {
			throw new PortfolioException("Can not exchange for same money. portfolioId: " + portfolioId);
		}

		if (targetAmount * rmbAmount >= 0) {
			throw new PortfolioException("Both money are reduced or increased. portfolioId: " + portfolioId
					+ ", targetAmount: " + targetAmount + ", rmbAmount: " + rmbAmount);
		}

		doInTransaction(() -> {
			Portfolio portfolio = getPortfolio(portfolioId);
			if (portfolio == null) {
				throw new PortfolioException("no such portfolio.");
			}

			checkEnoughCash(portfolio, MoneyName.RMB, rmbAmount);
			checkEnoughCash(portfolio, target, targetAmount);

			portfolio.addCashByName(MoneyName.RMB, rmbAmount);
			portfolio.addCashByName(target, targetAmount);

			ExchangeMoneyRecord record = new ExchangeMoneyRecord();
			record.setPortfolio(portfolio);
			record.setTarget(target);
			record.setTargetAmount(targetAmount);
			record.setRmbAmount(rmbAmount);
			record.setDate(date);
			em.persist(record);
		});
	}

	private void checkEnoughCash(Portfolio portfolio, MoneyName name, double amount) {
		if (CommonUtil.dCompare(portfolio.getCashByName(name) + amount, 0, 2) < 0) {
			throw new PortfolioException("No enough money to exchange. portfolioId: " + portfolio.getId() + ", Money: "
					+ name + ", current: " + portfolio.getCashByName(name) + ", required: " + amount);
		}
	}

	@Override
	public void modifyCash(String portfolioId, MoneyName target, double amount, Date date) {
		LOGGER.info("modify cash, portfolioId: " + portfolioId + ", target: " + target + "amount: " + amount
				+ ", date: " + date);

		if (amount < 0) {
			throw new PortfolioException("amount can not be less than 0.");
		}

		doInTransaction(() -> {
			Portfolio portfolio = getPortfolio(portfolioId);
			if (portfolio == null) {
				throw new PortfolioException("no such portfolio.");
			}

			ModifyCashRecord record = new ModifyCashRecord();
			record.setPortfolio(portfolio);
			record.setTarget(target);
			record.setNewAmount(amount);
			record.setOldAmount(portfolio.getCashByName(target));
			record.setDate(date);
			em.persist(record);

			portfolio.setCashByName(target, amount);
		});
	}

	private void doInTransaction(Runnable r) {
		em.getTransaction().begin();
		try {
			r.run();
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

}