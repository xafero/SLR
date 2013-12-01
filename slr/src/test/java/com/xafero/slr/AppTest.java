package com.xafero.slr;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import com.xafero.slr.util.IOHelper;

public class AppTest {
	private App app;

	@Before
	public void testSetup() {
		app = new App();
	}

	private void testCmd(String expected, String... args) throws Exception {
		PrintStream orig = System.out;
		ByteArrayOutputStream bo = IOHelper.newSystemOut();
		app.run(args);
		System.setOut(orig);
		String txt = IOHelper.getUTF8Str(bo);
		System.out.println(txt);
		assertTrue(txt, txt.startsWith(expected));
	}

	@Test
	public void testHelp() throws Exception {
		testCmd("usage: slr", "-help");
	}

	@Test
	public void testVersion() throws Exception {
		testCmd(String.format("%n SLR v"), "-version");
	}
}