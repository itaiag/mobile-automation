package org.jsystemtest.mobile.core;

public class ConnectionException extends Exception {
	private static final long serialVersionUID = 1L;
	private final String deviceSerial;

	public ConnectionException(String deviceSerial) {
		super("Failed to connect to device with serial " + deviceSerial);
		this.deviceSerial = deviceSerial;

	}

	public String getDeviceSerial() {
		return deviceSerial;
	}

}
