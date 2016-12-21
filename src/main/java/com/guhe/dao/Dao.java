package com.guhe.dao;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface Dao {

	Portfolio getPortfolio(String id);
	
	void createPortfolio(Portfolio portfolio);
	
	void deletePortfolio(String id);
}
