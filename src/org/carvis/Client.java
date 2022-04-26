package org.carvis;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;

public class Client {

	public static void main(String[] args){
		final String host = "localhost";
		final Integer port = 5000;
		Socket socket = null;
		try {
			socket = new Socket(host,port);
		} catch (UnknownHostException e) {
			Logger.getGlobal().severe("IP address of a host could not be determined.");
		} catch (IOException e) {
			Logger.getGlobal().severe(e.getLocalizedMessage()
					+ " Could not connect to server"
					+ " on "+host+":"+port+". Make sure the server is up and running.");
			System.exit(0);
		}
		socket = Objects.requireNonNull(socket,"Failed creating new socket.");
		
		ObjectOutputStream temporaryOut = null;
		try {
			temporaryOut = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			Logger.getGlobal().severe("Could not get output stream.");
		}
		ObjectOutputStream output = Objects.requireNonNull(temporaryOut,"Failed creating new output stream.");
		
		Package dataPackage = new Package();
		Random random = new Random(); 
		dataPackage.setId(random.nextInt());
		final int length = Package.getNameLengthLimit();
		dataPackage.setName(new RandomString(length).nextString());
		dataPackage.setValue(random.nextDouble());
		
		System.out.println("Client is sending package \n"+
				"\nid: "+dataPackage.getId()+" "+
				"\nname: "+dataPackage.getName()+" "+
				"\nvalue:"+dataPackage.getValue()+"\n");
		
		try {
			output.writeObject(dataPackage);
		} catch (IOException e) {
			Logger.getGlobal().severe("Could not write data package.");
		}
		System.out.println("Package is sent.");
		
		try {
			output.close();
		} catch (IOException e) {
			Logger.getGlobal().warning("Could not close output.");
		}
	}
}