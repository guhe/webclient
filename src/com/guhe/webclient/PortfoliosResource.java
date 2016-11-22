package com.guhe.webclient;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/portfolios")
public class PortfoliosResource {

	@GET
	public List<Portfolio> getPortfolios() {
		List<Portfolio> portfolios = new ArrayList<Portfolio>();
		portfolios.addAll(PortfolioDao.instance.getModel().values());
		return portfolios;
	}

	@Path("{portfolio}")
	public PortfolioResource getPortfolio(@PathParam("portfolio") String id) {
		return new PortfolioResource(id);
	}
}
