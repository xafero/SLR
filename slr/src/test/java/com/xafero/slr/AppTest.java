package com.xafero.slr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
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

	@Test(expected = UnsupportedOperationException.class)
	public void testMain() throws Exception {
		App.main(null);
	}

	@Test
	public void testHelp() throws Exception {
		testCmd("usage: slr", "-help");
	}

	@Test
	public void testVersion() throws Exception {
		testCmd(String.format("%n SLR v"), "-version");
	}

	@Test(expected = FileNotFoundException.class)
	public void testConfig() throws Exception {
		testCmd("?", "-config", "devNull");
	}

	@Test
	public void testLangJs() {
		try {
			testCmd("", "-language", "js", "-e", "#");
			fail("Should not happen!");
		} catch (Exception e) {
			assertEquals(
					"sun.org.mozilla.javascript.internal.EvaluatorException", e
							.getMessage().split(":")[0]);
		}
	}

	@Test
	public void testLangGroovy() {
		try {
			testCmd("", "-language", "groovy", "-e", "#");
			fail("Should not happen!");
		} catch (Exception e) {
			assertEquals(
					"org.codehaus.groovy.control.MultipleCompilationErrorsException",
					e.getMessage().split(":")[0]);
		}
	}

	@Test
	public void testLangClj() {
		try {
			testCmd("", "-language", "clj", "-e", "#");
			fail("Should not happen!");
		} catch (Exception e) {
			assertEquals(
					"java.lang.RuntimeException: EOF while reading character",
					e.getMessage().split(",")[0]);
		}
	}

	@Test
	public void testLangRb() {
		try {
			testCmd("", "-language", "rb", "-e", "oh my gosh");
			fail("Should not happen!");
		} catch (Exception e) {
			assertEquals("org.jruby.embed.EvalFailedException", e.getMessage()
					.split(":")[0]);
		}
	}

	@Test
	public void testLangPy() {
		try {
			testCmd("", "-language", "py", "-e", "oh my gosh");
			fail("Should not happen!");
		} catch (Exception e) {
			assertEquals(
					"SyntaxError: no viable alternative at input 'my' in ", e
							.getMessage().split("<s")[0]);
		}
	}

	@Test
	public void testLangLua() {
		try {
			testCmd("", "-language", "lua", "-e", "oh my gosh");
			fail("Should not happen!");
		} catch (Exception e) {
			assertEquals(" [string \"script\"]:1: syntax error", e.getMessage()
					.split(":", 2)[1]);
		}
	}

	@Test
	public void testLangPhp() {
		try {
			testCmd("", "-language", "php", "-e", "<?php test>");
			fail("Should not happen!");
		} catch (Exception e) {
			assertEquals("eval::1: end of file is an unexpected token", e
					.getMessage().split(",")[0]);
		}
	}

	@Test
	public void testLangBasic() {
		try {
			testCmd("", "-language", "bas", "-e", "oh my gosh");
			fail("Should not happen!");
		} catch (Exception e) {
			assertEquals("The line could not be analyzed null(0):2",
					e.getMessage());
		}
	}
}