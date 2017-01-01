package com.guhe.portfolio;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.guhe.portfolio.TradeRecord.BuyOrSell;

public class JpaPortfolioManager implements PortfolioManager {

	private EntityManager em;

	public JpaPortfolioManager(EntityManager em) {
		this.em = em;
	}

	public EntityManager getEm() {
		return em;
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
		em.getTransaction().begin();
		em.persist(portfolio);
		em.getTransaction().commit();
	}

	@Override
	public void deletePortfolio(String id) {
		em.getTransaction().begin();
		em.remove(em.find(Portfolio.class, id));
		em.getTransaction().commit();
	}

	private Stock getStockByCode(String stockCode) {
		TypedQuery<Stock> query = em.createQuery("FROM Stock WHERE code=:code", Stock.class);
		query.setParameter("code", stockCode);
		List<Stock> stocks = query.getResultList();
		return stocks.isEmpty() ? null : stocks.get(0);
	}

	@Override
	public void trade(String portfolioId, String stockCode, BuyOrSell buyOrSell, double price, long amount, double cost,
			Date date) {
		em.getTransaction().begin();

		Portfolio portfolio = getPortfolio(portfolioId);
		if (portfolio == null) {
			em.getTransaction().rollback();
			throw new PortfolioException("no such portfolio.");
		}
		if (buyOrSell == BuyOrSell.BUY && portfolio.getCash() - cost - amount * price < 0) {
			em.getTransaction().rollback();
			throw new PortfolioException("no enough money to buy.");
		}
		portfolio.setCash(portfolio.getCash() - amount * price - cost);

		Stock stock = getStockByCode(stockCode);
		if (stock == null) {
			em.getTransaction().rollback();
			throw new PortfolioException("no such stock code.");
		}

		TypedQuery<Holding> query = em.createQuery("FROM Holding WHERE portfolio.id=:pId AND stock.code=:code",
				Holding.class);
		query.setParameter("pId", portfolioId);
		query.setParameter("code", stockCode);
		List<Holding> holdings = query.getResultList();
		if (holdings.isEmpty()) {
			if (buyOrSell == BuyOrSell.SELL) {
				em.getTransaction().rollback();
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
				em.getTransaction().rollback();
				throw new PortfolioException("No enough holdings to sell.");
			}
			holding.setAmount(holding.getAmount() + (buyOrSell == BuyOrSell.SELL ? -amount : amount));
		}

		TradeRecord tradeRecord = new TradeRecord();
		tradeRecord.setAmount(amount);
		tradeRecord.setBuyOrSell(buyOrSell);
		tradeRecord.setDate(date);
		tradeRecord.setPortfolio(portfolio);
		tradeRecord.setPrice(price);
		tradeRecord.setStock(getStockByCode(stockCode));
		em.persist(tradeRecord);

		em.getTransaction().commit();
	}

	public void saveStock(Stock stock) {
		em.getTransaction().begin();
		em.persist(stock);
		em.getTransaction().commit();
	}

}