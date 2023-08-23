package clientPackage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.*;

public class ClientGUI extends JFrame {

	private static JButton connectButton;
	private static JButton disconnectButton;
	private static JLabel ipDescLabel;
	private static JTextField ipInput;
	private static JLabel portDescLabel;
	private static JTextField portInput;
	private static JButton sendButton;
	private static JLabel msgDescLabel;
	private static JTextField msgInput;
	private static JTextArea clientLogs;

	private final Client client;

	private ClientGUI() {


		client = new Client(this);

		this.setTitle("Client");

		connectButton = new JButton("Connect");
		disconnectButton = new JButton("Disconnect");
		disconnectButton.setEnabled(false);
		ipDescLabel = new JLabel("TCP server IP");
		ipInput = new JTextField("localhost");
		portDescLabel = new JLabel("TCP server port");
		portInput = new JTextField("7");

		sendButton = new JButton("Send");
		sendButton.setEnabled(false);
		msgDescLabel = new JLabel("Your message");
		msgInput = new JTextField();

		clientLogs = new JTextArea();
		clientLogs.setFont(new Font("Calibri", Font.PLAIN,12).deriveFont(Font.BOLD));


		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);

		// automatic gap insertion
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);


		//horizontal layout
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								// pierwszy rzad
								.addGroup(layout.createSequentialGroup()
										.addComponent(connectButton)
										.addComponent(disconnectButton)
										.addComponent(ipDescLabel)
										.addComponent(ipInput)
										.addComponent(portDescLabel)
										.addComponent(portInput))
								//drugi rzad
								.addGroup(layout.createSequentialGroup()
										.addComponent(sendButton)
										.addComponent(msgDescLabel)
										.addComponent(msgInput))
								//trzeci rzad
								.addComponent(clientLogs)
						));





		//vertical layout
		layout.setVerticalGroup(
				layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(connectButton)
								.addComponent(disconnectButton)
								.addComponent(ipDescLabel)
								.addComponent(ipInput)
								.addComponent(portDescLabel)
								.addComponent(portInput))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(sendButton)
								.addComponent(msgDescLabel)
								.addComponent(msgInput))
						.addComponent(clientLogs)
		);

		this.setSize(600,400);
		this.setVisible(true);


		connectButton.addActionListener(
				e ->
				{
					//todo connect
					try {
						InetAddress address = InetAddress.getByName(ipInput.getText());
						String portStr = portInput.getText();
						int port = Integer.parseInt(portStr);
						client.connect(address,port);
						client.startListener();

						connectButton.setEnabled(false);
						disconnectButton.setEnabled(true);
						sendButton.setEnabled(true);
						addLog("Connected to server");

					}
					catch (UnknownHostException e1)
					{
						System.out.println("Incorrect ip address");
						addLog("Incorrect ip address");
					}
					catch (NumberFormatException e2)
					{
						System.out.println("Could not parse port to int");
						addLog("Incorrect port");
					}
					catch (IOException e3)
					{
						System.out.println("Could not bind socket");
						addLog("Could not connect to server");
					}
				}
		);



		disconnectButton.addActionListener(
				e ->
				{
					//todo disconnect
					try {
						client.disconnect();
						client.waitForListenerEnd();
						connectButton.setEnabled(true);
						disconnectButton.setEnabled(false);
						sendButton.setEnabled(false);
						addLog("Disconnected from server");
					}
					catch (Exception e1)
					{
						System.out.println("Could not close socket");
					}


				}
		);


		sendButton.addActionListener(
				e ->
				{
					try {

							String text = msgInput.getText();
							if(text.length()!=0)
							{
								client.sendMsg(text);
								addLog("Msg to server(" + text.length() + "b): " +text);

							}
					}
					catch (Exception exception)
					{
						System.out.println("Could not send msg");
						addLog("Could not send msg");
						addLog("You are not longer connected to server");
						connectButton.setEnabled(true);
						disconnectButton.setEnabled(false);
						sendButton.setEnabled(false);

					}
				}

		);

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				try {
					if(client.getClientSocket().isConnected())
						client.disconnect();
					connectButton.setEnabled(true);
					disconnectButton.setEnabled(false);
					sendButton.setEnabled(false);
					addLog("Disconnected from server");
					dispose();
				}
				catch (Exception e1)
				{
					System.out.println("Could not close socket");
					dispose();
				}
			}
		});
	}

	void setDefaultButtonsSettings()
	{
		connectButton.setEnabled(true);
		disconnectButton.setEnabled(false);
		sendButton.setEnabled(false);
	}

	void addLog(String msg)
	{
		clientLogs.insert( msg + '\n',0);
	}

	public static void main(String[] args) {
		 new ClientGUI();
	}
}
