package org.jsystemtest.mobile.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.SyncService;

public abstract class AbstractAndroidDevice {
	private final static Logger logger = Logger.getLogger(AbstractAndroidDevice.class);
	
	protected final AndroidDebugBridge adb;
	protected final IDevice device;

	// Variables that we get from the AdbController
	protected final File adbLocation;
	

	public AbstractAndroidDevice(AndroidDebugBridge adb, IDevice device) throws Exception {
		super();
		this.adb = adb;
		this.device = device;

		// Get Data from the AdbController
		try {
			adbLocation = AdbController.getInstance().getAdbLocation();
		} catch (Exception e) {
			throw new Exception("Adb location was not set");
		}

		
	}

	/**
	 * Set port forwarding for the requested device
	 * 
	 * @param deviceSerial
	 * @param localPort
	 * @param remotePort
	 * @throws Exception
	 */
	public void setPortForwarding(int localPort, int remotePort) throws Exception {
		if (device.getState() == IDevice.DeviceState.ONLINE) {
			device.createForward(localPort, remotePort);
		} else {
			Exception e = new Exception("Unable to perform port forwarding - " + device.getSerialNumber() + " is not online");
			logger.error(e);
			throw e;
		}
	}

	/**
	 * Captures device screenshot
	 * 
	 * 
	 * @param screenshotFile
	 *            - File on which to write the screenshot data. If null is
	 *            specified, the content will be written to temporary file.
	 * @return The screenshot file
	 * @throws Exception
	 */
	public File getScreenshot(File screenshotFile) throws Exception {
		logger.info("Screen Shot " + device.getSerialNumber());
		RawImage ri = device.getScreenshot();
		return display(device.getSerialNumber(), ri, screenshotFile);
	}

	private static File display(String device, RawImage rawImage, File screenshotFile) throws Exception {
		BufferedImage image = new BufferedImage(rawImage.width, rawImage.height, BufferedImage.TYPE_INT_RGB);
		// Dimension size = new Dimension(image.getWidth(), image.getHeight());

		int index = 0;
		int indexInc = rawImage.bpp >> 3;
		for (int y = 0; y < rawImage.height; y++) {
			for (int x = 0; x < rawImage.width; x++, index += indexInc) {
				int value = rawImage.getARGB(index);
				image.setRGB(x, y, value);
			}
		}
		if (screenshotFile == null) {
			screenshotFile = File.createTempFile("screenshot", ".png");

		}
		ImageIO.write(image, "png", screenshotFile);
		logger.info("ScreenShot can be found in:" + screenshotFile.getAbsolutePath());
		return screenshotFile;
	}


	/**
	 * Grab file from the device
	 * 
	 * @param deviceName
	 * @param fileLocation
	 *            file location on the device
	 * @param fileName
	 *            file name on the device
	 * @throws Exception
	 */
	public void getFile(String fileLocation, String fileName, String localLocation) throws Exception {
		String devStr = device.getSerialNumber();
		if (device.getSerialNumber().indexOf(":") != -1)
			devStr = device.getSerialNumber().substring(0, device.getSerialNumber().indexOf(":"));
		while (devStr.contains("."))
			devStr = devStr.replace(".", "");

		try {
			File local = new File(localLocation.substring(0, localLocation.lastIndexOf(fileName) - 1));
			if (!local.exists())
				local.mkdirs();
			device.getSyncService().pullFile(fileLocation + "/" + fileName, localLocation,
					SyncService.getNullProgressMonitor());
			// ReporterHelper.copyFileToReporterAndAddLink(report, new
			// File(localLocation), devStr + "_" + fileName);
			// FileUtils.deleteFile(localLocation);
		} catch (Exception e) {
			logger.error("Exception ", e);
			throw e;
		}
	}

	public abstract void connect() throws ConnectionException;
	
	public abstract void disconnect();
	
	public abstract void runTestOnDevice(String pakageName, String testClassName, String testName) throws IOException;
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Serial Number:").append(device.getSerialNumber()).append("\n");
		return sb.toString();
	}
	

	/**
	 * Push file to the device
	 * 
	 * @param deviceName
	 * @param fileLocation
	 *            file location on the device
	 * @param fileName
	 *            file name on the device
	 * @throws Exception
	 */
	public void pushFileToDevice(String fileLocation, String fileName, String localLocation) throws Exception {
		try {
			device.getSyncService().pushFile(localLocation, fileLocation + "/" + fileName,
					SyncService.getNullProgressMonitor());
		} catch (Exception e) {
			logger.error("Exception ", e);
			throw e;
		}
	}

	/**
	 * Install APK on device
	 * 
	 * @param apkLocation
	 * @throws InstallException
	 */
	public abstract void installPackage(String apkLocation, boolean reinstall) throws InstallException;


	public boolean isOnline() {
		return device.isOnline();
	}

	public boolean isOffline() {
		return device.isOffline();
	}
	
	public String getSerialNumber(){
		return device.getSerialNumber();
	}
}
