package com.xafero.slr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Properties;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import com.xafero.slr.api.IRuntime;
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
		// Check if file's execution is wanted
		if (cmd.hasOption("f")) {
			String path = cmd.getOptionValue("f");
			File file = new File(path);
			if (engine == null) {
				String lang = IOHelper.last(file.getName().split("\\."));
				engine = getEngine(lang, defCfg, usrCfg);
			}
			engine.eval(new FileReader(file));
			return;
		}
		// Just panic here
		throw new UnsupportedOperationException("Can't handle '"
				+ Arrays.toString(args) + "'!");
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
}