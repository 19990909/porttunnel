package de.einwesen.porttunnel.net;

import java.io.IOException;
import java.net.Socket;

/**
 * 
 * @author EinWesen
 */
public class SimpleForwardSocketFactory implements TunnelSocketFactory {

	@Override
	public Socket openSocket(String host, int port) throws IOException {
		return new Socket(host, port);
	}

	@Override
	public String getConnectionTypeString() {
		return "DIRECT";
	}

}
