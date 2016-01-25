package de.einwesen.porttunnel.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.LogFactory;

import de.einwesen.porttunnel.log.ConditionalStackLog;
import de.einwesen.porttunnel.log.ConditionalStackLog.STACK_LOGLEVEL;

/**
 * Forwards traffic on a local port to an Destination port.
 * 
 * @author EinWesen
 *
 */
public class ConnectionProcessor implements Runnable {

	private final int localPort;
	private final String remoteHost;
	private final int remotePort;
	private final TunnelSocketFactory socketFactory;
	private boolean isRunning = false;
	
	private ServerSocket srvSocket = null;
	
	/**
	 * @param localPort
	 * @param remoteHost
	 * @param remotePort
	 * @param socketFactory
	 */
	public ConnectionProcessor(int localPort, String remoteHost, int remotePort, TunnelSocketFactory socketFactory) {
		this.localPort = localPort;
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.socketFactory = socketFactory;
	}

	private static final ConditionalStackLog LOGGER = ConditionalStackLog.getInstance(LogFactory.getLog(ConnectionProcessor.class));
	
	@Override
	public void run() {

		LOGGER.info("Fowarding traffic to: " + remoteHost + ":" + remotePort );
		LOGGER.info("Connection Type: " + socketFactory.getConnectionTypeString() );
		

		try  {
			srvSocket = this.socketFactory.openSocket(remoteHost, remotePort);
			LOGGER.info("Listening on: " + srvSocket.getLocalSocketAddress() );
		} catch (IOException e) {
			LOGGER.fatal("Can not open local socket", e, STACK_LOGLEVEL.TRACE);
		}
			
		if (srvSocket != null) {
			isRunning = true;
			while (isRunning) {
				try {
					LOGGER.trace("Waiting for connection...");
					final Socket clientSocket = srvSocket.accept();
					LOGGER.info("Incoming from " + clientSocket.getRemoteSocketAddress() + " . Connecting to target...");

					// Try to create a tunnel to the destination
					Socket targetSocket = null;
					
					try {
						targetSocket = new Socket(remoteHost, remotePort);

						//Start sending to target first
						final TrafficProcessor source2Target = new TrafficProcessor(clientSocket, targetSocket, 1024);
						source2Target.setSourceName(clientSocket.getRemoteSocketAddress().toString());
						source2Target.setTargetName(remoteHost + ":" + remotePort);
						
						new Thread(source2Target, TrafficProcessor.class.getSimpleName() + " " + source2Target.getSourceName() + " -> " + source2Target.getTargetName()).start();

						final TrafficProcessor target2source = new TrafficProcessor(targetSocket, clientSocket, 1024);
						target2source.setTargetName(clientSocket.getRemoteSocketAddress().toString());
						target2source.setSourceName(remoteHost + ":" + remotePort);

						new Thread(target2source, TrafficProcessor.class.getSimpleName() + " " + target2source.getSourceName() + " -> " + target2source.getTargetName()).start();
						
						
					} catch (Throwable e) {
						LOGGER.error("Could not connect to target. Closing incoming connection.", e,  STACK_LOGLEVEL.DEBUG);
						try {
							clientSocket.close();
						} catch (Throwable e1) {
							LOGGER.trace("Could not close incoming connection", e1);
						}
					}
					
				} catch (Throwable e) {
					LOGGER.error("Error waiting for connection", e,  STACK_LOGLEVEL.DEBUG);
				}
			}
			
		}
		
		isRunning = false;
		
	}
	
	public void stopRunning() {
		if (isRunning) {
			try {
				this.srvSocket.close();
			} catch (IOException e) {
				LOGGER.trace("Could not close server connection", e);
			}			
			isRunning = false;
		}
	}

}
