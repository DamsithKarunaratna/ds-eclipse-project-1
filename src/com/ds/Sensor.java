package com.ds;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * TODO : MODIFY THIS! A simple Swing-based client for the chat server.
 * Graphically it is a frame with a text field for entering messages and a
 * textarea to see the whole dialog.
 *
 * The client follows the Chat Protocol which is as follows. When the server
 * sends "SUBMITNAME" the client replies with the desired screen name. The
 * server will keep sending "SUBMITNAME" requests as long as the client submits
 * screen names that are already in use. When the server sends a line beginning
 * with "NAMEACCEPTED" the client is now allowed to start sending the server
 * arbitrary strings to be broadcast to all chatters connected to the server.
 * When the server sends a line beginning with "MESSAGE " then all characters
 * following this string should be displayed in its message area.
 */
public class Sensor {

	private static BufferedReader in;
	private static PrintWriter out;
	private static final int PORT = 9001;
	private static final int TIMEOUT = 5; // timeout in seconds before next update (5 minutes = 60 * 5)
	private static double temperature;
	private static double smoke;
	private static double battery;
	private static double co2;
	public static String sensorUID;
	JFrame frame = new JFrame("Sensor");
	JTextField textField = new JTextField(10);
	public static JTextField textFieldTemperature = new JTextField(20);
	public static JTextField textFieldcO2 = new JTextField(20);
	public static JTextField textFieldSmoke = new JTextField(20);
	public static JTextField textFieldBattery = new JTextField(20);
	private final JButton btnTestTemp = new JButton("TEST");
	private final JButton btnTestCarbon = new JButton("TEST");
	private final JButton btnTestSmoke = new JButton("TEST");
	private final JButton btnTestBattery = new JButton("TEST");

	/**
	 * Constructs the client by laying out the GUI. This was made using Eclipse
	 * WindowBuilder
	 */
	public Sensor() {
		frame.getContentPane().setLayout(new GridLayout(0, 1));
		JPanel pane = new JPanel();
		frame.getContentPane().add(pane);
		GridBagLayout gbl_pane = new GridBagLayout();
		gbl_pane.columnWidths = new int[] { 0, 115, 89, 0 };
		gbl_pane.rowHeights = new int[] { 0, 20, 20, 0, 20, 20, 20, 0 };
		gbl_pane.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_pane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		pane.setLayout(gbl_pane);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(0, 0, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 1;
		JLabel label = new JLabel("Sensor ID: ");
		pane.add(label, gbc);

		// initializing Layout GUI
		textField.setEditable(false);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.BOTH;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 1;
		pane.add(textField, gbc_textField);
		GridBagConstraints gbc_1 = new GridBagConstraints();
		gbc_1.anchor = GridBagConstraints.EAST;
		gbc_1.fill = GridBagConstraints.VERTICAL;
		gbc_1.insets = new Insets(0, 0, 5, 5);
		gbc_1.gridx = 0;
		gbc_1.gridy = 2;
		JLabel label_1 = new JLabel("Temperature: ");
		pane.add(label_1, gbc_1);
		textFieldTemperature.setEditable(false);
		GridBagConstraints gbc_textFieldTemperature = new GridBagConstraints();
		gbc_textFieldTemperature.fill = GridBagConstraints.BOTH;
		gbc_textFieldTemperature.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldTemperature.gridx = 1;
		gbc_textFieldTemperature.gridy = 2;
		pane.add(textFieldTemperature, gbc_textFieldTemperature);

		GridBagConstraints gbc_btnTestTemp = new GridBagConstraints();
		gbc_btnTestTemp.insets = new Insets(0, 0, 5, 0);
		gbc_btnTestTemp.gridx = 2;
		gbc_btnTestTemp.gridy = 2;

		btnTestTemp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});

		pane.add(btnTestTemp, gbc_btnTestTemp);
		GridBagConstraints gbc_2 = new GridBagConstraints();
		gbc_2.anchor = GridBagConstraints.EAST;
		gbc_2.fill = GridBagConstraints.VERTICAL;
		gbc_2.insets = new Insets(0, 0, 5, 5);
		gbc_2.gridx = 0;
		gbc_2.gridy = 3;
		JLabel label_2 = new JLabel("Co2 Level : ");
		pane.add(label_2, gbc_2);
		textFieldcO2.setEditable(false);
		GridBagConstraints gbc_textFieldcO2 = new GridBagConstraints();
		gbc_textFieldcO2.fill = GridBagConstraints.BOTH;
		gbc_textFieldcO2.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldcO2.gridx = 1;
		gbc_textFieldcO2.gridy = 3;
		pane.add(textFieldcO2, gbc_textFieldcO2);

