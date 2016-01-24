package de.einwesen.porttunnel;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * 
 * @author EinWesen
 *
 * Helper class to parse command line options
 */
public class CmdLineHandler {
	
	public static final String CMD_OPT_BUFFERSIZE = "bs";
	public static final String CMD_OPT_PROXYPASSWORD = "pxpass";
	public static final String CMD_OPT_PROXYUSER = "pxusr";
	public static final String CMD_OPT_PROXYPORT = "pxp";
	public static final String CMD_OPT_PROXYHOST = "pxh";
	public static final String CMD_OPT_REMOTEPORT = "rp";
	public static final String CMD_OPT_REMOTEHOST = "rh";
	public static final String CMD_OPT_HELP = "h";
	public static final String CMD_OPT_LOCALPORT = "lp";
		
	private final CommandLine cmdLine;
	
	private CmdLineHandler(CommandLine cl) {
		this.cmdLine = cl;
	}
	
	public boolean hasOptions() {
		return this.cmdLine.getOptions().length > 0;
	}
	
	public boolean hasOption(String option) {
		return this.cmdLine.hasOption(option);
	}
	
	/**
	 * Return parsed option, or null if undefined
	 * 
	 * @param cmd
	 * @return
	 * @throws ParseException
	 */
	private Object getParsedOptionValue(String cmd) throws ParseException {
		Object o = null;
		try {
			o = cmdLine.getParsedOptionValue(cmd);		
		} catch (ParseException p) {
			o = cmdLine.getOptionValue(cmd);			
			if (o != null && "".equals(o.toString())) {
				o = null;
			} else {
				throw new ParseException("Could not parse option " + cmd + "[" + p.getMessage() + "]");
			}
		}
		return o; 
	}
		
	/**
	 * 
	 * @param option
	 * @return parsed option as int, or default value
	 * @throws ParseException 
	 */	
	public int getParsedOptionValue(String option, int defaultValue) throws ParseException {
		final Object o = this.getParsedOptionValue(option);
		return o != null ? ((Long)o).intValue() : defaultValue; 
	}

	/**
	 * 
	 * @param option
	 * @return parsed option as int
 	 * @throws ParseException
	 */		
	public int getRequiredIntegerOptionValue(String option) throws ParseException {
		return ((Long)this.getParsedOptionValue(option)).intValue();
	}
	
	/**
	 * @see CommandLine.getOptionValue
	 * @param option
	 * @return
	 */
	public String getOptionValue(String option) {
		return this.cmdLine.getOptionValue(option);
	}

