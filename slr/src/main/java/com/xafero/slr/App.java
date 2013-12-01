package com.xafero.slr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.jar.Manifest;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

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
		options.addOption("v", "version", false, "print the version information");
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
		if(cmd.hasOption("v")) {
			System.out.println();
			System.out.println(" SLR v.0.0.1 alpha");
			System.out.println(" Copyright (C) 2013 by Xafero Inc.");
			System.out.println();
			return;
		}
		Manifest manifest = new Manifest(App.class.getResourceAsStream("META-INF/MANIFEST.MF"));

		if(true)
		throw new UnsupportedOperationException(manifest+"");
		
		
		System.out.println("To be done...");

		Properties config = new Properties();
		try {
			System.out.println("Looking for a user config...");
			config.loadFromXML(new FileInputStream(DEFAULT_CFG_FILE));
		} catch (FileNotFoundException fnfe) {
			System.out.println("None found, defaulting to built-in config...");
			config.loadFromXML(App.class.getClassLoader().getResourceAsStream(
					DEFAULT_CFG_FILE));
		}
		System.out.printf("%s file endings bound. %n", config.size());

		Option help = new Option("help", "print this message");
		

		
		options.addOption("l", false, "list all available engines");
		options.addOption(help);

		@SuppressWarnings("static-access")
		Option scriptfile = OptionBuilder.withArgName("file").hasArg()
				.withDescription("use given script file").create("script");
		options.addOption(scriptfile);


		

		if (cmd.hasOption("l")) {
			ScriptEngineManager mgr = new ScriptEngineManager();
			for (ScriptEngineFactory factory : mgr.getEngineFactories()) {
				System.out.println("* " + factory);
			}
		}
	}
}