		GridBagConstraints gbc_btnTestCarbon = new GridBagConstraints();
		gbc_btnTestCarbon.insets = new Insets(0, 0, 5, 0);
		gbc_btnTestCarbon.gridx = 2;
		gbc_btnTestCarbon.gridy = 3;
		pane.add(btnTestCarbon, gbc_btnTestCarbon);
		GridBagConstraints gbc_3 = new GridBagConstraints();
		gbc_3.anchor = GridBagConstraints.EAST;
		gbc_3.fill = GridBagConstraints.VERTICAL;
		gbc_3.insets = new Insets(0, 0, 5, 5);
		gbc_3.gridx = 0;
		gbc_3.gridy = 4;
		JLabel label_4 = new JLabel("Smoke Level : ");
		pane.add(label_4, gbc_3);
		textFieldSmoke.setEditable(false);
		GridBagConstraints gbc_textFieldSmoke = new GridBagConstraints();
		gbc_textFieldSmoke.fill = GridBagConstraints.BOTH;
		gbc_textFieldSmoke.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldSmoke.gridx = 1;
		gbc_textFieldSmoke.gridy = 4;
		pane.add(textFieldSmoke, gbc_textFieldSmoke);

		GridBagConstraints gbc_btnTestSmoke = new GridBagConstraints();
		gbc_btnTestSmoke.insets = new Insets(0, 0, 5, 0);
		gbc_btnTestSmoke.gridx = 2;
		gbc_btnTestSmoke.gridy = 4;
		pane.add(btnTestSmoke, gbc_btnTestSmoke);
		GridBagConstraints gbc_4 = new GridBagConstraints();
		gbc_4.anchor = GridBagConstraints.EAST;
		gbc_4.fill = GridBagConstraints.VERTICAL;
		gbc_4.insets = new Insets(0, 0, 5, 5);
		gbc_4.gridx = 0;
		gbc_4.gridy = 5;
		JLabel label_5 = new JLabel("Battery Level: ");
		pane.add(label_5, gbc_4);
		textFieldBattery.setEditable(false);
		GridBagConstraints gbc_textFieldBattery = new GridBagConstraints();
		gbc_textFieldBattery.fill = GridBagConstraints.BOTH;
		gbc_textFieldBattery.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldBattery.gridx = 1;
		gbc_textFieldBattery.gridy = 5;
		pane.add(textFieldBattery, gbc_textFieldBattery);

		GridBagConstraints gbc_btnTestBattery = new GridBagConstraints();
		gbc_btnTestBattery.insets = new Insets(0, 0, 5, 0);
		gbc_btnTestBattery.gridx = 2;
		gbc_btnTestBattery.gridy = 5;
		pane.add(btnTestBattery, gbc_btnTestBattery);

