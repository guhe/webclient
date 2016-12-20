package com.guhe.dao;

import java.util.List;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface Dao {

	List<Portfolio> getPortfolios();

	Portfolio getPortfolio(String id);
}
