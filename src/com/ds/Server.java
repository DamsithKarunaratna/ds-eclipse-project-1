package com.ds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class Server extends UnicastRemoteObject implements Runnable, IServer {

	private static final int PORT = 9001;
	private static int currentMonitorUID = 0;
	// UPDATETIMEOUT : Interval in seconds before server requests updates from the
	// sensors, set to 10 seconds
	private static final int UPDATETIMEOUT = 10; // (example: 1 hour is 60 * 60)
	private static final String PASSWORD = "1234";
	private static ArrayList<ServerListener> monitors1 = new ArrayList<ServerListener>();
	private static HashMap<String, ServerListener> monitors = new HashMap<>();
	private static ArrayList<String> sensorResponseList = new ArrayList<String>();
	private static ArrayList<ISensorTracker> sensors = new ArrayList<ISensorTracker>();
	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	private static String response[];

	public Server() throws java.rmi.RemoteException {

	}

	private static void listenerBroadcast() {

	}

	// getter for sensor data
	@Override
	public ArrayList<String> getAllSensorData() throws java.rmi.RemoteException {
		// synchronize the ArrayList to lock it from updates
		synchronized (sensorResponseList) {
			return sensorResponseList;
		}
	}

	// getter for number of monitors connected to the server
	@Override
	public int getMonitorCount() throws java.rmi.RemoteException {
		System.out.println("Monitor count requested - " + monitors.size());
		return monitors.size();
	}

	// getter for number of sensors connected to the server
	@Override
	public int getSensorCount() throws RemoteException {
		System.out.println("sensor count requested - " + sensors.size());
		return sensors.size();
	}

	// method to add a new monitor to the list of connected monitors
	@Override
	public String addObserver(ServerListener listener) throws java.rmi.RemoteException {
		currentMonitorUID++;
		String monitorIdentifier = "monitor" + currentMonitorUID;
		monitors.put(monitorIdentifier, listener);
		System.out.println("adding listener -" + currentMonitorUID + " " + listener);
		listenerBroadcast();
		return monitorIdentifier;
	}

	// To remove monitor from list
	@Override
	public void removeObserver(ServerListener listener) throws java.rmi.RemoteException {
		System.out.println("removing listener -" + listener);
		monitors.remove(listener);
		listenerBroadcast();
	}

	// request data from a specific sensor
	@Override
	public void getSensorData(String sensorUID, String monitorUID) {

		for (PrintWriter writer : writers) {
			writer.println(sensorUID + ">>" + monitorUID);
			System.out.println("sent sensor specific request to -> (" + sensorUID + ")");
		}
	}

	@Override
	public void run() {
		// time.scheduleAtFixedRate(update, 5000, 1000 * 60 * 60);
		timer.scheduleAtFixedRate(task, 5000, 1000 * UPDATETIMEOUT); // 10 Second update request interval (testing)
	}

	Timer timer = new Timer();
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			// set "responseReceived" fields of all sensors to false.
			for (int j = 0; j < sensors.size(); j++) {
				sensors.get(j).setResponseReceived(false);
			}
			// requesting data from all sensors
			if (sensors.size() > 0) {
				for (PrintWriter writer : writers) {

					writer.println("DATAUPDATE");
					System.out.println("request send");

				}
			}
			// check if all sensors have replied. If not, send a message to monitor
			for (ISensorTracker sensor : sensors) {

				if (!sensor.isResponseReceived()) {
					System.out.println("no response from :" + sensor.getSensor());
					
					for (ServerListener monitor : monitors.values()) {

						try {
							monitor.noResponse(sensor.getSensor());
						} catch (RemoteException ex) {
							System.out.println(ex);
						}
					}
				}
			}

		}
	};

	// this method is use to quickly inform to monitors if there are any alert
	// private static void WarningUpdate() {
	// synchronized (list) {
	// System.out.println("update started");
	// for (Listeners EachMonitor : list) {
	// try {
	// System.out.println("Update");
	// EachMonitor.UpdateData();
	//
	// } catch (RemoteException ex) {
	// System.out.println(ex + "-WarningUpdate()");
	//
	// }
	// }
	// }
	// System.out.println("update end");
	// }

	// To notify that one monitor closed or a sensor
	// private static void CloseNotify() {
	//
	// for (Listeners EachMonitor : list) {
	// try {
	// System.out.println(EachMonitor);
	// if (EachMonitor != null)
	// EachMonitor.CloseNotify();
	// } catch (Exception e) {
	// System.out.println(e + "-CloseNotify()");
	// }
	// }
	// }

	public static void main(String[] args) {
		System.out.println("Loading Sensor Sever");
		try {
			Server server = new Server();
			String registry = "localhost";
			String registration = "rmi://" + registry + "/SensorServer";
			Naming.rebind(registration, server);
			ServerSocket listener = new ServerSocket(PORT);
			Thread serverThread = new Thread(server);
			serverThread.start();
			
			try {
				while (true) {
					new Server.Handler(listener.accept()).start();
				}
			} finally {
				listener.close();
			}

		} catch (RemoteException re) {
			System.err.println("[server error]:" + re);
		} catch (Exception e) {
			System.err.println("[server error]: " + e);
		}

	}

	/**
	 * handler thread class. Handlers are spawned from the Server's listening loop
	 * and are responsible for a dealing with a single sensor and handling its
	 * messages.
	 */
	private static class Handler extends Thread {

		private String sensorUID;
		private Socket socket;
		BufferedReader in;
		PrintWriter out;

		/**
		 * Sensor Tracker Class : This class is used to check if a particular sensor has
		 * not responded to the hourly update request
		 */
		private class SensorTracker implements ISensorTracker {

			public SensorTracker(String sensor) {
				super();
				this.sensor = sensor;
			}

			private String sensor;
			private boolean responseReceived;

			@Override
			public String getSensor() {
				return this.sensor;
			}

			@Override
			public boolean isResponseReceived() {
				return this.responseReceived;
			}

			@Override
			public void setResponseReceived(boolean responseReceived) {
				this.responseReceived = responseReceived;
			}

		}

		/**
		 * Constructs a handler thread, accepting the socket from the listener.accept()
		 * method.
		 */
		public Handler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {

				// create input and output streams for the socket
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				// Request an ID from this sensor. Keep requesting until
				// an ID that is not already used is submitted.
				while (true) {
					out.println("SUBMITSENSORID");
					sensorUID = in.readLine();
					if (sensorUID == null) {
						return;
					}
					synchronized (sensors) {
						if (sensors.isEmpty()) {

							ISensorTracker newSensor = new SensorTracker(sensorUID);
							newSensor.setResponseReceived(false); // TODO : check if necessary
							sensors.add(newSensor);
							System.out.println("sensor added");

						} else {

							for (ISensorTracker sensor : sensors) {

								if (sensor.getSensor() != sensorUID) {
									System.out.println("sensor added");
									ISensorTracker newSensor = new SensorTracker(sensorUID);
									newSensor.setResponseReceived(false);
									sensors.add(newSensor);
									break;
								}
							}
						}

						break;

					}
				}
				out.println("IDACCEPTED");

				// basic authentication, if password is incorrect, Server will keep sending
				// GETPASSWORD
				while (true) {
					out.println("GETPASSWORD");
					String passString = in.readLine();
					if (passString.equals(PASSWORD)) {
						break;
					}
				}

				writers.add(out);

				// first Data update from new sensor
				while (true) {
					out.println("DATAUPDATE");
					System.out.println("DATAUPDATE requested");
					String input = in.readLine();
					if (input.contains(">>")) {
						System.out.println(input.substring(15));
						sensorResponseList.add(input.substring(15));
						break;
					}
				}

				// replace newly update sensor data
				// CloseNotify();
				for (;;) {

					String input = in.readLine();
					if (input == null) {
						return;
					} else if (input.startsWith("HourlyResponse")) {
						// receive hourly updates
						System.out.println("HourlyResponse received ");
						if (input.contains(">>")) {
							input = input.substring(15);
							System.out.println(input);
							response = input.split(">>");

							if (sensorUID.endsWith(response[0])) {
								// System.out.println(newLocation+"/"+Dataparts[0]);
								int s = sensorResponseList.size();
								for (int i = 0; i < s; i++) {
									System.out.println("[" + LocalDateTime.now().toString().substring(0, 19)
											+ "] sensor - " + i + ": current data -> " + sensorResponseList.get(i));

									// update the dataSet of the relevant sensor
									if (sensorResponseList.get(i).startsWith(sensorUID)) {
										synchronized (sensorResponseList) {
											sensorResponseList.set(i, input);
											System.out.println("[" + LocalDateTime.now().toString().substring(0, 19)
													+ "] sensor - " + sensorUID + ": updated -> " + input);
										}
										synchronized (sensors) {
											for (ISensorTracker sensor : sensors) {
												if (sensor.getSensor() == sensorUID) {
													sensor.setResponseReceived(true);
												}
											}
										}
										break;
									}
								}
							}
							// System.out.println(checkHourupdate);
						}
					} else if (input.startsWith("QUICKUPDATE")) {
						// to catch forced updates
						System.out.println("QUICKUPDATE received ");
						if (input.contains(">>")) {
							input = input.substring(11);
							response = input.split(">>");
							System.out.println("input : " + input);

							if (sensorUID.equals(response[0])) {
								int s = sensorResponseList.size();
								for (int i = 0; i < s; i++) {
									System.out.println(i + "," + sensorResponseList.get(i));

									if (sensorResponseList.get(i).startsWith(sensorUID)) {
										synchronized (sensorResponseList) {
											sensorResponseList.set(i, input);
											System.out.println(input + " replaced");
										}
										break;
									}

								}

							}
						}
					}

				}
			} catch (IOException e) {
				System.out.println(e);
			} finally {

				if (sensorUID != null) {

					// remove sensor from the set of sensors
					for (int j = 0; j < sensors.size(); j++) {
						if (sensors.get(j).getSensor().startsWith(sensorUID)) {
							sensors.remove(sensors.get(j));
						}
					}
					// for (Listeners EachMonitor : list) {
					// try {
					// EachMonitor.LostConnection(newLocation);
					// } catch (Exception e) {
					// System.out.println(e);
					// }
					// }

					// remove sensor from Data set
					for (int i = 0; i < sensorResponseList.size(); i++) {
						if (sensorResponseList.get(i).startsWith(sensorUID)) {
							sensorResponseList.remove(i);
							System.out.println("Sensor removed");
							break;
						}
					}
				}
				if (out != null) {
					writers.remove(out);
				}
				try {
					System.out.println("socket closed");
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
