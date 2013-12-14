package com.xafero.slr.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;

public class HelperTest {

	@Test
	public void testConstructors() {
		new RuntimeHelper();
		new IOHelper();
		new MavenHelper();
	}

	@Test
	public void testClassByName() {
		RuntimeHelper.getClassByName(String.class.getName());
	}

	@Test(expected = RuntimeException.class)
	public void testGetMethodFail() {
		RuntimeHelper.getMethod(null, null);
	}

	@Test
	public void testGetMethodOk() {
		Method tm = RuntimeHelper.getMethod(String.class, "trim");
		assertNotNull(tm);
		assertEquals("java.lang.String.trim()",
				IOHelper.last(tm.toString().split(" ")));
	}

	@Test(expected = RuntimeException.class)
	public void testGetMethodNonPublicFail() {
		RuntimeHelper.getMethod(String.class, "iDontKnowYou");
	}

	@Test
	public void testGetMethodNonPublicOk() {
		Method tm = RuntimeHelper.getMethod(String.class,
				"indexOfSupplementary", int.class, int.class);
		assertNotNull(tm);
		assertEquals("indexOfSupplementary(int,int)",
				IOHelper.last(tm.toString().split("\\.")));
	}

	@Test(expected = RuntimeException.class)
	public void testInvoke() {
		RuntimeHelper.invoke(null, null);
	}

	@Test
	public void testClasspath() {
		URLClassLoader loader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		URL url = loader.getURLs()[0];
		int result = RuntimeHelper.extendClassPath(url);
		assertEquals(0, result);
	}

	@Test(expected = RuntimeException.class)
	public void testGetUTF8Str() {
		ByteArrayOutputStream fake = new DummyStream();
		IOHelper.getUTF8Str(fake);
	}
}