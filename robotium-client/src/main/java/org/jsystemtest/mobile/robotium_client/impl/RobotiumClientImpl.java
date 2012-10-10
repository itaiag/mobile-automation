package org.jsystemtest.mobile.robotium_client.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import net.iharder.Base64;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsystemtest.mobile.common_mobile.client.enums.HardwareButtons;
import org.jsystemtest.mobile.common_mobile.client.interfaces.MobileClintInterface;
import org.jsystemtest.mobile.core.AdbController;
import org.jsystemtest.mobile.core.ConnectionException;
import org.jsystemtest.mobile.core.GeneralEnums;
import org.jsystemtest.mobile.core.device.AbstractAndroidDevice;
import org.jsystemtest.mobile.core.device.USBDevice;
import org.jsystemtest.mobile.robotium_client.infrastructure.TcpClient;

import com.android.ddmlib.InstallException;

public class RobotiumClientImpl implements MobileClintInterface {

	private static Logger logger = Logger.getLogger(RobotiumClientImpl.class);;

	private static final String SERVER_PACKAGE_NAME = "org.topq.jsystem.mobile";
	private static final String SERVER_CLASS_NAME = "RobotiumServer";
	private static final String SERVER_TEST_NAME = "testMain";
	private static final String RESULT_STRING = "RESULT";
	private static final String CONFIG_FILE = "/data/conf.txt";

	private TcpClient tcpClient;
	private USBDevice device;
	private static boolean getScreenshots = false;
	private static int port = 4321;
	private static String deviceSerial;
	private static String apkLocation = null;
	private static String host = "localhost";

	public RobotiumClientImpl(String configFileName) throws Exception {
		this(configFileName, true);
	}

	public RobotiumClientImpl(String configFileName, boolean deployServer) throws Exception {
		this(configFileName, deployServer, true);
	}

	public RobotiumClientImpl(Properties configProperties, boolean deployServer, boolean launchServer)
			throws InstallException, Exception {
		readConfigFile(configProperties);
		launchClient();
		launchServer(deployServer, launchServer, configProperties);
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
			if (in != null){
				in.close();
			}
			
		}

		readConfigFile(configProperties);
		launchClient();
		launchServer(deployServer, launchServer, configProperties);

	}

	private void launchClient() throws ConnectionException, Exception {
		device = AdbController.getInstance().waitForDeviceToConnect(deviceSerial);
		setPortForwarding();
		tcpClient = new TcpClient(host, port);
	}

	private void launchServer(boolean deployServer, boolean launchServer, Properties configProperties)
			throws InstallException, Exception {
		if (deployServer) {
			logger.info("About to deploy server on device");
			device.installPackage(apkLocation, true);
			String serverConfFile = configProperties.getProperty("ServerConfFile");
			logger.debug("Server Conf File:" + serverConfFile);
			device.pushFileToDevice(CONFIG_FILE, serverConfFile);
		}
		if (launchServer) {
			logger.info("About to launch server on device");
			device.runTestOnDevice(SERVER_PACKAGE_NAME, SERVER_CLASS_NAME, SERVER_TEST_NAME);
		}
	}

	/**
	 * Read all the details from the given properties and populate the object
	 * members.
	 * 
	 * @param configProperties
	 */
	private void readConfigFile(final Properties configProperties) {
		if (isPropertyExist(configProperties, "Port")) {
			port = Integer.parseInt(configProperties.getProperty("Port"));
		}
		logger.debug("Port is set to" + port);

		if (!isPropertyExist(configProperties, "DeviceSerail")) {
			throw new IllegalStateException("Device serial is not specify in config file");
		}
		deviceSerial = configProperties.getProperty("DeviceSerail");

		logger.debug("Device serial is set to" + deviceSerial);

		if (isPropertyExist(configProperties, "ApkLocation")) {
			apkLocation = configProperties.getProperty("ApkLocation");
		}
		logger.debug("APK location is set to:" + apkLocation);

		if (isPropertyExist(configProperties, "Host")) {
			host = configProperties.getProperty("Host");
		}
		logger.debug("Host is set to" + host);
	}

	/**
	 * Check if the property with the specified key exists in the specified
	 * properties object.
	 * 
	 * @param configProperties
	 * @param key
	 * @return true if and only if the property with the specified key exists.
	 */
	private boolean isPropertyExist(Properties configProperties, String key) {
		final String value = configProperties.getProperty(key);
		return value != null && !value.isEmpty();
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
			if (tcpClient.sendData(jsonobj) == null) {
				throw new Exception("No data recvied from server! pleas check server log!");
			}
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
