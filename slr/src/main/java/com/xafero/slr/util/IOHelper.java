package com.xafero.slr.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class IOHelper {

	public static ByteArrayOutputStream newSystemOut() {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bo);
		System.setOut(ps);
		return bo;
	}

	public static String getUTF8Str(ByteArrayOutputStream bo) {
		try {
			return bo.toString("UTF8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}