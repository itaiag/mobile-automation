package org.jsystemtest.mobile.core.device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jsystemtest.mobile.core.ConnectionException;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;

public class USBDevice extends AbstractAndroidDevice {

	private final static Logger logger = Logger.getLogger(USBDevice.class);

	public USBDevice(AndroidDebugBridge adb, IDevice device) throws Exception {
		super(adb, device);
	}

	@Override
	public void connect() throws ConnectionException {
		// Done automatically via IDeviceChangeListener

	}

	@Override
	public void disconnect() {
		// Done automatically via IDeviceChangeListener

	}

	/**
	 * Install APK on device
	 * 
	 * @param apkLocation
	 * @throws InstallException
	 */
	public void installPackage(String apkLocation, boolean reinstall) throws InstallException {
		final String result = device.installPackage(apkLocation, reinstall);
		if (result != null) {
			throw new InstallException("Failed to install: " + result, null);
		}

	}

	/**
	 * Limor B
	 * 
	 * Execute test on device.
	 * 
	 * @param pakageName
	 * @param testClassName
	 * @param testName
	 * @throws IOException,Exception
	 */
	public void runTestOnDevice(String pakageName, String testClassName, String testName) throws Exception {
		runAdbCommand("-e class " + pakageName + "." + testClassName + "#" + testName + " "
				+ pakageName + "/android.test.InstrumentationTestRunner",null);
	}
	
	public void  startServer(String pakageName,String launcherActivityClass) throws Exception{
		Map<String, String> parms = new HashMap<String, String>();
		parms.put("launcherActivityClass", launcherActivityClass);
		runAdbCommand(pakageName + "/"+pakageName+".RobotiumServerInstrumentation",parms);
		
	}

	private void runAdbCommand(String commandPrfix,Map<String,String> params) throws IOException, Exception {
		if (null == adbLocation || !adbLocation.exists()) {
			throw new IOException("Can't find adb location");
		}
		StringBuilder cmd = new StringBuilder(adbLocation.getAbsolutePath() + "\\adb -s " + device.getSerialNumber()+" shell am instrument ");
		if(params != null){
			cmd.append(" -e");
			for(String name : params.keySet()){
				cmd.append(" "+name+" "+params.get(name));
			}
		}
		cmd.append(" "+commandPrfix);
		logger.info("Try to run command:" + cmd);
		Runtime run = Runtime.getRuntime();
		Process pr = run.exec(cmd.toString());
		try {
			pr.waitFor();
			Thread.sleep(TimeUnit.SECONDS.toMillis(2));
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String s;
			StringBuilder  allBuffer = new StringBuilder(); 
			while ((s = stdInput.readLine()) != null) {
				allBuffer.append(s);
			} 
			if(allBuffer.indexOf("Exception")!=-1 || allBuffer.indexOf("Error")!=-1){
				Exception e = new Exception(allBuffer.toString());
				logger.error(e);
				throw e;
			}

		} catch (InterruptedException e) {
			logger.error(e);
		}
	}



}
