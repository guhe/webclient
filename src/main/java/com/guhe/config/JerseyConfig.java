package com.guhe.config;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import com.guhe.dao.DaoManager;
import com.guhe.dao.impl.JpaDaoFactory;
import com.guhe.webclient.StockMarket;
import com.guhe.webclient.TencentStockMarket;

public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(new TencentStockMarket()).to(StockMarket.class);
				bind(new JpaDaoFactory()).to(DaoManager.class);
			}
		});
	}
}
