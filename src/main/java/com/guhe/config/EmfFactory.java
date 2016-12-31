package com.guhe.config;

import java.util.Collections;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.glassfish.hk2.api.Factory;

import com.guhe.util.DerbyUtil;

class EmfFactory implements Factory<EntityManagerFactory> {

	private static final Logger LOGGER = Logger.getLogger(PortfolioManagerFactory.class.getName());

	@Override
	public EntityManagerFactory provide() {
		String jdbcUrl = "jdbc:derby:" + System.getenv("DERBY_SPACE") + "/guhe;create=true";
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("GUHE",
				Collections.singletonMap("javax.persistence.jdbc.url", jdbcUrl));
		LOGGER.info("JPA EMF is created, url: " + jdbcUrl);
		return emf;
	}

	@Override
	public void dispose(EntityManagerFactory emf) {
		emf.close();
		LOGGER.info("JPA EMF is closed.");

		String dbPath = System.getenv("DERBY_SPACE");
		DerbyUtil.closeEmbeddedDatebase(dbPath + "/guhe");
		LOGGER.info("Derby is closed, path: " + dbPath);
	}

}
