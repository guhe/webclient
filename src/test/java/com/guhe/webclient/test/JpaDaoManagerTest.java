package com.guhe.webclient.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.guhe.dao.JpaDao;
import com.guhe.dao.JpaDaoManager;
import com.guhe.dao.Portfolio;
import com.guhe.util.DerbyUtil;

public class JpaDaoManagerTest {

	private static EntityManagerFactory testEmf;
	private static JpaDaoManager daoManager;
	private static JpaDao dao;

	@BeforeClass
	public static void beforeAll() throws IOException {
		FileUtils.deleteDirectory(new File("test_temp"));
		System.setProperty("derby.stream.error.file","test_temp/derby.log");

		String jdbcUrl = "jdbc:derby:test_temp/guhe;create=true";
		testEmf = Persistence.createEntityManagerFactory("GUHE",
				Collections.singletonMap("javax.persistence.jdbc.url", jdbcUrl));

		daoManager = new JpaDaoManager();
	}

	@AfterClass
	public static void afterAll() throws IOException {
		testEmf.close();

		DerbyUtil.closeEmbeddedDatebase("test_temp/derby");

		FileUtils.deleteDirectory(new File("test_temp"));
	}
	
	@Before
	public void before(){
		ServletContext servletContext = mock(ServletContext.class);
		when(servletContext.getAttribute("jpa.emf")).thenReturn(testEmf);
		HttpServletRequest httpReq = mock(HttpServletRequest.class);
		when(httpReq.getServletContext()).thenReturn(servletContext);
		
		dao = (JpaDao) daoManager.getDao(httpReq);
	}
	
	@After
	public void after(){
		dao.getEm().close();
	}

	@Test
	public void get_nonexistent_portfolio_by_id() {
		assertNull(dao.getPortfolio("ID999"));
	}
	
	@Test
	public void save_get_remove_portfolio() {
		Portfolio portfolio = new Portfolio();
		portfolio.setId("ID001");
		portfolio.setName("Test Portfolio Name");
		portfolio.setCash(123.4);
		portfolio.setNumberOfShares(100.0);
		portfolio.setNetWorthPerUnitLastYear(1.1);
		
		dao.createPortfolio(portfolio);
		
		Portfolio saved = dao.getPortfolio("ID001");
		assertEquals("ID001", saved.getId());
		assertEquals("Test Portfolio Name", saved.getName());
		assertEquals(123.4, saved.getCash(), 0.000001);
		assertEquals(100.0, saved.getNumberOfShares(), 0.000001);
		assertEquals(1.1, saved.getNetWorthPerUnitLastYear(), 0.000001);
		
		dao.deletePortfolio("ID001");
		
		assertNull(dao.getPortfolio("ID001"));
	}
}
