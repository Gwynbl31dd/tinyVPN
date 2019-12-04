package com.paulin.vpn;

/**
 * Host used for the connection
 * @author Anthony Paulin
 * @since 14/09/2018
 * @version 0.1
 *
 */
public class Host {
	String hostname;
	int port;
	
	/**
	 * Create a host
	 * @param hostname
	 * @param port
	 */
	public Host(String hostname,int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	/**
	 * Create a host
	 * @param hostname
	 */
	public Host(String hostname) {
		this(hostname,22);
	}
	
	/**
	 * Return the hostname
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}
	
	/**
	 * Return the port
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Return hostname+port
	 */
	public String toString() {
		return hostname+":"+port;
	}
}
