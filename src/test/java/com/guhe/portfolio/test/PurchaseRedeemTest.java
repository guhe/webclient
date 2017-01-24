package com.guhe.portfolio.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.guhe.portfolio.Holder;
import com.guhe.portfolio.Portfolio;
import com.guhe.portfolio.PortfolioHolder;
import com.guhe.portfolio.PurchaseRedeemRecord;
import com.guhe.portfolio.PurchaseRedeemRecord.PurchaseOrRedeem;
import com.guhe.util.CommonUtil;

public class PurchaseRedeemTest extends PortfolioTestBase {

	private Portfolio portfolio;

	@BeforeClass
	public static void beforeAll() throws IOException {
		PortfolioTestBase.beforeAll();

		EntityManager em = testEmf.createEntityManager();
		em.getTransaction().begin();
		em.persist(new Holder("Tiger"));
		em.getTransaction().commit();
		em.close();
	}

	@Before
	public void before() {
		super.before();

		portfolio = new Portfolio();
		portfolio.setId("ID001");
		portfolio.setName("Test Portfolio Name");
		portfolio.setCash(0);
		portfolio.setNetWorthPerUnitLastYear(1);

		pm.savePortfolio(portfolio);
	}

	@After
	public void after() {
		pm.deletePortfolio("ID001");

		super.after();
	}

	@Test
	public void test_purchase_redeem() {
		pm.purchase("ID001", "Tiger", 10000.0, 2.0, 5.0, CommonUtil.parseDate("yyyy-MM-dd", "2017-01-22"));
		portfolio = pm.getPortfolio("ID001");
		assertEquals(9995.0, portfolio.getCash(), 0.000001);
		List<PortfolioHolder> holders = portfolio.getHolders();
		assertEquals(1, holders.size());
		assertThat(holders.get(0), "Tiger", 4997.5, 10000);

		pm.redeem("ID001", "Tiger", 2997.5, 2.0, 5, CommonUtil.parseDate("yyyy-MM-dd", "2017-01-23"));
		portfolio = pm.getPortfolio("ID001");
		assertEquals(4000, portfolio.getCash(), 0.000001);
		holders = portfolio.getHolders();
		assertEquals(1, holders.size());
		assertThat(holders.get(0), "Tiger", 2000, 4010);

		pm.redeem("ID001", "Tiger", 2000.0, 2.0, 5, CommonUtil.parseDate("yyyy-MM-dd", "2017-01-24"));
		portfolio = pm.getPortfolio("ID001");
		assertEquals(0, portfolio.getCash(), 0.000001);
		holders = portfolio.getHolders();
		assertEquals(0, holders.size());

		portfolio = pm.getPortfolio("ID001");
		List<PurchaseRedeemRecord> records = portfolio.getPurchaseRedeemRecords();
		assertEquals(3, records.size());
		assertThat(records.get(0), PurchaseOrRedeem.PURCHASE, "Tiger", 4997.5, 2.0, 5.0, "2017-01-22");
		assertThat(records.get(1), PurchaseOrRedeem.REDEEM, "Tiger", 2997.5, 2.0, 5.0, "2017-01-23");
		assertThat(records.get(2), PurchaseOrRedeem.REDEEM, "Tiger", 2000, 2.0, 5.0, "2017-01-24");
	}

	private void assertThat(PortfolioHolder ph, String holderName, double share, double totalInvestment) {
		assertEquals(holderName, ph.getHolder().getName());
		assertEquals(share, ph.getShare(), 0.000001);
		assertEquals(totalInvestment, ph.getTotalInvestment(), 0.000001);
	}

	private void assertThat(PurchaseRedeemRecord record, PurchaseOrRedeem purchaseOrRedeem, String holderName,
			double share, double netWorthPerUnit, double fee, String date) {
		assertEquals(purchaseOrRedeem, record.getPurchaseOrRedeem());
		assertEquals(holderName, record.getHolder().getName());
		assertEquals(share, record.getShare(), 0.000001);
		assertEquals(netWorthPerUnit, record.getNetWorthPerUnit(), 0.000001);
		assertEquals(fee, record.getFee(), 0.000001);
		assertEquals(CommonUtil.parseDate("yyyy-MM-dd", date), record.getDate());
	}

}
