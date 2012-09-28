package org.jsystemtest.mobile.robotium_client.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import net.iharder.Base64;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsystemtest.mobile.core.AbstractAndroidDevice;
import org.jsystemtest.mobile.core.AdbController;
import org.jsystemtest.mobile.core.GeneralEnums;
import org.jsystemtest.mobile.core.USBDevice;
import org.jsystemtest.mobile.robotium_client.infrastructure.TcpClient;
import org.topq.mobile.common_mobile.client.enums.HardwareButtons;
import org.topq.mobile.common_mobile.client.interfaces.MobileClintInterface;

public class RobotiumClientImpl implements MobileClintInterface {

	private TcpClient tcpClient;
	private USBDevice device;
	private static Logger logger = Logger.getLogger(RobotiumClientImpl.class);;
	private static boolean getScreenshots = false;
	private static int port = 6100;
	private static String deviceSerial;
	private static String apkLocation = null;
	private static String pakageName = null;
	private static String testClassName = null;
	private static String host = null;
	private static String testName = null;
	private static final String RESULT_STRING = "RESULT";

	public RobotiumClientImpl(String configFileName) throws Exception {
		this(configFileName, true);
	}

	public RobotiumClientImpl(String configFileName, boolean deployServer) throws Exception {
		this(configFileName, deployServer, true);
	}

	public RobotiumClientImpl(String configFileName, boolean deployServer, boolean launchServer) throws Exception {
		final File configFile = new File(configFileName);
		if (!configFile.exists()) {
			throw new IOException("Configuration file was not found in " + configFileName);
		}

		Properties configProperties = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(configFile);
			configProperties.load(in);

		} finally {
			in.close();
		}

		port = Integer.parseInt(configProperties.getProperty("Port"));
		logger.debug("In Properties file port is:" + port);

		deviceSerial = configProperties.getProperty("DeviceSerail");
		logger.debug("In Properties file device serial is:" + deviceSerial);

		apkLocation = configProperties.getProperty("ApkLocation");
		logger.debug("APK location is:" + apkLocation);

		pakageName = configProperties.getProperty("PakageName");
		logger.debug("Pakage Name is:" + pakageName);

		testClassName = configProperties.getProperty("TestClassName");
		logger.debug("Test Class Name is:" + testClassName);

		testName = configProperties.getProperty("TestName");
		logger.debug("Test  Name is:" + testName);

		host = configProperties.getProperty("Host");
		logger.debug("Host  Name is:" + host);

		device = AdbController.getInstance().waitForDeviceToConnect(deviceSerial);
		if (deployServer) {
			device.installPackage(apkLocation, true);
			// TODO: Handle the configuration file via push
		}
		if (launchServer) {
			device.runTestOnDevice(pakageName, testClassName, testName);
		}
		logger.info("Start server on device");
		setPortForwarding();
		tcpClient = new TcpClient(host, port);
	}

	/**
	 * Send data using the TCP connection & wait for response Parse the response
	 * (make conversions if necessary - pixels to mms) and report
	 * 
	 * @param device
	 * @param data
	 *            serialised JSON object
	 * @throws Exception
	 */
	public String sendData(String command, String... params) throws Exception {
		String resultValue;
		try {
			JSONObject result = sendDataAndGetJSonObj(command, params);

			if (result.isNull(RESULT_STRING)) {
				logger.error("No data recieved from the device");
				return NO_DATA_STRING;
			}
			resultValue = (String) result.get(RESULT_STRING);
			if (resultValue.contains(ERROR_STRING)) {
				logger.error(result);
				device.getScreenshot(null);
			} else if (resultValue.contains(SUCCESS_STRING)) {
				logger.info(result);
			}

		} catch (Exception e) {
			logger.error("Failed to send / receive data", e);
			throw e;
		}
		if (getScreenshots) {
			device.getScreenshot(null);
		}
		return resultValue;
	}

	public JSONObject sendDataAndGetJSonObj(String command, String... params) throws Exception {
		JSONObject jsonobj = new JSONObject();
		jsonobj.put("Command", command);
		jsonobj.put("Params", params);
		logger.info("Sending command: " + jsonobj.toString());
		JSONObject result = null;
		logger.info("Send Data to " + device.getSerialNumber());

		try {
			result = new JSONObject(tcpClient.sendData(jsonobj));
		} catch (Exception e) {
			logger.error("Failed to send / receive data", e);
			throw e;
		}
		return result;
	}

	public String launch() throws Exception {
		return sendData("launch");
	}

	public String getTextView(int index) throws Exception {
		return sendData("getTextView", Integer.toString(index));
	}

	public String getTextViewIndex(String text) throws Exception {
		String response = sendData("getTextViewIndex", text);
		return response;
	}

	public String getCurrentTextViews() throws Exception {
		return sendData("getCurrentTextViews", "a");
	}

	public String getText(int index) throws Exception {
		return sendData("getText", Integer.toString(index));
	}

	public String clickOnMenuItem(String item) throws Exception {
		return sendData("clickOnMenuItem", item);
	}

	public String clickOnView(int index) throws Exception {
		return sendData("clickOnView", Integer.toString(index));

	}

	public String enterText(int index, String text) throws Exception {
		return sendData("enterText", Integer.toString(index), text);
	}

	public String clickOnButton(int index) throws Exception {
		return sendData("clickOnButton", Integer.toString(index));
	}

	public String clickInList(int index) throws Exception {
		return sendData("clickInList", Integer.toString(index));
	}

	public String clearEditText(int index) throws Exception {
		return sendData("clearEditText", Integer.toString(index));
	}

	public String clickOnButtonWithText(String text) throws Exception {
		return sendData("clickOnButtonWithText", text);
	}

	public String clickOnText(String text) throws Exception {
		return sendData("clickOnText", text);
	}

	public String sendKey(int key) throws Exception {
		return sendData("sendKey", Integer.toString(key));
	}

	public String clickOnHardwereButton(HardwareButtons button) throws Exception {
		return sendData("clickOnHardware", button.name());
	}

	public byte[] pull(String fileName) throws Exception {
		JSONObject jsonObj = sendDataAndGetJSonObj("pull", fileName);
		logger.info("command pull receved" + jsonObj);
		return ((jsonObj.getString("file"))).getBytes("UTF-16LE");
	}

	public String push(byte[] data, String newlocalFileName) throws Exception {
		String result = sendData("createFileInServer", newlocalFileName, Base64.encodeBytes(data, Base64.URL_SAFE),
				"true");
		return result;
	}

	public void closeConnection() throws Exception {
		sendData("exit");

	}

	private void setPortForwarding() throws Exception {
		device.setPortForwarding(port, GeneralEnums.SERVERPORT);
	}

	public AbstractAndroidDevice getDevice() throws Exception {
		return device;
	}

}
