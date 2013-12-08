package com.xafero.slr.util.log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DummyHandler implements InvocationHandler {

	public Object invoke(Object obj, Method method, Object[] args)
			throws Throwable {
		return null;
	}

}