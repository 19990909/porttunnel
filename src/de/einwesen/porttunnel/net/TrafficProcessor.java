package de.einwesen.porttunnel.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.logging.LogFactory;

import de.einwesen.porttunnel.log.ConditionalStackLog;
import de.einwesen.porttunnel.log.ConditionalStackLog.STACK_LOGLEVEL;

/**
 * 
 * @author EinWesen
 * 
 * Reads from one socket, and copy it's data to another as long as 
 * both ends are up and running.
 *
 */
public class TrafficProcessor implements Runnable {

	private static final ConditionalStackLog LOGGER = ConditionalStackLog.getInstance(LogFactory.getLog(TrafficProcessor.class));	
	private static final int BAD_READ_MAX = 10; 
	
	private final Socket sourceSocket;
	private final Socket targetSocket;
	private final int    bufferSize;
	
	private final InputStream is;
	private final OutputStream os;
	
	private String sourceName = "SOURCE";
	private String targetName = "TARGET";
	
	/** caches the logging prefix for performance **/
	private String logPrefix = "[SOURCE -> TARGET] ";
		
	/**
	 * @param sourceSocket
	 * @param targetSocket
	 * @param bufferSize
	 * @throws IOException 
	 */
	public TrafficProcessor(Socket sourceSocket, Socket targetSocket, int bufferSize) throws IOException {
		this.sourceSocket = sourceSocket;
		this.targetSocket = targetSocket;
		this.bufferSize = bufferSize;

		sourceName = sourceSocket.getInetAddress().getHostAddress();
		targetName = targetSocket.getInetAddress().getHostAddress();
		generateLogPrefix(); 
		
		is = sourceSocket.getInputStream();
		os = targetSocket.getOutputStream();
	}

	@Override
	public void run() {
		
		final byte[] buffer = new byte[bufferSize];
		int readBytes = 0;
		int badReadCounter = 0;
		
		LOGGER.debug(this.logPrefix + "running ("+bufferSize+") ... ");
		
		while (checkSocketState(sourceSocket, 0)  && checkSocketState(targetSocket, 1) && badReadCounter < BAD_READ_MAX) {
			   
			try {
				readBytes = is.read(buffer);			
			} catch (Throwable e) {
				readBytes = 0;
				LOGGER.error( logPrefix + "Error reading source", e, STACK_LOGLEVEL.TRACE);
			}
			if (readBytes > 0) {
				badReadCounter = 0;
				try {				
					os.write(buffer, 0, readBytes);				
				} catch (Throwable e) {
					LOGGER.error( "Error writing to target", e, STACK_LOGLEVEL.TRACE);
				}										
			} else {
				if (++badReadCounter == BAD_READ_MAX) {
					LOGGER.debug(this.logPrefix + "No more data read from socket ("+BAD_READ_MAX+" times)");					
				}
			}
			
		}
		
		shutdownAndCloseSocket(sourceSocket, 0, sourceName);
		shutdownAndCloseSocket(targetSocket, 1, targetName);
		LOGGER.info(this.logPrefix + "Stopped");
	}
	
	private boolean checkSocketState(Socket s, int flagInOut) {		
		return (!s.isClosed() && ((flagInOut == 0 && !s.isInputShutdown()) || (flagInOut == 1 && !s.isOutputShutdown())));		
	}
	
	private void shutdownAndCloseSocket(Socket s, int flagInOut, String logName) {
		if (s.isClosed()) {
			LOGGER.trace(this.logPrefix + " " + logName + " is closed");
		} else {
			LOGGER.trace(this.logPrefix + "Disconnecting " + logName);

//			try {
//				if (flagInOut == 0) {
//					s.shutdownInput();
//				} else if (flagInOut == 1){
//					s.shutdownOutput();
//				}
//			} catch (IOException e) {
//				LOGGER.trace(this.logPrefix + "can not shutdown " + logName, e);
//			}				

			try {
				s.close();
			} catch (IOException e) {
				LOGGER.trace(this.logPrefix + "can not close " + logName, e);
			}						
		}		
	}

	private void generateLogPrefix() {
		this.logPrefix = "[" + sourceName + " -> " + targetName + "] ";		
	}
		
	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
		generateLogPrefix();
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
		generateLogPrefix();
	}

}
