package com.xafero.slr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.xafero.slr.util.RuntimeHelper;

public class RuntimeTests {
	private Runtime rt;

	@Before
	public void testSetup() {
		rt = Runtime.getInstance();
	}

	@Test
	public void testRequire() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		String className = "com.thoughtworks.xstream.XStream";
		assertNull(RuntimeHelper.getClassByName(className));
		assertTrue(rt.require("com.thoughtworks.xstream:xstream") >= 1);
		Class<?> cl;
		assertNotNull(cl = Class.forName(className));
		Object instance = cl.newInstance();
		assertNotNull(instance);
		assertEquals(className, instance.getClass().getName());
	}
}