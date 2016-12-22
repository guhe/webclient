package com.guhe.dao;

import java.util.Collections;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class EmfProvider implements ServletContextListener {

	private static final Logger LOGGER = Logger.getLogger(EmfProvider.class.getName());

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String jdbcUrl = "jdbc:derby:" + System.getenv("DERBY_SPACE") + "/guhe;create=true";
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("GUHE",
				Collections.singletonMap("javax.persistence.jdbc.url", jdbcUrl));
		LOGGER.info("JPA EMF is initialized, url: " + jdbcUrl);
		sce.getServletContext().setAttribute("jpa.emf", emf);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		EntityManagerFactory emf = (EntityManagerFactory) sce.getServletContext().getAttribute("jpa.emf");
		if (emf != null) {
			emf.close();
			LOGGER.info("EMF is closed.");
		}
	}

}