		frame.pack();

	}

	/**
	 * Prompt for and return the IP address of the server.
	 */
	private String getServerAddress() {
		return JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:", "Sensor Started",
				JOptionPane.QUESTION_MESSAGE);
	}

	/**
	 * Prompt for and return the SensorID.
	 */
	private String inputSensorID() {
		return JOptionPane.showInputDialog(frame, "Input Sensor ID:", "Sensor ID", JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Prompt for and return the Server password.
	 */
	private String getPassword() {
		return JOptionPane.showInputDialog(frame, "Input server password:", "Server password",
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Connects to the server then enters the processing loop.
	 */
	private void run() throws IOException {

		// Make connection and initialize streams
		System.out.println("Starting Sensor");
		Thread sensor = initializeSensor(34, 8, 80, 45);
		sensor.start();
		String serverAddress = getServerAddress();
		Socket socket = new Socket(serverAddress, PORT);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);

		// Process all messages from server, according to the protocol.
		while (true) {

			String line = in.readLine();
			if (line.startsWith("SUBMITSENSORID")) {
				sensorUID = inputSensorID();
				out.println(sensorUID);
			} else if (line.startsWith("IDACCEPTED")) {
				textField.setText(sensorUID);
			} else if (line.startsWith("GETPASSWORD")) {
				out.println(getPassword());
			} else if (line.startsWith(sensorUID)) {

				System.out.println(line);

				out.println("QUICKUPDATE" + sensorUID + ">>" + temperature + ">>" + smoke + ">>" + battery + ">>" + co2
						+ ">>" + LocalDateTime.now().toString().substring(0, 19) + ">>");
				out.flush();
				System.out.println("QUICKUPDATE data sent");

			} else if (line.startsWith("DATAUPDATE")) {

				// send hour request data
				out.println("HourlyResponse " + sensorUID + ">>" + temperature + ">>" + smoke + ">>" + battery + ">>"
						+ co2 + ">>" + LocalDateTime.now().toString().substring(0, 19) + ">>");
				System.out.println("[" + LocalDateTime.now().toString().substring(0, 19) + "] DATAUPDATE sent");

			}

		}
	}

	public static Thread initializeSensor(double tempLvl, double smokeLvl, double batteryLvl, double carbonLvl) {

		temperature = tempLvl;
		smoke = smokeLvl;
		battery = batteryLvl;
		co2 = carbonLvl;

		Thread sensorThread = new Thread() {
			@Override
			public void run() {
				Random r = new Random();
				for (;;) {

					// If readings are at a critical level, alert the server
					if (battery < 16) {
						System.out.println("Critical battery level");
						System.out.println("Warning conmmand send");
						out.println("WARNING" + sensorUID + ">>" + temperature + ">>" + smoke + ">>" + battery + ">>"
								+ co2 + ">>" + LocalDateTime.now().toString().substring(0, 19));

					} else if (temperature > 35) {
						System.out.println("Critical temperature");
						System.out.println("Warning conmmand send");
						out.println("WARNING" + sensorUID + ">>" + temperature + ">>" + smoke + ">>" + battery + ">>"
								+ co2 + ">>" + LocalDateTime.now().toString().substring(0, 19));

					} else if (smoke > 9) {
						System.out.println("Critical smoke level");
						System.out.println("Warning conmmand send");
						out.println("WARNING" + sensorUID + ">>" + temperature + ">>" + smoke + ">>" + battery + ">>"
								+ co2 + ">>" + LocalDateTime.now().toString().substring(0, 19));

					} else if (co2 > 49) {

						System.out.println("Critical CO2 level");
						System.out.println("Warning conmmand send");
						out.println("WARNING" + sensorUID + ">>" + temperature + ">>" + smoke + ">>" + battery + ">>"
								+ co2 + ">>" + LocalDateTime.now().toString().substring(0, 19));

					} else {

					}
					try {

						// set thread to sleep for 5 minutes
						// int duration = 1000 * 60 * 5;
						// TIMEOUT constant can be changed to change the number of seconds to sleep
						int duration = 1000 * TIMEOUT;
						textFieldTemperature.setText(temperature + "C");
						textFieldcO2.setText(co2 + "");
						textFieldSmoke.setText(smoke + "");
						textFieldBattery.setText(battery + "%");
						Thread.sleep(duration);

					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}

					// randomly generating each sensor's readings
					int randtemp = r.nextInt();
					if (randtemp < 0) {
						temperature += 0.5;
					} else {
						temperature -= 0.5;
					}

					int randSmoke = r.nextInt();
					if (randSmoke < 0) {
						smoke += 0.5;
					} else {
						smoke -= 0.5;
					}

					int randBattery = r.nextInt();
					if (randBattery < 0) {
						if (battery != 0) {
							battery -= 0.5;
						} else {
							battery = 100; // cycle battery level for testing purposes
						}

					}

					int randCarbon = r.nextInt();
					if (randCarbon < 0) {
						co2 += 0.5;
					} else {
						co2 -= 0.5;
					}
				}
			}
		};
		System.out.println("Sensor started");
		return sensorThread;
	}

	/**
	 * Runs the client as an application with a closeable frame.
	 */
	public static void main(String[] args) throws Exception {
		Sensor client = new Sensor();
		client.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run();

	}
}
