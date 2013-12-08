package com.xafero.slr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
		} catch (Exception e) {
			assertEquals("org.jruby.embed.EvalFailedException", e.getMessage()
					.split(":")[0]);
		}
	}

	@Test
	public void testLangPy() {
		try {
			testCmd("", "-language", "py", "-e", "oh my gosh");
		} catch (Exception e) {
			assertEquals(
					"SyntaxError: no viable alternative at input 'my' in ", e
							.getMessage().split("<s")[0]);
		}
	}
}