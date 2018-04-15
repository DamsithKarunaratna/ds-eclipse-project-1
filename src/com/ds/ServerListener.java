package com.ds;

import java.rmi.Remote;
import java.util.ArrayList;

public interface ServerListener extends Remote {

	public void LostConnection(String sensorUID) throws java.rmi.RemoteException;

	public void closeNotify() throws java.rmi.RemoteException;

	public void update() throws java.rmi.RemoteException;

	public void showWarningMessage(String message) throws java.rmi.RemoteException;
	
	public void showMessage(String message) throws java.rmi.RemoteException;

	public void specificSensorResponse(String sensorResponse[]) throws java.rmi.RemoteException;

	public void noResponse(String sensorUID) throws java.rmi.RemoteException;

}
