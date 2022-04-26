package org.carvis;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Server extends Thread {

	private static final Logger logger = Logger.getLogger(Server.class.getName());
	private final Postgres postgres;
	private final ServerSocket server;
	private ObjectInputStream input;
	private Socket client;
	
	public static void main(String args[]) throws IOException {
		logger.finer("Establishing connection to database.");
		Postgres postgres = new Postgres();
		
		logger.finer("Connecting and starting server.");
		Thread server = new Server(postgres);
		server.start();
		logger.finer("Server is up and running.");
		
		System.out.println("Press Enter to stop server.");
		if(System.in.read()>0)
			server.interrupt();
	}

	public Server(Postgres postgres) {
		this.postgres = postgres;
		final int port = 5000;
		server = connectServer(port);
	}

	@Override
	public void run() {

		while (true) {
			try {
				client = acceptClient(server);
				input = getInput(client);

				Package dataPackage = readPackage(input);
				postgres.insertPackage(dataPackage);
			} catch (Exception e) {
				logger.finest("Server does not work any more.");
				break;
			}
		}
	}

	@Override
	public void interrupt() {
		try {
			logger.finest("Stopping sever.");
			input.close();
			client.close();
			server.close();
		} catch (IOException e) {
			logger.warning("Could not close input.");
			e.printStackTrace();
		} finally {
			super.interrupt();
			logger.finest("Server stopped.");
			System.exit(0);
		}
	}

	private ServerSocket connectServer(Integer port) {
		ServerSocket result = null;
		try {
			logger.finest("Connecting server to port "+port+".");
			result = new ServerSocket(port);
			logger.finest("Server connected to port "+port+".");
		} catch (IOException e) {
			logger.severe("Could not start server on port " + port + "."
					+" Make sure the previous instance of server has stopped. Exiting.");
			e.printStackTrace();
			System.exit(0);
		}
		checkForNull(result,"server");
		return result;

	}

	private Package readPackage(ObjectInputStream input) {
		Package result = null;
		try {
			logger.finest("Reading object from input stream.");
			Object object = input.readObject();
			logger.finest("Object is read.");
			
			logger.finest("Recognizing data package from object.");
			result = (Package) object;
			logger.finest("Data package is recognized.");
		} catch (IOException e) {
			logger.warning("Could not read data.");
		} catch (ClassNotFoundException e) {
			logger.warning("Could not recognize data.");
		}
		checkForNull(result,"data package");
		return result;
	}

	private ObjectInputStream getInput(Socket client) {
		ObjectInputStream result = null;
		try {
			logger.finest("Getting input stream from client.");
			result = new ObjectInputStream(client.getInputStream());
			logger.finest("Got input stream from client.");
		} catch (IOException e) {
			logger.warning("Could not get input stream from client.");
		}
		checkForNull(result,"input stream");
		return result;
	}

	private Socket acceptClient(ServerSocket server) {
		Socket result = null;
		try {
			logger.finest("Asking server to listen for a connection.");
			result = server.accept();
			logger.finest("Server is listening for a connection.");
		} catch (IOException e) {
			logger.warning("Could not get client connected.");
		}
		checkForNull(result,"client");
		return result;
	}
	private void checkForNull(Object objectToCheck,String name) {
		logger.finest("Checking if "+name+" is null.");
		if(objectToCheck==null){
			logger.severe(name+" is null. Exiting.");
			//throw new NullPointerException();
			System.exit(0);
		}else {
			logger.finest(name+" is not null.");	
		}
	}
}
