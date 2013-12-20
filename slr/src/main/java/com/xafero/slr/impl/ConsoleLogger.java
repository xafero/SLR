package com.xafero.slr.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.xafero.slr.api.ILogger;

public class ConsoleLogger implements ILogger {

	public static final SimpleDateFormat FullDateFmt = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	@Override
	public void info(String txt, Object... args) {
		String date = FullDateFmt.format(new Date());
		txt = String.format(" [%s] %s", date, String.format(txt, args));
		System.out.println(txt);
	}
}