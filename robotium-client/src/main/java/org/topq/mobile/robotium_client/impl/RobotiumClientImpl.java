package org.topq.mobile.robotium_client.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.topq.mobile.common_mobile.client.interfaces.MobileClintInterface;
import org.topq.mobile.core.AdbController;
import org.topq.mobile.core.GeneralEnums;
import org.topq.mobile.robotium_client.infrastructure.AdbTcpClient;

import com.android.ddmlib.IDevice;


<<<<<<< HEAD:robotium-client/src/main/java/org/topq/mobile/robotium_client/impl/RobotuimClientImpel.java
public class RobotuimClientImpel implements MobileClintInterface{
=======
public class RobotiumClientImpl implements RobotiumClient{
>>>>>>> 2b301e0b4e9f6510a4025c510af65cc1c947ea9d:robotium-client/src/main/java/org/topq/mobile/robotium_client/impl/RobotiumClientImpl.java
	
	
	private AdbTcpClient tcpClient;
	private static AdbController adb;
	private static Logger logger= null;
	private static boolean getScreenshots = false;
	private static int port = 6100;
	private static String deviceSerial;
	private static String apkLocation = null;
	private static String pakageName = null;
	private static String testClassName = null;
	private static String host = null;
	private static String testName = null;
	
	public RobotiumClientImpl(String configFile,boolean doDeply) throws Exception{
		logger = Logger.getLogger(RobotiumClientImpl.class);
		File file = new File(configFile);
		if(file.exists()){
		Properties pro = new Properties();
		InputStream in = new FileInputStream(file);
		pro.load(in);
		String temeroryProrp = pro.getProperty("Port");
		logger.debug("In Properties file port is:"+temeroryProrp);
		port = Integer.parseInt(temeroryProrp);
		temeroryProrp = pro.getProperty("DeviceSerail");
		logger.debug("In Properties file DeviceSerial is:"+temeroryProrp);
		apkLocation = temeroryProrp;
		temeroryProrp = pro.getProperty("ApkLocation");
		logger.debug("APK location is:"+apkLocation);
		pakageName = pro.getProperty("PakageName");
		logger.debug("Pakage Name is:"+pakageName);
		testClassName = pro.getProperty("TestClassName");
		logger.debug("Test Class Name is:"+testClassName);
		testName = pro.getProperty("TestName");
		logger.debug("Test  Name is:"+testName);
		host = pro.getProperty("Host");
		logger.debug("Host  Name is:"+host);
		deviceSerial=pro.getProperty("DeviceSerail");
		adb = new AdbController(deviceSerial);
		String serverConfFileLocation = pro.getProperty("ServerConfFile");
		if(doDeply){
			adb.installAPK(serverConfFileLocation, temeroryProrp);
		}
		adb.runTestOnDevice(pakageName,testClassName,testName);
		logger.info("Start server on device");
		setPortForwarding();
		tcpClient = new AdbTcpClient(host, port);
		}else{
			Exception e= new Exception("Can't fiend the file:"+file.getAbsolutePath());
			logger.error("Can't fiend the file:"+file.getAbsolutePath());
			throw e;
		}
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
	public String sendData(final String data) throws Exception {
		logger.info("Sending command: " + data);
		String result = null;
		IDevice device = getDevice();
		logger.info("Send Data to " + device.getSerialNumber());

		try {
			result = tcpClient.sendData(data);
			int splitIndex = result.indexOf("{");
			if (splitIndex == -1) {
				logger.error("No data recieved from the device");
				return NO_DATA_STRING;
			}
			if (result.contains(ERROR_STRING)) {
				logger.error(result);
				adb.getScreenShots(getDevice());
			} else if (result.contains(SUCCESS_STRING)) {
				logger.info(result);
			}

		} catch (Exception e) {
			logger.error("Failed to send / receive data", e);
			throw e;
		} 
		if (getScreenshots) {
			adb.getScreenShots(getDevice());
		}
		return result;
	}


	public String launch() throws Exception {
		return sendData("{launch;}");
		
	}

	public String getTextView(int index) throws Exception {
		return sendData("{getTextView," + index + ";}");
	}

	public String getTextViewIndex(String text) throws Exception {
		String response = sendData("{getTextViewIndex," + text + ";}");
		return response;
	}

	public String getCurrentTextViews() throws Exception {
		return sendData("{getCurrentTextViews,a;}");
	}

	public String getText(int index) throws Exception {
		return sendData("{getText," + index + ";}");
	}

	public String clickOnMenuItem(String item) throws Exception {
		return sendData("{clickOnMenuItem," + item + ";}");
	}

	public String  clickOnView(int index) throws Exception {
		return sendData("{clickOnView," + index + ";}");
	}

	public String enterText(int index, String text) throws Exception {
		return sendData("{enterText," + index + "," + text + ";}");
	}

	public String clickOnButton(int index) throws Exception {
		return sendData("{clickOnButton," + index + ";}");
	}

	public String clickInList(int index) throws Exception {
		return sendData("{clickInList," + index + ";}");
	}

	public String clearEditText(int index) throws Exception {
		return sendData("{clearEditText," + index + ";}");
	}

	public String clickOnButtonWithText(String text) throws Exception {
		return sendData("{clickOnButtonWithText," + text + ";}");
	}

	public String clickOnText(String text) throws Exception {
		return sendData("{clickOnText," + text + ";}");
	}

	public String goBack() throws Exception {
		return sendData("{goBack,;}");
	}

	public String sendKey(int key) throws Exception {
		return sendData("{sendKey," + key + ";}");
	}

	public void closeConnection() throws Exception {
		sendData("{GodBay;}");
		tcpClient.closeConnection();

	}
	
	public AdbController getAdb() {
		return adb;
	}

	public void setAdb(AdbController adb) {
		this.adb = adb;
	}

	private void setPortForwarding() throws Exception {
		IDevice device = getDevice();
		if (device.getState() == IDevice.DeviceState.ONLINE){
			device.createForward(port, GeneralEnums.SERVERPORT);
		}else{
			Exception e = new Exception("Unable to perform port forwarding - " + deviceSerial + " is not online");
			logger.error(e);
			throw e;
		}
	}
	
	private IDevice getDevice() throws Exception {
		IDevice device = adb.getDevice(deviceSerial);
		if (null == device) {
			Exception e = new Exception("Unable to find device with serial number: " + deviceSerial);
			logger.error(e);
			throw e;
		}
		return device;
	}

}
