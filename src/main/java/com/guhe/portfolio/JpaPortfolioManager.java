package com.guhe.portfolio;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.guhe.portfolio.PurchaseRedeemRecord.PurchaseOrRedeem;
import com.guhe.portfolio.TradeRecord.BuyOrSell;
import com.guhe.util.CommonUtil;

public class JpaPortfolioManager implements PortfolioManager {
	private static final Logger LOGGER = Logger.getLogger(JpaPortfolioManager.class.getName());

	private EntityManager em;

	public JpaPortfolioManager(EntityManager em) {
		this.em = em;
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
			Date date) {
		LOGGER.info("trade: portfolioId:" + portfolioId + ", stockCode:" + stockCode + ", buyOrSell:" + buyOrSell
				+ ", price:" + price + ", amount:" + amount + ", fee:" + fee + ", date:"
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

			if (buyOrSell == BuyOrSell.BUY && portfolio.getCash() - fee - amount * price < 0) {
				throw new PortfolioException("no enough money to buy.");
			}
			portfolio.setCash(CommonUtil
					.dRound(portfolio.getCash() - (buyOrSell == BuyOrSell.SELL ? -amount : amount) * price - fee, 2));

			TypedQuery<Holding> query = em.createQuery("FROM Holding WHERE portfolio.id=:pId AND stock.code=:code",
					Holding.class);
			query.setParameter("pId", portfolioId);
			query.setParameter("code", stockCode);
			List<Holding> holdings = query.getResultList();
			if (holdings.isEmpty()) {
				if (buyOrSell == BuyOrSell.SELL) {
					throw new PortfolioException("no such holding to sell.");
				}
				Holding holding = new Holding();
				holding.setStock(stock);
				holding.setPortfolio(portfolio);
				holding.setAmount(amount);
				em.persist(holding);
			} else {
				Holding holding = holdings.get(0);
				if (buyOrSell == BuyOrSell.SELL && holding.getAmount() < amount) {
					throw new PortfolioException("No enough holdings to sell.");
				}
				holding.setAmount(holding.getAmount() + (buyOrSell == BuyOrSell.SELL ? -amount : amount));
			}

			TradeRecord tradeRecord = new TradeRecord();
			tradeRecord.setAmount(amount);
			tradeRecord.setBuyOrSell(buyOrSell);
			tradeRecord.setDate(date);
			tradeRecord.setPortfolio(portfolio);
			tradeRecord.setPrice(CommonUtil.dRound(price, 2));
			tradeRecord.setFee(CommonUtil.dRound(fee, 2));
			tradeRecord.setStock(getStockByCode(stockCode));
			em.persist(tradeRecord);
		});
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

			portfolio.setCash(CommonUtil.dRound(portfolio.getCash() + money, 2));

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

			PurchaseRedeemRecord record = new PurchaseRedeemRecord();
			record.setPortfolio(portfolio);
			record.setHolder(holder);
			record.setPurchaseOrRedeem(PurchaseOrRedeem.PURCHASE);
			record.setShare(CommonUtil.dRound(share, 2));
			record.setNetWorth(CommonUtil.dRound(netWorth, 4));
			record.setFee(CommonUtil.dRound(fee, 4));
			record.setDate(date);
			em.persist(record);
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
			if (CommonUtil.dCompare(portfolio.getCash(), money, 2) < 0) {
				throw new PortfolioException("No enough cash to redeem.");
			}
			portfolio.setCash(CommonUtil.dRound(portfolio.getCash() - money, 2));

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
			}

			PurchaseRedeemRecord record = new PurchaseRedeemRecord();
			record.setPortfolio(portfolio);
			record.setHolder(holder);
			record.setPurchaseOrRedeem(PurchaseOrRedeem.REDEEM);
			record.setShare(CommonUtil.dRound(share, 2));
			record.setNetWorth(CommonUtil.dRound(netWorth, 4));
			record.setFee(CommonUtil.dRound(fee, 4));
			record.setDate(date);
			em.persist(record);
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