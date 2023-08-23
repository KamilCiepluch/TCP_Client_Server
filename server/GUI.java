package serverPackage;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {


	private static GUI gui;

	private Server server;
	private static Integer connectedClients;


	private static JLabel portLabel;
	private static JLabel clientsLabel;
	private static JTextField portInput;
	private static JButton startButton;
	private static JButton stopButton;
	private static JTextArea serverLogsArea;

	private GUI() {


		this.setTitle("Server");
		portLabel = new JLabel("Listening port");

		portInput = new JTextField("7");
		portInput.setForeground(Color.black);
		portInput.setFont(new Font("Calibri", Font.PLAIN,12));

		clientsLabel = new JLabel("Connected clients: " + connectedClients);
		startButton = new JButton("Start server");
		stopButton = new JButton("Stop server");

		serverLogsArea = new JTextArea();
		serverLogsArea.setFont(new Font("Calibri", Font.PLAIN,12).deriveFont(Font.BOLD));

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
										.addComponent(portLabel)
										.addComponent(portInput)
										.addComponent(startButton)
										.addComponent(stopButton))
								//drugi rzad
								.addComponent(clientsLabel)
								.addComponent(serverLogsArea)
		));

		//vertical layout
		layout.setVerticalGroup(
				layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(portLabel)
								.addComponent(portInput)
								.addComponent(startButton)
								.addComponent(stopButton))

						.addComponent(clientsLabel)
						.addComponent(serverLogsArea)
		);

		this.setSize(600,400);
		this.setVisible(true);

		connectedClients = 0;


		startButton.addActionListener(e ->
		{
			if(Server.isServerAlive())
			{
				server.closeServer();
			}

			System.out.println("Started server");
			server = Server.getServer();
			server.startServer();
		});
		stopButton.addActionListener(e ->
		{
			if(Server.isServerAlive())
			{
				System.out.println("Close server");
				Server.getServer().closeServer();
			}
		});


		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				try {
					Server.getServer().closeServer();
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
	public static GUI getGui() {


		synchronized (GUI.class)
		{
				if(gui == null)
				{
					gui = new GUI();
				}
		}
		return gui;
	}

	public static void addNewLog(String str)
	{
		synchronized (GUI.class)
		{
			serverLogsArea.insert(str +'\n',0);
		}
	}
	public static void increaseNumberOfClients() {
		synchronized (GUI.class)
		{
			connectedClients++;
		}
	}
	public static void decreaseNumberOfClients() {
		synchronized (GUI.class)
		{
			connectedClients--;
		}
	}
	public static void refreshClientLabel()
	{
		synchronized (GUI.class)
		{
			clientsLabel.setText("Connected clients: " + connectedClients);
		}
	}

	public int getPort()
	{
		String txt = portInput.getText();
		if(txt.length()==0) return 7;
		return Integer.parseInt(txt);
	}
	public static void main(String[] args)  {

		GUI.getGui();

	}
}


