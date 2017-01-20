package com.guhe.webclient;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "portfolioResult")
class PortfolioResultViewData {
	private int rltCode;
	private String message;

	public PortfolioResultViewData(int rltCode, String message) {
		this.rltCode = rltCode;
		this.message = message;
	}

	public int getRltCode() {
		return rltCode;
	}

	public void setRltCode(int rltCode) {
		this.rltCode = rltCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}