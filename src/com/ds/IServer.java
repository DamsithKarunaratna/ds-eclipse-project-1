package com.ds;

import java.rmi.Remote;
import java.util.ArrayList;

public interface IServer extends Remote {

	public ArrayList<String> getAllSensorData() throws java.rmi.RemoteException;

	public int getMonitorCount() throws java.rmi.RemoteException;

	public int getSensorCount() throws java.rmi.RemoteException;

	public String addObserver(ServerListener listener) throws java.rmi.RemoteException;

	public void removeObserver(ServerListener listener) throws java.rmi.RemoteException;

	public void getSensorData(String sensorUID, String monitorUID) throws java.rmi.RemoteException;

}
