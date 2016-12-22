package com.guhe.dao;

import javax.persistence.EntityManager;

public class JpaDao implements Dao {

	private EntityManager em;

	public JpaDao(EntityManager em) {
		this.em = em;
	}

	public EntityManager getEm() {
		return em;
	}

	@Override
	public Portfolio getPortfolio(String id) {
		return em.find(Portfolio.class, id);
	}
	
	public void createPortfolio(Portfolio portfolio){
		em.getTransaction().begin();
		em.persist(portfolio);
		em.getTransaction().commit();
	}
	
	public void deletePortfolio(String id){
		em.getTransaction().begin();
		em.remove(getPortfolio(id));
		em.getTransaction().commit();
	}

}