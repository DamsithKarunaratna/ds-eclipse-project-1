package com.ds;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JTable;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.net.MalformedURLException;

import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class Monitor extends UnicastRemoteObject implements Runnable, ServerListener {

	public static JFrame frame = new JFrame("Monitor");
	private static JTable sensorTable;
	private static ArrayList<String> Data = new ArrayList<String>();
	private static JTextArea messageArea = new JTextArea();
	private int count = 0;
	private String eachData[];
	private static String monitorUID;
	private static IServer server;
	private static Monitor monitor;

//	private static DefaultTableModel model = (DefaultTableModel) sensorTable.getModel();
	static String columnNames[] = new String[] { "sensorUID", "Temperature", "Smoke_Level", "Battery", "Humidity",
			"Last Update", "Status" };
	private static DefaultTableModel model = new DefaultTableModel(columnNames, 0);
	private final JScrollPane scrollPane = new JScrollPane();
	private final JScrollPane scrollPane_1 = new JScrollPane();

	public Monitor() throws RemoteException {
		frame.setSize(700, 500);
		frame.setResizable(false);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 15, 134, 567, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 50, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JLabel lblMonitorId = new JLabel("Monitor ID :");
		GridBagConstraints gbc_lblMonitorId = new GridBagConstraints();
		gbc_lblMonitorId.anchor = GridBagConstraints.EAST;
		gbc_lblMonitorId.insets = new Insets(0, 0, 5, 5);
		gbc_lblMonitorId.gridx = 2;
		gbc_lblMonitorId.gridy = 0;
		frame.getContentPane().add(lblMonitorId, gbc_lblMonitorId);

		JLabel monitorIdVal = new JLabel("");
		GridBagConstraints gbc_monitorIdVal = new GridBagConstraints();
		gbc_monitorIdVal.insets = new Insets(0, 0, 5, 5);
		gbc_monitorIdVal.gridx = 4;
		gbc_monitorIdVal.gridy = 0;
		frame.getContentPane().add(monitorIdVal, gbc_monitorIdVal);
		
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 2;
		gbc_scrollPane.gridy = 1;
		frame.getContentPane().add(scrollPane, gbc_scrollPane);

		sensorTable = new JTable(model);
		scrollPane.setViewportView(sensorTable);
		sensorTable.setRowSelectionAllowed(true);
		sensorTable.setAutoscrolls(true);
		sensorTable.setCellSelectionEnabled(true);
		
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.gridwidth = 2;
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 2;
		gbc_scrollPane_1.gridy = 3;
		frame.getContentPane().add(scrollPane_1, gbc_scrollPane_1);
		scrollPane_1.setViewportView(messageArea);
	}

	@Override
	public void LostConnection(String sensorUID) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeNotify() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateData() throws java.rmi.RemoteException {

//		Data = server.getAllSensorData();
		
		((DefaultTableModel) sensorTable.getModel()).addRow(new Object[] { "one", "two" , "three" , "four",
				"five", "six", "seven" });
		
		
//		System.out.println(Data.size());

//		for (int i = 0; i < Data.size(); i++) {
//
//			if (Data.get(i).contains(">>")) {
//				eachData = Data.get(i).split(">>");
//				System.out.println(Data.get(i));
//				model.addRow(new Object[] { eachData[0], eachData[1] + "C", eachData[2] + "psi", eachData[3] + "mm",
//						eachData[4] + "%", eachData[5], eachData[6] });
//
//				// check exceeded values and notify
//				if (Double.parseDouble(eachData[3]) >= 20) {
//
//					messageArea.append("Rainfall value exceeded:" + eachData[3] + "mm, may be rain falling in "
//							+ eachData[0] + "\n");
//					messageArea.append("------------------------------------------------\n");
//				}
//				if (Double.parseDouble(eachData[1]) > 35) {
//
//					messageArea.append("Temperature level exceeded 35C :" + eachData[1] + "C in " + eachData[0] + "\n");
//					messageArea.append("------------------------------------------------\n");
//
//				} else if (Double.parseDouble(eachData[1]) < 20) {
//
//					messageArea.append("Temperature level is Low :" + eachData[1] + "C in " + eachData[0] + "\n");
//					messageArea.append("------------------------------------------------\n");
//				}
//			}

//		}
		messageArea.append("Data Updated\n");

	}


	@Override
	public void showWarningMessage(String message) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void specificSensorResponse(String[] sensorResponse) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void noResponse(String sensorUID) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Monitor running");

	}

	public static void main(String[] args) throws NotBoundException {

		try {
			monitor = new Monitor();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
//			String registration = "//localhost/TemperatureServer";
//			Remote remoteService = Naming.lookup(registration);
//			server = (IServer) remoteService;
//			monitorUID = server.addObserver(monitor);
			System.out.println("monitor initialized...");
//			int num = server.getMonitorCount();
//			jSC.setText("Number of Monitors : " + num);
			monitor.run();

		} catch (RemoteException ex) {
			System.out.println(ex);
		} 

	}

}
