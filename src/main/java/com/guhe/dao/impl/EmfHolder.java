package com.guhe.dao.impl;

import java.util.Collections;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

abstract class EmfHolder {

	private static final Logger LOGGER = Logger.getLogger(EmfHolder.class.getName());
	
	private static EntityManagerFactory emf;

	public static synchronized EntityManagerFactory getEmf() {
		if (emf == null) {
			String jdbcUrl = "jdbc:derby:" + System.getenv("DERBY_SPACE") + "/guhe;create=true";
			emf = Persistence.createEntityManagerFactory("GUHE",
					Collections.singletonMap("javax.persistence.jdbc.url", jdbcUrl));
			LOGGER.info("JPA DAO is initialized, url: " + jdbcUrl);
		}
		return emf;
	}
}
