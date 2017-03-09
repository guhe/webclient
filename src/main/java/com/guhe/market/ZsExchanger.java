package com.guhe.market;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.ClientBuilder;

public class ZsExchanger implements Exchanger {
	private Map<MoneyName, MoneyPrice> moneyPrices = new HashMap<>();

	@Override
	public MoneyPrice getMoneyPrice(MoneyName moneyName) {
		return null;
	}

	private void loadExchangeRate() {
		String url = "http://fx.cmbchina.com/Hq/";
		String data = ClientBuilder.newClient().target(url).request().get(String.class);
		Pattern p = Pattern.compile("\\<td class=\"numberright\"\\>\\s*([\\d\\.]+)\\s*\\</td\\>");
		Matcher m = p.matcher(data);

		int priceIndex = 0;
		MoneyPrice mp = new MoneyPrice();
		while (m.find() && priceIndex < 5) {
			int moneyIndex = priceIndex / 5;
			int priceType = priceIndex % 5; // 中间价 现汇卖出价 现钞卖出价 现汇买入价 现钞买入价
			double price = Double.parseDouble(m.group(1));
			if (priceType == 1) {
				mp.setSell(price);
			} else if (priceType == 4) {
				mp.setBuy(price);
				moneyPrices.put(MoneyName.values()[moneyIndex], mp);
			}

			priceIndex++;
		}
	}

	public static void main(String[] args) {
		ZsExchanger calculator = new ZsExchanger();
		calculator.loadExchangeRate();
		System.out.println(calculator.moneyPrices);
	}
}
