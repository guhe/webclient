package com.guhe.webclient.test;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mock;

public class MockitoFactory<T> implements Factory<T> {

	@Mock
	private T t;

	public MockitoFactory() {
		doMock(t);
	}

	@Override
	public void dispose(T arg0) {

	}

	protected void doMock(T t) {

	}

	@Override
	public T provide() {
		return t;
	}

}
