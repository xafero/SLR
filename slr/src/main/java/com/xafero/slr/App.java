package com.xafero.slr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

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
		System.out.println("To be done...");

		Properties config = new Properties();
		try {
			System.out.println("Looking for a user config...");
			config.loadFromXML(new FileInputStream(DEFAULT_CFG_FILE));
		} catch (FileNotFoundException fnfe) {
			System.out.println("None found, defaulting to built-in config...");
			config.loadFromXML(App.class.getClassLoader()
					.getResourceAsStream(DEFAULT_CFG_FILE));
		}
		System.out.printf("%s file endings bound. %n", config.size());
		
		Option help = new Option( "help", "print this message" );
		Option version = new Option( "version", "print the version information and exit" );
		
		Options options = new Options();
		options.addOption("l", false, "list all available engines");
		options.addOption(version);
		options.addOption(help);
		
		@SuppressWarnings("static-access")
		Option scriptfile = OptionBuilder.withArgName( "file" )
                .hasArg()
                .withDescription(  "use given script file" )
                .create( "script");
		options.addOption(scriptfile);

		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);

		if(cmd.hasOption("help")){
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "slr", options );
			return;
		}
		
		if (cmd.hasOption("l")) {
			ScriptEngineManager mgr = new ScriptEngineManager();
			for (ScriptEngineFactory factory : mgr.getEngineFactories()) {
				System.out.println("* " + factory);
			}
		}		
	}
}