package com.guhe.market;

import com.guhe.util.Reflector;

public interface ExchangeRateCalculator {
	
	MoneyPrice getMoneyPrice(MoneyName moneyName);
	
	public class MoneyPrice {
		private double buy; // HKD(100) --> RMB
		private double sell; // RMB --> HKD(100)

		public double getBuy() {
			return buy;
		}

		public void setBuy(double buy) {
			this.buy = buy;
		}

		public double getSell() {
			return sell;
		}

		public void setSell(double sell) {
			this.sell = sell;
		}

		@Override
		public String toString() {
			return Reflector.toStringByAllFields(this);
		}
	}
}
