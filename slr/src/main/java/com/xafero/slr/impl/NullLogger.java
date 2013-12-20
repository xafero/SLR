package com.xafero.slr.impl;

import com.xafero.slr.api.ILogger;

public class NullLogger implements ILogger {

	@Override
	public void info(String txt, Object... args) {
		/* NO-OP */
	}
}