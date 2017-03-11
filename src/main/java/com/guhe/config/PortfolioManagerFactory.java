package com.guhe.config;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;

import org.glassfish.hk2.api.Factory;

import com.guhe.market.MoneyExchanger;
import com.guhe.market.StockMarket;
import com.guhe.portfolio.JpaPortfolioManager;
import com.guhe.portfolio.PortfolioManager;

class PortfolioManagerFactory implements Factory<PortfolioManager> {

	private static final Logger LOGGER = Logger.getLogger(PortfolioManagerFactory.class.getName());

	@Inject
	private HttpServletRequest httpReq;
	
	@Inject
	private EntityManagerFactory emf;
	
	@Inject
	private StockMarket market;
	
	@Inject
	private MoneyExchanger moneyExchanger;

	@Override
	public PortfolioManager provide() {
		JpaPortfolioManager pm = new JpaPortfolioManager(emf.createEntityManager());
		pm.setMarket(market);
		pm.setMoneyExchanger(moneyExchanger);
		LOGGER.info("JPA EM is created for request " + getReqUrl());
		return pm;
	}

	@Override
	public void dispose(PortfolioManager instance) {
		JpaPortfolioManager pm = (JpaPortfolioManager) instance;
		pm.getEm().close();
		LOGGER.info("JPA EM is closed for request " + getReqUrl());
	}

	private String getReqUrl() {
		String url = httpReq.getServletPath();
		if (httpReq.getQueryString() != null) {
			url += "?" + httpReq.getQueryString();
		}
		return url;
	}

}
