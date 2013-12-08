package com.xafero.slr.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class DummyStream extends ByteArrayOutputStream {

	@Override
	public synchronized String toString(String charsetName)
			throws UnsupportedEncodingException {
		throw new UnsupportedEncodingException(charsetName);
	}
}