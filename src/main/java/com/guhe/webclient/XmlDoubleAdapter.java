package com.guhe.webclient;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XmlDoubleAdapter extends XmlAdapter<Double, Double> {

	@Override
	public Double unmarshal(Double v) throws Exception {
		return v == null ? null : Math.round(v * 1000000) / 1000000.0;
	}

	@Override
	public Double marshal(Double v) throws Exception {
		return v == null ? null : Math.round(v * 1000000) / 1000000.0;
	}
}