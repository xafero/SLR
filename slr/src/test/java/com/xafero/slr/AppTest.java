package com.xafero.slr;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;

import com.xafero.slr.util.IOHelper;

public class AppTest {
	private static final File tmpRoot = new File("target/test-tmp");
	private App app;

	@Before
	public void testSetup() {
		tmpRoot.mkdirs();
		app = new App();
	}

	private synchronized void testCmd(String expected, String... args)
			throws Exception {
		PrintStream orig = System.out;
		ByteArrayOutputStream bo = IOHelper.newSystemOut();
		app.run(args);
		System.setOut(orig);
		String txt = IOHelper.getUTF8Str(bo);
		System.out.println(txt);
		assertTrue(getMessageOfEquals(expected, txt), txt.startsWith(expected));
	}

	private static String getMessageOfEquals(String expected, String actual) {
		expected = escape(expected);
		actual = escape(actual);
		ComparisonFailure cf = new ComparisonFailure(null, expected, actual);
		return cf.getMessage().replace("but was",
				String.format("%n%s", "but was"));
	}

	private static String escape(String text) {
		return text.replace('\n', 'N').replace('\r', 'R');
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

	@Test(expected = UnsupportedOperationException.class)
	public void testLanguageFail() throws Exception {
		testCmd("?", "-language", "iDontKnow");
	}

	@Test
	public void testExecuteLine() throws Exception {
		// Set the text which is tested
		String txt = (new Date()) + "";
		String line = "print('" + txt + "')";
		// Execute and check result
		testCmd(txt, "-language", "js", "-e", line);
	}

	@Test
	public void testExecuteFile() throws Exception {
		// Create a new temporary file
		File sf = new File(tmpRoot, "t1");
		sf.mkdirs();
		File tf = new File(sf, "tmpTest.js");
		tf.deleteOnExit();
		// Set the text which is tested
		String txt = (new Date()) + "";
		IOHelper.writeAllText(tf, "print('" + txt + "')");
		// Execute and check result
		testCmd(txt, "-run", tf.getAbsolutePath());
		// Delete manually
		tf.delete();
		sf.delete();
	}

	@Test
	public void testWatchFile() throws Exception {
		// Create a new temporary file
		File sf = new File(tmpRoot, "t2");
		sf.mkdirs();
		File tf = new File(sf, "tmpTest.js");
		tf.deleteOnExit();
		// Set the text which is tested
		String txt = (new Date()) + "";
		IOHelper.writeAllText(tf, "print('" + txt + "')");
		// Test it
		testWatch(txt, tf, "-run", 200);
		// Delete manually
		tf.delete();
		sf.delete();
	}

	private void testWatch(String txt, File file, String cmd, int ms)
			throws Exception {
		// Setup input
		final PipedOutputStream po = new PipedOutputStream();
		InputStream oldIn = System.in;
		System.setIn(new PipedInputStream(po));
		// Execute and check result
		Timer t = new Timer(true);
		t.schedule(new TimerTask() {
			public void run() {
				try {
					po.write(13);
					po.flush();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}, ms);
		testCmd(txt, cmd, file.getAbsolutePath(), "-watchInterval", 50 + "");
		// Set back input
		System.setIn(oldIn);
	}

	@Test
	public void testWatchDir() throws Exception {
		File sf = new File(tmpRoot, "t3");
		sf.mkdirs();
		// Create a new temporary file
		File tfA = new File(sf, "tmpTestA-dir.js");
		tfA.deleteOnExit();
		// Set the first text which is tested
		String txtA = (new Date()) + " | A ";
		IOHelper.writeAllText(tfA, "print('" + txtA + "')");
		// Create a new temporary file
		File tfB = new File(sf, "tmpTestB-dir.js");
		tfB.deleteOnExit();
		// Set the second text which is tested
		String txtB = (new Date()) + " | B ";
		IOHelper.writeAllText(tfB, "print('" + txtB + "')");
		// Test it
		String txt = String.format("%s%s", txtA, txtB);
		File dir = tfA.getAbsoluteFile().getParentFile();
		testWatch(txt, dir, "-runAll", 300);
		// Delete manually
		tfA.delete();
		tfB.delete();
		sf.delete();
	}

	@Test
	public void testExecuteDir() throws Exception {
		File sf = new File(tmpRoot, "t4");
		sf.mkdirs();
		// Create first temporary file
		File tfA = new File(sf, "tmpTestA-dir.js");
		tfA.deleteOnExit();
		// Set the first text which is tested
		String txtA = (new Date()) + " | A";
		IOHelper.writeAllText(tfA, "print('" + txtA + " \\n')");
		// Create second temporary file
		File tfB = new File(sf, "tmpTestB-dir.js");
		tfB.deleteOnExit();
		// Set the second text which is tested
		String txtB = (new Date()) + " | B";
		IOHelper.writeAllText(tfB, "print('" + txtB + " \\n')");
		// Get current directory
		File root = tfA.getAbsoluteFile().getParentFile();
		// Check if files are there
		FilenameFilter filter = IOHelper.filterBySuffix("-dir.js");
		assertArrayEquals(
				new File[] { tfA.getAbsoluteFile(), tfB.getAbsoluteFile() },
				root.listFiles(filter));
		// Execute and check result
		String txt = String.format("%s \n%s \n", txtA, txtB);
		testCmd(txt, "-runAll", root.getAbsolutePath());
		// Delete manually
		tfA.delete();
		tfB.delete();
		sf.delete();
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