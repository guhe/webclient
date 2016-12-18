package com.guhe.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import com.guhe.dao.DaoManager;
import com.guhe.dao.impl.JpaDaoManager;
import com.guhe.webclient.StockMarket;
import com.guhe.webclient.TencentStockMarket;

@ApplicationPath("rest")
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		packages("com.guhe.webclient");

		register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(new TencentStockMarket()).to(StockMarket.class);
				bind(new JpaDaoManager()).to(DaoManager.class);
			}
		});
	}
}
