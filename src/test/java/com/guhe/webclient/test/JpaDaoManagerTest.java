package com.guhe.webclient.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.guhe.dao.impl.JpaDao;
import com.guhe.dao.impl.JpaDaoManager;

public class JpaDaoManagerTest {

	private static EntityManagerFactory testEmf;
	private static JpaDao dao;

	@BeforeClass
	public static void beforeAll() throws IOException {
		FileUtils.deleteDirectory(new File("test_temp"));
		System.setProperty("derby.stream.error.file","test_temp/derby.log");

		String jdbcUrl = "jdbc:derby:test_temp/guhe;create=true";
		testEmf = Persistence.createEntityManagerFactory("GUHE",
				Collections.singletonMap("javax.persistence.jdbc.url", jdbcUrl));

		ServletContext servletContext = mock(ServletContext.class);
		when(servletContext.getAttribute("jpa.emf")).thenReturn(testEmf);
		HttpServletRequest httpReq = mock(HttpServletRequest.class);
		when(httpReq.getServletContext()).thenReturn(servletContext);

		JpaDaoManager daoManager = new JpaDaoManager();
		dao = (JpaDao) daoManager.getDao(httpReq);
	}

	@AfterClass
	public static void afterAll() throws IOException {
		dao.getEm().close();
		testEmf.close();

		try {
			DriverManager.getConnection("jdbc:derby:test_temp/guhe;shutdown=true");
		} catch (SQLException e) {
			assertEquals(45000, e.getErrorCode());
			assertEquals("08006", e.getSQLState());
		}

		FileUtils.deleteDirectory(new File("test_temp"));
	}

	@Test
	public void test_get_portfolio_by_id() {
		dao.getPortfolio("P123456");
	}
}
