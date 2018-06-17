package com.guhe.market;

import java.util.Arrays;
import java.util.List;

public enum Exchange {

	ShangHai(MoneyName.RMB, '6'), ShenZheng(MoneyName.RMB, '0', '3'), ShangHai_B(MoneyName.USD,
			'9'), ShenZheng_B(MoneyName.HKD, '2'), HuShen_CB(MoneyName.RMB, '1');

	private static final double RATE_BROKERAGE_A = 0.00025;
	private static final double RATE_BROKERAGE_B = 0.0005;
	private static final double RATE_BROKERAGE_CB = 0.00005;
	private static final double RATE_SETTLEMENT_B = 0.0005;
	private static final double RATE_TRANSFER = 0.00002;
	private static final double RATE_TAX = 0.001;

	private MoneyName moneyName;
	private List<Character> prefixChars;

	private Exchange(MoneyName mn, Character... firsts) {
		moneyName = mn;
		prefixChars = Arrays.asList(firsts);
	}

	public MoneyName getMoneyName() {
		return moneyName;
	}

	public double getBrokerage(double amount) {
		if (this == Exchange.ShangHai || this == Exchange.ShenZheng) {
			return Math.max(5, amount * RATE_BROKERAGE_A);
		} else if (this == Exchange.ShangHai_B) {
			return Math.max(1, amount * RATE_BROKERAGE_B);
		} else if (this == Exchange.ShenZheng_B) {
			return Math.max(5, amount * RATE_BROKERAGE_B);
		}  else if (this == Exchange.HuShen_CB) {
			return RATE_BROKERAGE_CB * amount;
		} else {
			return 0;
		}
	}

	public double getSettlementFee(double amount) {
		if (this == Exchange.ShangHai_B || this == Exchange.ShenZheng_B) {
			return RATE_SETTLEMENT_B * amount;
		}
		return 0;
	}
	
	public double getTransferFee(double amount) {
		if (this == Exchange.ShangHai || this == Exchange.ShangHai_B) {
			return RATE_TRANSFER * amount;
		}
		return 0;
	}
	
	public double getTax(double amount) {
		if (this == Exchange.HuShen_CB) {
			return 0;
		}
		return amount * RATE_TAX;
	}

	public static Exchange instance(String stockCode) {
		if (stockCode == null || stockCode.length() == 0) {
			throw new RuntimeException();
		}
		for (Exchange ex : values()) {
			if (ex.prefixChars.contains(stockCode.charAt(0))) {
				return ex;
			}
		}
		throw new RuntimeException();
	}
}