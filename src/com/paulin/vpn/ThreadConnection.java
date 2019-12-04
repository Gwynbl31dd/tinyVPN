package com.paulin.vpn;

import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.InteractiveCallback;
import ch.ethz.ssh2.KnownHosts;
import ch.ethz.ssh2.ServerHostKeyVerifier;

/**
 * Thread used to Control the connections
 * @author apaulin
 * @since 17/09/2018
 * @version 0.2
 */
public class ThreadConnection implements Runnable {
	private static int occurence = 0;//Number of connection generated
	private ConnectionData connection;// Connection data
	private static boolean ready = true;//Check if the connection is ready
	private boolean last = false;
	static final String knownHostPath = "~/.ssh/known_hosts";
	static final String idDSAPath = "~/.ssh/id_dsa";
	static final String idRSAPath = "~/.ssh/id_rsa";
	
	
	/**
	 * Thread used to maintain and building a connection
	 * @param connection
	 */
	public ThreadConnection(ConnectionData connection) {
		this.connection = connection;
		occurence++;
	}

	public void setAsLast() {
		this.last = true;
	}
	
	/**
	 * Start and maintain the redirection
	 */
	@Override
	public void run() {
		while(ready==false) {
			//Force blocking the thread until the next works
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ThreadConnection.ready=false;
		//System.out.println("Connection to "+connection.getHost1()+" ready.");
		Connection conn = new Connection(connection.getHost1().getHostname(),connection.getHost1().getPort());
		try {
			SSHMonitor cmon = new SSHMonitor();//Create a monitor
			KnownHosts database = new KnownHosts();  
			conn.addConnectionMonitor(cmon);
			String[] hostkeyAlgos = database.getPreferredServerHostkeyAlgorithmOrder(connection.getHost1().getHostname()); 
			
			if (hostkeyAlgos != null)  {  
				conn.setServerHostKeyAlgorithms(hostkeyAlgos);  
		    }  
			
			conn.connect(new AdvancedVerifier());

			
			/*if (conn.isAuthMethodAvailable(connection.getUsername(), "publickey")) {
			    System.out.println("--> public key auth method supported by server");
			} else {
			    System.out.println("--> public key auth method not supported by server");
			}*/
			
			
			if (conn.isAuthMethodAvailable(connection.getUsername(), "password")) {
				System.out.println("--> password auth method supported by server");

			} else {
				System.out.println("--> password auth method not supported by server");

			}
			boolean isAuthenticated = conn.authenticateWithPassword(connection.getUsername(), connection.getPassword());

			// First, try password
			if (isAuthenticated == false) {
				if (conn.isAuthMethodAvailable(connection.getUsername(), "keyboard-interactive")) {
					System.out.println("--> keyboard interactive auth method supported by server");
					UsernamePasswordInteractiveCallback il = new UsernamePasswordInteractiveCallback();
					isAuthenticated = conn.authenticateWithKeyboardInteractive(connection.getUsername(), il);
				} else {
					System.out.println("--> keyboard interactive auth method not supported by server");

				}
				if (isAuthenticated == false) {
					throw new IOException("Authentification failed for" + connection.getHost1());
				}
			}
			
			conn.createLocalPortForwarder(connection.getPortVPN(), connection.getHost2().getHostname(), connection.getHost2().getPort());
			ThreadConnection.ready=true;
			if(last == true) {
				System.out.println("Tunnel generated :"+occurence);
				System.out.println("connection ready, have fun !");
			}
			while(true) {
				//Keep the connection open
				//Stop the thread for a few seconds.
				try {
					//If the connection is dead, restart the connection
					if(!cmon.isAlive()) {
						System.out.println(cmon.toString());
						System.out.println("Restart connection "+connection);
						conn.close();
						conn.connect();
						isAuthenticated = conn.authenticateWithPassword(connection.getUsername(), connection.getPassword());
						if (isAuthenticated == false) {
							throw new IOException("Authentication to host "+connection.getHost1()+" failed. ");
						}
						System.out.println(connection);
						conn.createLocalPortForwarder(connection.getPortVPN(), connection.getHost2().getHostname(), connection.getHost2().getPort());
						cmon.setAlive();//The connection is alive again
						System.out.println("Restarted connection "+connection);
					}
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			conn.close();//Stop the connection
		}
	}
	
	/**
	 * Return the number the connection generated
	 * @return The occurence
	 */
	public static int getOccurence() {
		return occurence;
	}
	
	public class UsernamePasswordInteractiveCallback implements InteractiveCallback  
	{  
	    @Override  
	    public String[] replyToChallenge(String arg0, String arg1, int arg2, String[] arg3, boolean[] arg4)  
	        throws Exception  
	    {  
	        final String[] password = new String[1];  
	        password[0] = connection.getPassword();  
	        return password;  
	    }  
	  
	} 
	
	class AdvancedVerifier implements ServerHostKeyVerifier  
	{  
	  
	    public boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm,  
	        byte[] serverHostKey) throws Exception  
	    {  
	  
	        return true;  
	    }  
	} 
}
