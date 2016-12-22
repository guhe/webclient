package com.guhe.dao;

import java.util.logging.Logger;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

@WebListener
public class EmCloser implements ServletRequestListener {

	private static final Logger LOGGER = Logger.getLogger(EmCloser.class.getName());

	@Override
	public void requestDestroyed(ServletRequestEvent sre) {
		HttpServletRequest httpReq = (HttpServletRequest) sre.getServletRequest();
		JpaDao dao = (JpaDao) httpReq.getAttribute("guhe.dao");
		if (dao != null) {
			dao.getEm().close();
			String uri = httpReq.getServletPath();
			if (httpReq.getQueryString() != null) {
				uri += "?" + httpReq.getQueryString();
			}
			LOGGER.info("EM is closed for request " + uri);
		}
	}

}
