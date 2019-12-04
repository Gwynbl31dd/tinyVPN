package com.paulin.vpn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Build a tunnel between devices, using SSH.
 * @author Anthony Paulin
 * @since  13/09/2018
 * @version 0.2
 */
public class Main {
	
	private static final String version = "1.1.4";
	/**
	 * Should be called from a file or from the cli
	 * Use a simple SSH Redirection.
	 * @param args
	 */
	public static void main(String[] args) {	
		//If an argument has been provided
		if(args.length > 0) {
			if(args[0].compareTo("-f")==0 || args[0].compareTo("--file")==0) {
				//Try to get the file
				try {
					if(args.length != 1) {
						File file = new File(args[1]);
						FileReader fileReader = new FileReader(file);
						BufferedReader bufferedReader = new BufferedReader(fileReader);
						StringBuffer stringBuffer = new StringBuffer();
						String line;
						ArrayList<String> listCmd = new ArrayList<String>();
						while ((line = bufferedReader.readLine()) != null) {
							if(!line.startsWith("#") && line.compareTo("")!=0) {//possibility to add a comment
								stringBuffer.append(line);
								listCmd.add(line);
							}
						}
						fileReader.close();
						String[] newList = listCmd.toArray(new String[0]);
						startProcess(newList);
					}
					else {
						System.out.println("Please enter a valid path.");
						showHelp();
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(2);
				}
			}
			else if(args[0].compareTo("-h")==0 || args[0].compareTo("--help")==0) {
				showHelp();
			}
			else if(args[0].compareTo("-v")==0 || args[0].compareTo("--version")==0) {
				showVersion();
			}
			else if(args[0].compareTo("-g")==0 || args[0].compareTo("--gui")==0) {
				Gui client = new Gui();
				client.startGUI();
			}
			else {
				try {
				startProcess(args);
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("Please enter a valid command :");
					showHelp();
					System.exit(2);
				}
			}
		}
		else {
			showHelp();
		}
	}
	
	/**
	 * Generate the connection list.
	 * @param args The list of connections
	 */
	private static void startProcess(String[] args) {
		ArrayList<ConnectionData> connections = new ArrayList<ConnectionData>();
		int previousPort = 22;
		for(int i=0;i<args.length;i++) {
			//Read the args, and build a list of connections.
			String username = args[i].split("@")[0].split(":")[0];
			String password = args[i].split("@")[0].split(":")[1];
			int vpnPort = Integer.parseInt(args[i].split("@")[3]);
			String[] hostsValues1 = args[i].split("@")[1].split(":");
			String[] hostsValues2 = args[i].split("@")[2].split(":");
			Host host1;
			Host host2;
			//Detect default ssh port
			if(hostsValues1.length < 2) {
				//Check the default value
				if(hostsValues1[0].compareTo("")==0) {
					hostsValues1[0] = "127.0.0.1";
					host1 = new Host(hostsValues1[0],  previousPort);
				}
				else {
					host1 = new Host(hostsValues1[0],  22);
				}
			}
			else {
				host1 = new Host(hostsValues1[0],  Integer.parseInt(hostsValues1[1]));
			}
			if(hostsValues2.length < 2) {
				host2 = new Host(hostsValues2[0],  22);
			}	
			else {
				host2 = new Host(hostsValues2[0],  Integer.parseInt(hostsValues2[1]));
			}
			connections.add(new ConnectionData(host1, host2,username,password, vpnPort));
			previousPort =  vpnPort;
		}
		//Build the connections
		for(int i=0;i<connections.size();i++) {
			ThreadConnection con = new ThreadConnection(connections.get(i));
			if(i==connections.size()-1) {
				con.setAsLast();//Set the Thread as the last.
			}
			Thread t = new Thread(con);
			t.start();
		}
	}
	
	/**
	 * Display the current version
	 */
	private static void showVersion() {
		System.out.println("tinyVPN, version "+version);
		System.out.println("Contact : apaulin@cisco.com");
	}
	
	/**
	 * Display the help
	 */
	private static void showHelp() {
		System.out.println("General usage:");
		System.out.println("java -jar tinyVPN.jar [USERNAME:PASSWORD@HOST_FROM:PORT@HOST_TO:PORT@LOCAL_PORT_TO_FORWARD]");
		System.out.println("----------");
		System.out.println("File usage:");
		System.out.println("You can call your connection from a file, by passing the connections line per line.\n");
		System.out.println("USERNAME1:PASSWORD1@HOST_FROM1:PORT@HOST_TO1:PORT@LOCAL_PORT_TO_FORWARD1");
		System.out.println("USERNAME2:PASSWORD2@HOST_FROM1:PORT@HOST_TO2:PORT@LOCAL_PORT_TO_FORWARD2\n");
		System.out.println("And calling it via : \n java -jar tinyVPN.jar -f VPN_FILE");
		System.out.println("You can also add a comment by starting a line by #");
		System.out.println("----------");
		System.out.println("Extra :");
		System.out.println("You can also open a ssh sheel from tinyVpn -g|--gui :");
		showVersion();
	}
}
