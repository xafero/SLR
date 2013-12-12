package com.xafero.slr.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

	public static String last(String[] parts) {
		return parts[parts.length - 1];
	}

	public static void copy(InputStream input, FileOutputStream output)
			throws IOException {
		byte[] buffer = new byte[16 * 1024];
		int readBytes;
		while ((readBytes = input.read(buffer)) > 0)
			output.write(buffer, 0, readBytes);
		output.flush();
		output.close();
		input.close();
	}
}