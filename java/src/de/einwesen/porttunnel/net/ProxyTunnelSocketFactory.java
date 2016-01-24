package de.einwesen.porttunnel.net;

import java.io.IOException;
import java.net.Socket;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.ProxyClient;

/**
 * 
 * @author EinWesen
 * 
 * Creates a socket, whichs is tunneled through an (HTTPS)proxy
 */
public class ProxyTunnelSocketFactory implements TunnelSocketFactory {
	
	private HttpHost proxyHost = null;
	private UsernamePasswordCredentials proxyCredentials = null;
    private ProxyClient proxyClient = null;
	
	/**
	 * @param proxyHost
	 * @param proxyPort
	 * @param proxyUser
	 * @param proxyPassword
	 */
	public ProxyTunnelSocketFactory(String proxyHost, int proxyPort, String proxyUser, String proxyPassword) {
		this.proxyHost = new HttpHost(proxyHost, proxyPort);
		
		if (proxyUser != null || proxyPassword != null) {
			this.proxyCredentials = new UsernamePasswordCredentials(proxyUser, proxyPassword);			
		}
		
		this.proxyClient = new ProxyClient();
	}

	/**
	 * @param proxyHost
	 * @param proxyPort
	 */	
	public ProxyTunnelSocketFactory(String proxyHost, int proxyPort) {
		this(proxyHost, proxyPort, null, null);
	}

	
	@Override
	public Socket openSocket(String host, int port) throws IOException {
		final HttpHost target = new HttpHost(host, port);
		try {
			return this.proxyClient.tunnel(this.proxyHost, target, this.proxyCredentials);
		} catch (HttpException e) {
			throw new IOException("Unable to create socket. Reason: " + e.getMessage(), e);
		}
	}

	@Override
	public String getConnectionTypeString() {
		return proxyHost.toHostString() + (this.proxyCredentials != null ? " with AUTH" : "");
	}
	
}
