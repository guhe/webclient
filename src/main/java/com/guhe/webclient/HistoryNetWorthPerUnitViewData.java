package com.guhe.webclient;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "historyNetWorthPerUnit")
public class HistoryNetWorthPerUnitViewData {

	private String date;
	private double netWorthPerUnit;
}
