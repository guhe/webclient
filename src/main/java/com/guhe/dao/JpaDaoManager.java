package com.guhe.dao;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;

public class JpaDaoManager implements DaoManager {

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
