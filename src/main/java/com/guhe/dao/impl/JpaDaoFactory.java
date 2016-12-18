package com.guhe.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;

import com.guhe.dao.Dao;
import com.guhe.dao.DaoManager;
import com.guhe.dao.Portfolio;

public class JpaDaoFactory implements DaoManager {

	@Override
	public synchronized Dao getDao(HttpServletRequest req) {
		Dao dao = (Dao) req.getAttribute("guhe.dao");
		if (dao == null) {
			EntityManagerFactory emf = (EntityManagerFactory) req.getServletContext().getAttribute("jpa.emf");
			dao = new JpaDao(emf.createEntityManager());
			req.setAttribute("guhe.dao", dao);
		}
		return dao;
	}

}

class JpaDao implements Dao {

	private EntityManager em;

	public JpaDao(EntityManager em) {
		this.em = em;
	}

	public EntityManager getEm() {
		return em;
	}

	@Override
	public List<Portfolio> getPortfolios() {
		return em.createQuery("from Portfolio", Portfolio.class).getResultList();
	}

	@Override
	public Portfolio getPortfolio(String id) {
		return em.find(Portfolio.class, id);
	}

}
