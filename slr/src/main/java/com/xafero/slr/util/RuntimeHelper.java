package com.xafero.slr.util;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

public class RuntimeHelper {

	public static int extendClassPath(URL... urls) {
		URLClassLoader loader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		Method addMethod = getMethod(URLClassLoader.class, "addURL", URL.class);
		addMethod.setAccessible(true);
		List<URL> existing = Arrays.asList(loader.getURLs());
		int counter = 0;
		for (URL url : urls) {
			if (existing.contains(url))
				continue;
			invoke(loader, addMethod, url);
			counter++;
		}
		return counter;
	}

	public static Object invoke(Object instance, Method method, Object... args) {
		try {
			return method.invoke(instance, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Method getMethod(Class<?> owner, String name,
			Class<?>... args) {
		try {
			return owner.getDeclaredMethod(name, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Class<?> getClassByName(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
}