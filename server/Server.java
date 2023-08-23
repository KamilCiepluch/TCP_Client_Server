package serverPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;


public class Server extends Thread {


	private static final int defaultPortNumber = 7;
	private static Server server;
	private ServerSocket serverSocket;
	private ArrayList<Socket > clientsSockets;
	private ArrayList<Thread> activeThreads;
	private static int port = defaultPortNumber;
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	private int clientID;


	private Server()
	{
		try {
			this.serverSocket = new ServerSocket();
			this.clientsSockets = new ArrayList<>();
			this.activeThreads = new ArrayList<>();
			this.clientID =0;
		}
		catch (Exception e)
		{
			System.out.println("Failed to create server");
			GUI.addNewLog("Failed to create server");
		}

	}

	public static Server getServer() {
		if(server == null)
		{
			server = new Server();
		}
		return server;
	}



	public static boolean isServerAlive()
	{
		synchronized (Server.class)
		{
			return server != null;
		}
	}

	public void startServer()
	{
		try {
			server = getServer();
			port = GUI.getGui().getPort();
			InetAddress address =  InetAddress.getByName("0.0.0.0");
			Server.getServer().getServerSocket().bind(new InetSocketAddress(address, port));
			GUI.addNewLog("Server start working at port: " + port );
			start();
		}
		catch (UnknownHostException u)
		{
			System.out.println("Incorrect host name - Should never happened");
			GUI.addNewLog("Incorrect host name - Should never happened");
		}
		catch (IOException e)
		{
			System.out.println("Bind failed");
			GUI.addNewLog("Bind failed");
			closeServer();
		}


	}
	public  void closeServer()
	{
		if(server!= null)
		{
			synchronized (Server.class)
			{
				System.out.println("clients to close: " + clientsSockets);
				for (Socket socket:clientsSockets)
				{
					try {
						socket.close();
					//	socket.shutdownInput();
					//	socket.shutdownOutput();
						System.out.println("Socket is closed:" + socket.isClosed() );
					}
					catch (Exception e)
					{
						System.out.println("Could not close client socket: " + socket);
						GUI.addNewLog("Could not close client socket: " + socket);
					}
				}

				clientsSockets.clear();
				try {
					getServerSocket().close();
				}
				catch (Exception e)
				{
					System.out.println("Could not close server socket: ");
					GUI.addNewLog("Could not close server socket: ");
				}
				server = null;
				port = defaultPortNumber;
				try {
					join();
				}
				catch (Exception e)
				{
					System.out.println("Should never happened");
				}


			}

		}


	}


	int getNumberOfClients()
	{
		synchronized (this)
		{
			return clientsSockets.size();
		}
	}

	@Override
	public void run() {

		try {
			GUI.addNewLog("Waiting for client");
			while (isServerAlive() && !serverSocket.isClosed())
			{

				//clientLimits.acquire();
				Socket socket = Server.getServer().getServerSocket().accept();


				//getNumberOfClients()<3
				if(getNumberOfClients()<3)
				{
					GUI.addNewLog("New client accepted: " + socket.getInetAddress().getHostAddress() + ":" +socket.getPort());
					clientsSockets.add(socket);
					GUI.increaseNumberOfClients();
					GUI.refreshClientLabel();

					int id;
					synchronized (this)
					{
						clientID++;
						id = clientID;
					}

					Thread task = new Thread(() -> {
						try {
							while (socket.isConnected() || !socket.isClosed())
							{
								DataInputStream in = new DataInputStream(socket.getInputStream());
								byte[] buffer = new byte[1024];
								int bytesRead = in.read(buffer);
								if(bytesRead < 1) break;


								String modifiedMsg = new String(buffer, 0, bytesRead);
								System.out.println(modifiedMsg);
								GUI.addNewLog("Client #" + id + "   " +socket.getInetAddress().getHostAddress()
										+ ":" +socket.getPort()  +" Msg: "+ modifiedMsg);

								DataOutputStream out = new DataOutputStream(socket.getOutputStream());
								out.write(buffer,0,bytesRead);

							}
							clientsSockets.remove(socket);
							GUI.addNewLog("Client #" + id + "   " +socket.getInetAddress().getHostAddress()
									+ ":" +socket.getPort()  +" disconnected");
							GUI.decreaseNumberOfClients();
							GUI.refreshClientLabel();
						//	clientLimits.release();
						}
						catch (Exception  e)
						{
							GUI.addNewLog("Client #" +clientID + "   " +socket.getInetAddress().getHostAddress()
									+ ":" +socket.getPort()  +" disconnected");
							System.out.println(e.getMessage());
							GUI.decreaseNumberOfClients();
							clientsSockets.remove(socket);
							GUI.refreshClientLabel();
						//	clientLimits.release();
						}
					});
					task.start();
					activeThreads.add(task);
				}
				else
				{
					String msg = "SERVER BUSY";
					GUI.addNewLog("Refused connection: " +  socket.getInetAddress().getHostAddress()
							+ ":" +socket.getPort()  +"  - " + msg);
					socket.close();
				}
			}
		}
		catch (Exception ignored){

			System.out.println("Server closing...");
		}

		for(Thread thread: activeThreads)
		{
			try {
				thread.join();
			}
			catch (Exception e)
			{
				System.out.println("Could not join thread");
			}
		}
		activeThreads.clear();
		GUI.addNewLog("Server closed");
	}

}
