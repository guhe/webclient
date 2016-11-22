package com.guhe.webclient;

import javax.ws.rs.GET;

public class PortfolioResource {

	private String id;

	public PortfolioResource(String id) {
		this.id = id;
	}

	@GET
	public Portfolio getPortfolio() {
		Portfolio portfolio = PortfolioDao.instance.getModel().get(id);
		if (portfolio == null) {
			throw new RuntimeException("Get: Portfolio with " + id + " not found");
		}
		return portfolio;
	}
}
