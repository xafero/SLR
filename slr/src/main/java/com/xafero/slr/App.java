package com.xafero.slr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Properties;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import com.xafero.slr.api.IRuntime;
import com.xafero.slr.util.FolderWatcher;
import com.xafero.slr.util.FolderWatcher.FileChange;
import com.xafero.slr.util.FolderWatcher.FileListener;
import com.xafero.slr.util.IOHelper;

public class App {
	private static final String DEFAULT_CFG_FILE = "scriptEngines.cfg.xml";

	public static void main(String[] args) throws Exception {
		App app = new App();
		app.run(args);
	}

	public void run(String... args) throws Exception {
		// Create options
		Options options = new Options();
		options.addOption("h", "help", false, "print an overview");
		options.addOption("v", "version", false,
				"print the version information");
		options.addOption(OptionBuilder.withLongOpt("language")
				.withDescription("specify the engine to use").hasArg()
				.withArgName("ext").create("l"));
		options.addOption(OptionBuilder.withLongOpt("config")
				.withDescription("specify the configuration to use").hasArg()
				.withArgName("file").create("c"));
		options.addOption(OptionBuilder.withLongOpt("exec")
				.withDescription("execute one line of code").hasArg()
				.withArgName("line").create("e"));
		options.addOption(OptionBuilder.withLongOpt("run")
				.withDescription("run one script file").hasArg()
				.withArgName("file").create("f"));
		options.addOption(OptionBuilder.withLongOpt("runAll")
				.withDescription("run all scripts found").hasArg()
				.withArgName("dir").create("d"));
		options.addOption(OptionBuilder.withLongOpt("watchInterval")
				.withDescription("watch for file changes").hasArg()
				.withArgName("ms").create("w"));
		// Parse the textual input
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);
		// Check for help switch
		if (cmd.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("slr", options);
			return;
		}
		// Check for version switch
		if (cmd.hasOption("v")) {
			System.out.println();
			System.out.println(" SLR v.0.0.1 alpha");
			System.out.println(" Copyright (C) 2013 by Xafero Inc.");
			System.out.println();
			return;
		}
		// Check for user-provided configuration
		Properties usrCfg = new Properties();
		if (cmd.hasOption("c")) {
			String file = cmd.getOptionValue("c");
			usrCfg.loadFromXML(new FileInputStream(file));
		}
		// Read in built-in script engine configuration
		Properties defCfg = new Properties();
		defCfg.loadFromXML(App.class.getClassLoader().getResourceAsStream(
				DEFAULT_CFG_FILE));
		// Check the language
		ScriptEngine engine = null;
		if (cmd.hasOption("l")) {
			String lang = cmd.getOptionValue("l");
			engine = getEngine(lang, defCfg, usrCfg);
		}
		// Check if line's execution is wanted
		if (cmd.hasOption("e")) {
			String line = cmd.getOptionValue("e");
			line = line.trim();
			engine.eval(line);
			return;
		}
		// Common stuff for files and folders
		File[] files = null;
		File file = null;
		File dir = null;
		// Check if directory's execution is wanted
		if (cmd.hasOption("d")) {
			String path = cmd.getOptionValue("d");
			dir = new File(path);
			files = dir.listFiles();
		}
		// Check if file's execution is wanted
		if (cmd.hasOption("f")) {
			String path = cmd.getOptionValue("f");
			file = new File(path);
			files = new File[] { file };
			dir = file.getParentFile();
		}
		// Check if file changes should be observed
		if (cmd.hasOption("w")) {
			int ms = Integer.parseInt(cmd.getOptionValue("w"));
			boolean throwError = file != null;
			String singleFile = file == null ? null : file.getAbsolutePath();
			FileListener listener = createListener(throwError, defCfg, usrCfg,
					singleFile, engine);
			FolderWatcher watcher = new FolderWatcher(dir, ms, listener);
			System.in.read();
			watcher.close();
			return;
		}
		// Execute scripts
		if (files != null) {
			for (File oneFile : files)
				try {
					executeFile(engine, oneFile, defCfg, usrCfg);
				} catch (UnsupportedOperationException uoe) {
					// Ignore more than one error, 'cause it'll be a directory
					if (files.length == 1)
						throw uoe;
				}
			return;
		}
		// Just panic here
		throw new UnsupportedOperationException("Can't handle '"
				+ Arrays.toString(args) + "'!");
	}

	private void executeFile(ScriptEngine engine, File file, Properties defCfg,
			Properties usrCfg) throws FileNotFoundException, ScriptException {
		if (engine == null) {
			String lang = IOHelper.last(file.getName().split("\\."));
			engine = getEngine(lang, defCfg, usrCfg);
		}
		engine.eval(new FileReader(file));
	}

	private ScriptEngine getEngine(String lang, Properties defCfg,
			Properties usrCfg) {
		String artf = usrCfg.getProperty(lang);
		if (artf == null)
			artf = defCfg.getProperty(lang);
		if (artf != null) {
			IRuntime rt = Runtime.getInstance();
			rt.require(artf);
		}
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByExtension(lang);
		if (engine == null)
			throw new UnsupportedOperationException(
					"Couldn't find an engine for '" + lang + "'!");
		return engine;
	}

	private FileListener createListener(final boolean throwError,
			final Properties defCfg, final Properties usrCfg,
			final String singleFile, final ScriptEngine engine) {
		return new FileListener() {
			public void fileChanged(FolderWatcher watcher, FileChange event) {
				try {
					File file = new File(event.Key);
					if (singleFile != null
							&& !singleFile.equalsIgnoreCase(file
									.getAbsolutePath()))
						return;
					executeFile(engine, file, defCfg, usrCfg);
				} catch (RuntimeException re) {
					if (!throwError)
						return;
					throw re;
				} catch (Exception e) {
					if (!throwError)
						return;
					throw new RuntimeException(e);
				}
			}
		};
	}
}