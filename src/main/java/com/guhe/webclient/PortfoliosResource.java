package com.guhe.webclient;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/Portfolios")
public class PortfoliosResource {

	@GET
	public List<Portfolio> getPortfolios() {
		List<Portfolio> portfolios = new ArrayList<Portfolio>();
		portfolios.addAll(Dao.instance.getPortfolios());
		return portfolios;
	}

	@GET
	@Path("{portfolio}")
	public Portfolio getPortfolio(@PathParam("portfolio") String id) {
		return Dao.instance.getPortfolio(id);
	}

	@GET
	@Path("{portfolio}/HoldingStocks")
	public List<HoldingStock> getHoldingStocks(@PathParam("portfolio") String portfolioId) {
		return Dao.instance.getHoldingStocks(portfolioId);
	}
}
