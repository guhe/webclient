package com.guhe.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import com.guhe.dao.Dao;
import com.guhe.dao.Portfolio;

public class JpaDao implements Dao {

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