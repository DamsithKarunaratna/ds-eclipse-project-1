package com.ds;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class Monitor1 extends UnicastRemoteObject implements ServerListener, Runnable {

	private static ArrayList<String> Data = new ArrayList<String>();
	private int count = 0;
	private String eachData[];
	private static String monitorUID;
	private static IServer server;
	private static Monitor monitor;
	public static JFrame frame = new JFrame("Monitor");
	DefaultTableModel model = new DefaultTableModel();
	JTextArea messageArea = new JTextArea(20, 35);
	JScrollPane scrollP = new JScrollPane();
	JScrollPane scrollP2 = new JScrollPane();
	public static JLabel jSC = new JLabel("Number of Sensors");
	// public static JLabel note = new JLabel("Get realtime Data by Selecting each
	// Row in Table");
	String[] SenMonNum;
	public static Timer time = new Timer();

	// public static WeatherMonitor moni;
	public Monitor1() throws RemoteException {

		System.out.println("Loading Monitor");
		frame.setSize(700, 500);
		frame.setResizable(false);
		frame.setLayout(new GridLayout(0, 1));
		JPanel pane = new JPanel(new GridLayout(0, 1));
		JPanel pane2 = new JPanel(new GridLayout(0, 1));
		JTable table = new JTable(model);
		// table.setFocusable(false);
		table.setRowSelectionAllowed(true);
		messageArea.setForeground(Color.blue);
		scrollP.setViewportView(table);
		pane.add(scrollP);
		scrollP2.add(messageArea);
		pane.setBorder(new TitledBorder("Sensors Data"));
		frame.add(pane);
		messageArea.setBorder(new TitledBorder("Notifications :"));
		scrollP2.setViewportView(messageArea);
		pane2.add(jSC);
		pane2.add(scrollP2);
		table.setAutoscrolls(true);
		table.setCellSelectionEnabled(true);
		pane2.add(jSC);
		messageArea.append("--Get realtime Data by Selecting each Row in Table-- \n");
		frame.add(pane2);

		DefaultTableModel model = (DefaultTableModel) table.getModel();
		String columnNames[] = new String[] { "sensorUID", "Temperature", "Smoke_Level", "Battery", "Humidity",
				"Last Update", "Status" };
		model.setColumnIdentifiers(columnNames);

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(frame, "Are you sure to close this window?", "Really Closing?",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					try {
						server.removeObserver(monitor);
						System.exit(0);
					} catch (RemoteException ex) {
						System.out.println(ex);
					}

				}
			}
		});
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("table select");
				try {
					String sensorIDentifier = table.getValueAt(table.getSelectedRow(), 0).toString();

					System.out.println(sensorIDentifier + "selected");
					server.getSensorData(sensorIDentifier, monitorUID);

				} catch (RemoteException ex) {
					System.out.println(ex);
				}

			}
		});

	}

	public static void main(String[] args) throws NotBoundException {

		try {
			monitor = new Monitor();
			Monitor.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			String registration = "//localhost/TemperatureServer";
			Remote remoteService = Naming.lookup(registration);
			server = (IServer) remoteService;
			monitorUID = server.addObserver(monitor);
			System.out.println("monitor initialized...");
			int num = server.getMonitorCount();
			jSC.setText("Number of Monitors : " + num);
			monitor.run();

		} catch (RemoteException ex) {
			System.out.println(ex);
		} catch (MalformedURLException ex) {
			System.out.println(ex);
		}

	}

	// this method notify that sensors communication lost
	@Override
	public void LostConnection(String sensorUID) throws java.rmi.RemoteException {
		// moni.LostConnection(locate);
		messageArea.append(sensorUID + " Sensor communication has been Lost \n");
		int num = server.getMonitorCount();
		jSC.setText("  Number of Monitors :" + num);

	}

	// update number of sensors and monitors
	@Override
	public void closeNotify() throws java.rmi.RemoteException {

		int num = server.getMonitorCount();
		jSC.setText("Number of Monitors :" + num);
		// messageArea.append(num + "\n");

	}

	// to notify hour update is not received
	@Override
	public void noResponse(String sensorUID) throws java.rmi.RemoteException {
		messageArea.append(sensorUID + " : last hourly update lost, Get realtime Data by selecting Row\n");
		messageArea.append("------------------------------------------------\n");
	}

	// to notify send to server monitor this close
	public void WindowCloseNotify() throws java.rmi.RemoteException {
		server.removeObserver(monitor);
	}

	// Update all sensors data to table
	@Override
	public void updateData() throws java.rmi.RemoteException {

		Data = server.getAllSensorData();
		model.getDataVector().removeAllElements();
		model.setRowCount(0);
		System.out.println(Data.size());

		for (int i = 0; i < Data.size(); i++) {

			if (Data.get(i).contains(">>")) {
				eachData = Data.get(i).split(">>");
				System.out.println(Data.get(i));
				model.addRow(new Object[] { eachData[0], eachData[1] + "C", eachData[2] + "psi", eachData[3] + "mm",
						eachData[4] + "%", eachData[5], eachData[6] });

				// check exceeded values and notify
				if (Double.parseDouble(eachData[3]) >= 20) {

					messageArea.append("Rainfall value exceeded:" + eachData[3] + "mm, may be rain falling in "
							+ eachData[0] + "\n");
					messageArea.append("------------------------------------------------\n");
				}
				if (Double.parseDouble(eachData[1]) > 35) {

					messageArea.append("Temperature level exceeded 35C :" + eachData[1] + "C in " + eachData[0] + "\n");
					messageArea.append("------------------------------------------------\n");

				} else if (Double.parseDouble(eachData[1]) < 20) {

					messageArea.append("Temperature level is Low :" + eachData[1] + "C in " + eachData[0] + "\n");
					messageArea.append("------------------------------------------------\n");
				}
			}

		}
		messageArea.append("Data Updated\n");

	}

	// request real time sensor data form a sensor
	@Override
	public void specificSensorResponse(String response[]) throws java.rmi.RemoteException {

		System.out.println("response 0 " + response[0]);
		System.out.println("response 1 " + response[1]);
		System.out.println("response 2 " + response[2]);
		System.out.println("response 3 " + response[3]);
		System.out.println("response 4 " + response[4]);
		System.out.println("response 5 " + response[5]);
		System.out.println("response 6 " + response[6]);

	}

	@Override
	public void run() {

		System.out.println("Monitor started");
		try {
//			updateData();
			closeNotify();
		} catch (RemoteException ex) {
			System.out.println(ex);
		}
	}

	@Override
	public void showWarningMessage(String message) throws RemoteException {
		JOptionPane.showMessageDialog(frame, "WARNING " + message);

	}
}
