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
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.border.LineBorder;

public class Monitor extends UnicastRemoteObject implements Runnable, ServerListener {

	public static JFrame frame = new JFrame("Monitor");
	private static JTable sensorTable;
	private static ArrayList<String> allSensorData = new ArrayList<String>();
	private static JTextArea messageArea = new JTextArea();
	private int count = 0;
	private String sensorData[];
	private static String monitorUID;
	private static IServer server;
	private static Monitor monitor;

	static String columnNames[] = new String[] { "sensorUID", "Temperature", "Smoke_Level", "Battery", "Humidity",
			"Last Update", "Status" };
	private static DefaultTableModel model = new DefaultTableModel(columnNames, 0);
	private final JScrollPane scrollPane = new JScrollPane();
	private final JScrollPane scrollPane_1 = new JScrollPane();
	private static JLabel lblMonitorIdValue = new JLabel("-");
	private final JPanel panel = new JPanel();
	private final JPanel panel_1 = new JPanel();
	private static JLabel lblSensorCount = new JLabel("Sensor Count : ");
	private static JLabel lblSensorCountval = new JLabel(" - ");
	private final JLabel lblNewLabel_1 = new JLabel("Connected Monitors : ");
	private static JLabel lblMonitorCountVal = new JLabel(" - ");
	private final JPanel infoPanel = new JPanel();

	public Monitor() throws RemoteException {
		frame.setSize(884, 500);
		frame.setResizable(false);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 183, 567, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 50, 193, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JLabel lblMonitorId = new JLabel("Monitor ID :");
		GridBagConstraints gbc_lblMonitorId = new GridBagConstraints();
		gbc_lblMonitorId.anchor = GridBagConstraints.EAST;
		gbc_lblMonitorId.insets = new Insets(0, 0, 5, 5);
		gbc_lblMonitorId.gridx = 0;
		gbc_lblMonitorId.gridy = 0;
		frame.getContentPane().add(lblMonitorId, gbc_lblMonitorId);

		GridBagConstraints gbc_lblMonitorIdValue = new GridBagConstraints();
		gbc_lblMonitorIdValue.anchor = GridBagConstraints.WEST;
		gbc_lblMonitorIdValue.insets = new Insets(0, 0, 5, 5);
		gbc_lblMonitorIdValue.gridx = 1;
		gbc_lblMonitorIdValue.gridy = 0;
		frame.getContentPane().add(lblMonitorIdValue, gbc_lblMonitorIdValue);

		JLabel monitorIdVal = new JLabel("");
		GridBagConstraints gbc_monitorIdVal = new GridBagConstraints();
		gbc_monitorIdVal.insets = new Insets(0, 0, 5, 0);
		gbc_monitorIdVal.gridx = 2;
		gbc_monitorIdVal.gridy = 0;
		frame.getContentPane().add(monitorIdVal, gbc_monitorIdVal);

		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.EAST;
		gbc_panel.fill = GridBagConstraints.VERTICAL;
		gbc_panel.gridwidth = 3;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "[sensors]", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		frame.getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 50, 66, 150, 639, 0 };
		gbl_panel.rowHeights = new int[] { 154, 16, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		sensorTable = new JTable(model);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 6;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		panel.add(scrollPane, gbc_scrollPane);
		scrollPane.setViewportView(sensorTable);
		sensorTable.setRowSelectionAllowed(true);
		sensorTable.setAutoscrolls(true);
		sensorTable.setCellSelectionEnabled(true);
		GridBagConstraints gbc_lblSensorCount = new GridBagConstraints();
		gbc_lblSensorCount.insets = new Insets(0, 0, 0, 5);
		gbc_lblSensorCount.gridx = 0;
		gbc_lblSensorCount.gridy = 1;
		panel.add(lblSensorCount, gbc_lblSensorCount);

		GridBagConstraints gbc_lblSensorCountval = new GridBagConstraints();
		gbc_lblSensorCountval.anchor = GridBagConstraints.WEST;
		gbc_lblSensorCountval.insets = new Insets(0, 0, 0, 5);
		gbc_lblSensorCountval.gridx = 1;
		gbc_lblSensorCountval.gridy = 1;
		panel.add(lblSensorCountval, gbc_lblSensorCountval);

		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_1.gridx = 2;
		gbc_lblNewLabel_1.gridy = 1;
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);

		GridBagConstraints gbc_lblMonitorCountVal = new GridBagConstraints();
		gbc_lblMonitorCountVal.anchor = GridBagConstraints.WEST;
		gbc_lblMonitorCountVal.insets = new Insets(0, 0, 0, 5);
		gbc_lblMonitorCountVal.gridx = 3;
		gbc_lblMonitorCountVal.gridy = 1;
		panel.add(lblMonitorCountVal, gbc_lblMonitorCountVal);
		
		GridBagConstraints gbc_infoPanel = new GridBagConstraints();
		gbc_infoPanel.insets = new Insets(0, 0, 0, 5);
		gbc_infoPanel.fill = GridBagConstraints.BOTH;
		gbc_infoPanel.gridx = 0;
		gbc_infoPanel.gridy = 2;
		infoPanel.setBorder(new TitledBorder(null, "[selected sensor]", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		frame.getContentPane().add(infoPanel, gbc_infoPanel);
		infoPanel.setLayout(new BorderLayout(0, 0));

		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.anchor = GridBagConstraints.EAST;
		gbc_panel_1.fill = GridBagConstraints.VERTICAL;
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 2;
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "[info]", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		frame.getContentPane().add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{634, 0};
		gbl_panel_1.rowHeights = new int[]{155, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 0;
		panel_1.add(scrollPane_1, gbc_scrollPane_1);
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
	public void update() throws java.rmi.RemoteException {

		allSensorData = server.getAllSensorData();
		((DefaultTableModel) sensorTable.getModel()).getDataVector().removeAllElements();
		((DefaultTableModel) sensorTable.getModel()).setRowCount(0);

		System.out.println(allSensorData.size());

		for (int i = 0; i < allSensorData.size(); i++) {

			if (allSensorData.get(i).contains(">>")) {

				sensorData = allSensorData.get(i).split(">>");
				((DefaultTableModel) sensorTable.getModel()).addRow(new Object[] { sensorData[0], sensorData[1],
						sensorData[2], sensorData[3], sensorData[4], sensorData[5], sensorData[6] });

			}

		}
		messageArea.append("Data Updated\n");

	}

	@Override
	public void showWarningMessage(String message) throws RemoteException {

		messageArea.append("[WARNING] : " + message + "\n");

	}

	@Override
	public void showMessage(String message) throws RemoteException {

		messageArea.append("[SERVERMESSAGE] : " + message + "\n");

	}

	@Override
	public void specificSensorResponse(String[] sensorResponse) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void noResponse(String sensorUID) throws RemoteException {

		messageArea.append("[NORESPONSE] : sensor - " + sensorUID + " has not responded \n");

	}

	@Override
	public void run() {

		try {
			lblMonitorCountVal.setText(Integer.toString(server.getMonitorCount()));
			lblSensorCountval.setText(Integer.toString(server.getSensorCount()));
			System.out.println("Monitor running");
		} catch (RemoteException e) {

			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws NotBoundException {

		try {
			monitor = new Monitor();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			String registration = "//localhost/SensorServer";
			Remote remoteService = Naming.lookup(registration);
			server = (IServer) remoteService;
			monitorUID = server.addObserver(monitor);
			lblMonitorIdValue.setText(monitorUID);

			System.out.println("monitor initialized...");
			monitor.run();

		} catch (RemoteException | MalformedURLException ex) {
			System.out.println(ex);
		}

	}

}
