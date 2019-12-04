package com.paulin.vpn;

/**
 * Connection system with status
 * @author Anthony Paulin
 * @since  13/09/2018
 * @version 0.2
 */
public class ConnectionData {
	private boolean connected = false;//Status of the connection
	private Host host1,host2;//Host to join
	private String username;//Username to use
	private String password;//Password to use
	private int portVPN;//Port used for the redirection
	
	/**
	 * Connection data used to generate the port forwarding
	 * @param host1
	 * - This host is used for the port forwarding, The username and password are used to get access to this host
	 * @param host2
	 * - Host where the connection is redirected
	 * @param username
	 * - Username to get access to host1
	 * @param password
	 * - Password to get access to host1
	 * @param portVPN
	 * - Port used for the local redirection
	 */
	public ConnectionData(Host host1,Host host2,String username,String password,int portVPN) {
		this.host1 = host1;
		this.host2 = host2;
		this.username = username;
		this.password = password;
		this.portVPN = portVPN;
	}

	/**
	 * Check if host is connected (Update by a Thread)
	 * @return get the connection status
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Set the connection status
	 * @param connected
	 */
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	/**
	 * Get the first host
	 * @return the first host
	 */
	public Host getHost1() {
		return host1;
	}

	/**
	 * Get the second host
	 * @return the second host
	 */
	public Host getHost2() {
		return host2;
	}

	/**
	 * Get the username
	 * @return The username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Get the password
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Get the VPN port (Local forwarding)
	 * @return the vpn port
	 */
	public int getPortVPN() {
		return portVPN;
	}
	
	/**
	 * Return the connection summary
	 */
	public String toString() {
		String cmd = host2+" -> 127.0.0.1:"+portVPN;
		return cmd;
	}
}
