package com.xafero.slr.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;

public class HelperTest {

	@Test
	public void testConstructors() {
		new RuntimeHelper();
		new IOHelper();
	}

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