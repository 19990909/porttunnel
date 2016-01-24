package de.einwesen.porttunnel;

import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.LogFactory;

import de.einwesen.porttunnel.log.ConditionalStackLog;
import de.einwesen.porttunnel.net.ConnectionProcessor;
import de.einwesen.porttunnel.net.ProxyTunnelSocketFactory;
import de.einwesen.porttunnel.net.SimpleForwardSocketFactory;
import de.einwesen.porttunnel.net.TunnelSocketFactory;

/**
 * 
 * @author EinWesen
 *
 */
public class LocalPortRedirect {
	
	private static final ConditionalStackLog LOGGER = ConditionalStackLog.getInstance(LogFactory.getLog(LocalPortRedirect.class));
		
	public static void main(String[] args) {

		final String[] myArgs = args;
		
		try {
			final CmdLineHandler cmdLine = CmdLineHandler.parseComandLineArgs(myArgs);
			
			if (!cmdLine.hasOptions() || cmdLine.hasOption(CmdLineHandler.CMD_OPT_HELP)) {
				CmdLineHandler.printUsage(LocalPortRedirect.class.getSimpleName());
			} else {
					
				// Getting basic options
				final int localPort = cmdLine.getRequiredIntegerOptionValue(CmdLineHandler.CMD_OPT_LOCALPORT);
				final String remoteHost = cmdLine.getOptionValue(CmdLineHandler.CMD_OPT_REMOTEHOST);
				final int remotePort = cmdLine.getParsedOptionValue(CmdLineHandler.CMD_OPT_REMOTEPORT, localPort);			
				
				
				TunnelSocketFactory socketFactory = null;				
				
				if (cmdLine.hasOption(CmdLineHandler.CMD_OPT_PROXYHOST)) {
					socketFactory = new ProxyTunnelSocketFactory(cmdLine.getOptionValue(CmdLineHandler.CMD_OPT_PROXYHOST), 
																 cmdLine.getRequiredIntegerOptionValue(CmdLineHandler.CMD_OPT_PROXYPORT),
																 cmdLine.getOptionValue(CmdLineHandler.CMD_OPT_PROXYUSER), 
																 cmdLine.getOptionValue(CmdLineHandler.CMD_OPT_PROXYPASSWORD));
				} else {
					socketFactory = new SimpleForwardSocketFactory();
				}
				
				// For the time being, we do not need different threads, since we are listening on a single port
				final ConnectionProcessor connectionProcessor = new ConnectionProcessor(localPort, remoteHost, remotePort, socketFactory);
				
				// Define cleanup 
				Runtime.getRuntime().addShutdownHook(new Thread(){
					@Override
					public synchronized void start() {
						connectionProcessor.stopRunning();
					}					
				});
				
				// Let's go!
				connectionProcessor.run();
				
				LOGGER.info("EXIT");
			
			}

		
		} catch (ParseException e) {
			CmdLineHandler.printUsage(LocalPortRedirect.class.getSimpleName(), e);
		} catch (Throwable t) {
			LOGGER.fatal("Execution error", t);
		}
		
		
	}

}
