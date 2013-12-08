package com.xafero.slr.util;

// import static org.junit.Assert.*;

import org.junit.Test;

public class HelperTest {

	@Test
	public void testClassByName() {
		RuntimeHelper.getClassByName(String.class.getName());
	}

	@Test(expected = RuntimeException.class)
	public void testGetMethod() {
		RuntimeHelper.getMethod(null, null);
	}

	@Test(expected = RuntimeException.class)
	public void testInvoke() {
		RuntimeHelper.invoke(null, null);
	}
}