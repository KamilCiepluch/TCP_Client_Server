package clientPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	private Socket clientSocket;
	private final ClientGUI gui;
	private Thread listenerThread;
	public Client(ClientGUI gui)
	{
		this.gui = gui;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	void sendMsg(String msg)  throws IOException
	{

		DataOutputStream send = new DataOutputStream(clientSocket.getOutputStream());
		send.write(msg.getBytes(),0,msg.length());
	}

	void disconnect() throws IOException
	{
		if(!clientSocket.isClosed())
		{
			clientSocket.close();
		}
	}

	void connect(InetAddress address, int port) throws IOException
	{
		clientSocket = new Socket(address,port);
	}


	void startListener()
	{
		listenerThread = new Thread( ()  ->
		{
			try {
				while (clientSocket.isConnected() || !clientSocket.isClosed())
				{
					DataInputStream receive;
					try {
						receive = new DataInputStream(clientSocket.getInputStream());
					}
					catch (Exception e)
					{
						gui.addLog("receive data failed");
						break;
					}

					byte[] buffer = new byte[1024];


					int bytesRead = receive.read(buffer);
					String receivedStr = new String(buffer, 0, bytesRead);
					System.out.println(receivedStr);
					gui.addLog("Msg From Server ("+bytesRead+"b): "+ receivedStr );
				}
			}

			catch (Exception exception)
			{
				gui.setDefaultButtonsSettings();
				gui.addLog("Socket is not connected");

			}
		});
		listenerThread.start();
	}

	void waitForListenerEnd()
	{
		try {
			listenerThread.join();
		}
		catch (Exception exception)
		{
			System.out.println("Should never happened");
		}

	}

}
