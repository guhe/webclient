package com.guhe.dao;

import java.util.List;

public interface Dao {

	List<Portfolio> getPortfolios();

	Portfolio getPortfolio(String id);
}
