package org.topq.jsystem.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * 
 * @author Bortman Limor
 * 
 */
public class ConfUtil {

	private Map<String, String> confMap = null;
	private static ConfUtil confUtil = null;
	private static final String TAG = "ConfUtil";
	private static final String CONFIG_FILE = "/data/conf.txt";

	// %%%%%%%%%%%%%%%%%%%%%%%%%%% PREVIOUS IMPL - DO NOT DELETE PLEASE -- ERAN !
	// private ConfUtil() throws IOException {
	//
	// confMap = new HashMap<String, String>();
	// InputStream in = null;
	// try {
	// // init all the configuration
	// File config = new File("/data/conf.txt");
	// if (config.exists()) {
	// in = new FileInputStream(config);
	//
	// InputStreamReader input = new InputStreamReader(in);
	// BufferedReader buffer = new BufferedReader(input);
	// String line = " ";
	//
	// while ((line = buffer.readLine()) != null) {
	// confMap.put(line.split("=")[0].trim(), line.split("=")[1].trim());
	// }
	//
	// } else {
	// confMap.put("LAUNCHER_ACTIVITY_FULL_CLASSNAME", "com.gettaxi.driverbox.activity.DriverIdentificationActivity");
	// confMap.put("TARGET_PACKAGE_ID", "com.gettaxi");
	// }
	// } catch (IOException e) {
	// Log.e(TAG, "Failed to open file:/data/conf.txt" + e.getMessage());
	// e.printStackTrace();
	// } finally {
	// if (null != in) {
	// in.close();
	// }
	// }
	//
	// }

	/**
	 * Search for config file from the following properties: <br>
	 * LAUNCHER_ACTIVITY_FULL_CLASSNAME <br>
	 * TARGET_PACKAGE_ID <br>
	 * <br>
	 * For example: <br>
	 * LAUNCHER_ACTIVITY_FULL_CLASSNAME = com.gettaxi.android.activities.login.LoadingActivity <br>
	 * TARGET_PACKAGE_ID = com.gettaxi <br>
	 * 
	 * 
	 * 
	 * 
	 * @throws IOException
	 */
	private ConfUtil() throws IOException {

		confMap = new HashMap<String, String>();
		InputStream in = null;
		BufferedReader buffer = null;
		try {
			// init all the configuration
			File config = new File(CONFIG_FILE);
			if (!config.exists()) {
				String error = "No config file found: " + CONFIG_FILE;
				Log.e(TAG, error);
				throw new IOException(error);
			}
			in = new FileInputStream(config);

			InputStreamReader input = new InputStreamReader(in);
			buffer = new BufferedReader(input);
			String line = " ";

			while ((line = buffer.readLine()) != null) {
				confMap.put(line.split("=")[0].trim(), line.split("=")[1].trim());
			}

		} finally {
			if (null != in) {
				in.close();
			}
			if (null != buffer) {
				buffer.close();
			}
		}

	}

	public static ConfUtil getInstance() throws IOException {
		if (confUtil == null) {
			try {
				confUtil = new ConfUtil();
			} catch (IOException e) {
				Log.e(TAG, "Failed to Create confUtil  " + e.getMessage());
				throw e;
			}
		}
		return confUtil;
	}

	public String getConfigParameters(String paramName) {
		return confMap.get(paramName);
	}

}
