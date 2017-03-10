package com.guhe.market;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.ClientBuilder;

public class ZsMoneyExchanger implements MoneyExchanger {
	private Map<MoneyName, MoneyPrice> moneyPrices = new HashMap<>();

	@Override
	public synchronized MoneyPrice getMoneyPrice(MoneyName moneyName) {
		if (moneyPrices.isEmpty()) {
			loadExchangeRate();
		}
		return moneyPrices.get(moneyName);
	}

	private void loadExchangeRate() {
		String url = "http://fx.cmbchina.com/Hq/";
		String data = ClientBuilder.newClient().target(url).request().get(String.class);
		Pattern p = Pattern.compile("\\<td class=\"numberright\"\\>\\s*([\\d\\.]+)\\s*\\</td\\>");
		Matcher m = p.matcher(data);

		int priceIndex = 0;
		MoneyPrice mp = null;
		while (m.find() && priceIndex < 4 * 5) {
			int moneyIndex = priceIndex / 5;
			int priceType = priceIndex % 5; // 中间价 现汇卖出价 现钞卖出价 现汇买入价 现钞买入价
			double price = Double.parseDouble(m.group(1));
			if (priceType == 2) {
				mp = new MoneyPrice();
				mp.setSell(price * 0.01);
			} else if (priceType == 4) {
				mp.setBuy(price * 0.01);
				System.out.println("index: " + moneyIndex + ", MoneyPrice: " + mp);
				if (moneyIndex == 0) { // 港币
					moneyPrices.put(MoneyName.HKD, mp);
				} else if (moneyIndex == 3) {
					moneyPrices.put(MoneyName.USD, mp);
				}
			}

			priceIndex++;
		}
	}

	public static void main(String[] args) {
		ZsMoneyExchanger calculator = new ZsMoneyExchanger();
		calculator.loadExchangeRate();
		System.out.println(calculator.moneyPrices);
	}
}
