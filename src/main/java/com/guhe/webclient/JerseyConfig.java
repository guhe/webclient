package com.guhe.webclient;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(TencentStockMarket.class).to(StockMarket.class);
			}
		});
	}
}
