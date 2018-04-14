package com.ds;

interface ISensorTracker {

	String getSensor();

	boolean isResponseReceived();

	void setResponseReceived(boolean responseReceived);

}