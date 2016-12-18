package com.guhe.dao.impl;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class EmfCloser implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		EmfHolder.getEmf().close();
	}

}
