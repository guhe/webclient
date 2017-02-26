package com.guhe.config;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

import com.guhe.market.StockMarket;
import com.guhe.market.TencentStockMarket;
import com.guhe.portfolio.PortfolioManager;

@ApplicationPath("rest")
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		packages("com.guhe.webclient");

		register(ImmediateFeature.class);

		register(new AbstractBinder() {
			@Override
			protected void configure() { 
				bind(TencentStockMarket.class).to(StockMarket.class).in(Singleton.class);
				bindFactory(EmfFactory.class).to(EntityManagerFactory.class).in(Immediate.class);
				bindFactory(PortfolioManagerFactory.class).to(PortfolioManager.class).in(RequestScoped.class);
			}
		});
	}
}

class ImmediateFeature implements Feature {

	@Inject
	private ServiceLocator locator;

	@Override
	public boolean configure(FeatureContext context) {
		ServiceLocatorUtilities.enableImmediateScope(locator);
		return true;
	}
}