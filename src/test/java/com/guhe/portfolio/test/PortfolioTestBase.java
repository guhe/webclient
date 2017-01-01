package com.guhe.portfolio.test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.guhe.portfolio.JpaPortfolioManager;
import com.guhe.util.DerbyUtil;

public class PortfolioTestBase {

	protected static EntityManagerFactory testEmf;
	
	protected JpaPortfolioManager pm;

	@BeforeClass
	public static void beforeAll() throws IOException {
		FileUtils.deleteDirectory(new File("test_temp"));
		System.setProperty("derby.stream.error.file", "test_temp/derby.log");

		String jdbcUrl = "jdbc:derby:test_temp/guhe;create=true";
		testEmf = Persistence.createEntityManagerFactory("GUHE",
				Collections.singletonMap("javax.persistence.jdbc.url", jdbcUrl));
	}

	@AfterClass
	public static void afterAll() throws IOException {
		testEmf.close();

		DerbyUtil.closeEmbeddedDatebase("test_temp/guhe");

		FileUtils.deleteDirectory(new File("test_temp"));
	}

	@Before
	public void before() {
		pm = new JpaPortfolioManager(testEmf.createEntityManager());
	}

	@After
	public void after() {
		pm.getEm().close();
	}
}
