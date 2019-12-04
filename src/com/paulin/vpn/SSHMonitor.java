package com.paulin.vpn;

import ch.ethz.ssh2.ConnectionMonitor;

/**
 * Monitor the SSH connection
 * @author Anthony Paulin
 * @since  29/10/2018
 * @version 0.1
 *
 */
public class SSHMonitor implements ConnectionMonitor{
	private boolean alive = true;
	private String lastMessage = new String();
	@Override
	public void connectionLost(Throwable arg0) {
		System.out.println("Connection lost "+arg0);
		this.alive = false;
		lastMessage = arg0.toString();
	}
	
	/**
	 * Return the connection status
	 * @return  The connection status
	 */
	public boolean isAlive() {
		return alive;
	}
	
	/**
	 * Set the connection to an alive state
	 */
	public void setAlive() {
		this.alive = true;
	}
	
	public String toString() {
		return lastMessage;
	}
}
