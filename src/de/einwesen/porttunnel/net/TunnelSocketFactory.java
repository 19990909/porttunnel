package de.einwesen.porttunnel.net;

import java.io.IOException;
import java.net.Socket;

public interface TunnelSocketFactory {
	public Socket openSocket(String host, int port) throws IOException;
	public String getConnectionTypeString();
}