	/**
	 * @see CommandLine.getOptionValue
	 * @param option
	 * @return
	 */	
	public String getOptionValue(String option, String defaultValue) {
		return this.cmdLine.getOptionValue(option, defaultValue);
	}
	
	
	private static Options buildAllCommandLineOptions() {
		final Options options = new Options();

		for (Option o : buildHelpCommandLineOptions().getOptions()) {
			options.addOption(o);
		}
		
		options.addOption(Option.builder(CMD_OPT_LOCALPORT)
				                       .longOpt("localport")
				                       .required(true)
				                       .numberOfArgs(1)
				                       .optionalArg(false)
				                       .argName("portnumber")
				                       .type(Number.class)
				                       .desc("Required. Local portnumber to listen on.")				                
				                       .build());
		
		
		options.addOption(Option.builder(CMD_OPT_REMOTEHOST)
                					   .longOpt("remotehost")
                					   .required(true)
                					   .numberOfArgs(1)
                					   .optionalArg(false)
                					   .argName("hostname")
                					   .type(String.class)
                					   .desc("Required. Host to redirect the traffic to.")				                
                					   .build());
		
		options.addOption(Option.builder(CMD_OPT_REMOTEPORT)
                .longOpt("remoteport")
                .required(false)
                .numberOfArgs(1)
                .optionalArg(false)
                .argName("portnumber")
                .type(Number.class)
                .desc("Port to redirect the traffic to on the remothost. If not given, the same port as used locally is assumed")				                
                .build());
		
		options.addOption(Option.builder(CMD_OPT_PROXYHOST)
                .longOpt("proxyhost")
                .required(false)
                .numberOfArgs(1)
                .optionalArg(false)
                .argName("hostname")
                .type(String.class)
                .desc("Host of proxy to use")				                
                .build());	
		
		options.addOption(Option.builder(CMD_OPT_PROXYPORT)
                .longOpt("proxyport")
                .required(false)
                .numberOfArgs(1)
                .optionalArg(false)
                .argName("proxyport")
                .type(Number.class)
                .desc("Port of the given proxy. Required when proxy host is given.")				                
                .build());	

		options.addOption(Option.builder(CMD_OPT_PROXYPORT)
                .longOpt("proxyport")
                .required(false)
                .numberOfArgs(1)
                .optionalArg(false)
                .argName("proxyport")
                .type(Number.class)
                .desc("Required when proxy host is given. Port of the given proxy.")				                
                .build());		
		
		options.addOption(Option.builder(CMD_OPT_PROXYUSER)
                .longOpt("proxyuser")
                .required(false)
                .numberOfArgs(1)
                .optionalArg(false)
                .argName("username")
                .type(String.class)
                .desc("User ID to access proxy")				                
                .build());
		
		options.addOption(Option.builder(CMD_OPT_PROXYPASSWORD)
                .longOpt("proxypassword")
                .required(false)
                .numberOfArgs(1)
                .optionalArg(false)
                .argName("password")
                .type(String.class)
                .desc("Required when proxy user is given. Password for proxy user")				                
                .build());		
		
		options.addOption(Option.builder(CMD_OPT_BUFFERSIZE)
                .longOpt("buffersize")
                .required(false)
                .numberOfArgs(1)
                .optionalArg(false)
                .argName("size")
                .type(Number.class)
                .desc("Size of the buffer in bytes per copyqueue. Used memory is: (buffersize * 2) * connectioncount")				                
                .build());

		return options;
	}
	
	private static Options buildHelpCommandLineOptions() {
		final Options options = new Options();
		options.addOption(Option.builder(CMD_OPT_HELP)
			   .longOpt("help")
			   .required(false)
			    .numberOfArgs(0)
			    .desc("print this message. Overides all other commands.")				                
				.build());
		
		return options;		
	}
	
	
	public static CmdLineHandler parseComandLineArgs (String[] args) throws ParseException {
		final CommandLineParser parser = new DefaultParser();
		
		CommandLine cmdLine = parser.parse( buildHelpCommandLineOptions(), args, true);
			
		if (!cmdLine.hasOption(CmdLineHandler.CMD_OPT_HELP)) {
			cmdLine = parser.parse( buildAllCommandLineOptions(), args, false);
			// TODO: validate arguments, for correct combinations and values
			// ...
		}
				
		return new CmdLineHandler(cmdLine);
	}
	
	
	public static void printUsage(String baseCmd) {
		printUsage(baseCmd, null);
	}
	
	public static void printUsage(String baseCmd, ParseException p) {
		HelpFormatter hf = new HelpFormatter();
		hf.setOptionComparator(null); // No sorting, use define-order
		
		final StringBuilder header = new StringBuilder(hf.getWidth() + 4);
		header.append(hf.getNewLine());
		for (int i=0; i < hf.getWidth();i++) {
			header.append("-");
		}
		
		header.append(hf.getNewLine());
		header.append(hf.getNewLine());
		
		String footer = null;
		if (p != null) {
			footer = header.toString()
				   + "Refer to the information above to correct the following error(s):"
				   + hf.getNewLine() + hf.getNewLine()
				   + p.getMessage();
		}
		
		hf.printHelp(baseCmd, header.toString(), buildAllCommandLineOptions(), footer, true);
	}

}

