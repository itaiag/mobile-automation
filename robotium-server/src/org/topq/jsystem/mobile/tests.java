package org.topq.jsystem.mobile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jayway.android.robotium.solo.Solo;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class tests extends ActivityInstrumentationTestCase2 {
	private static final String TAG = "ANDROID CLIENT";

	private static final String TARGET_PACKAGE_ID = "com.gettaxi";
	private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "com.gettaxi.android.activities.login.LoadingActivity";
	private static Class launcherActivityClass;
	
	static {

		try {
			launcherActivityClass = Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public tests() throws ClassNotFoundException {
		super(TARGET_PACKAGE_ID, launcherActivityClass);
	}

	private Solo solo;

	@Override
	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testLogin() throws IOException {

		File file = new File("/data/app.txt");
		InputStream in = new FileInputStream(file);
		InputStreamReader input = new InputStreamReader(in);
		BufferedReader buffreader = new BufferedReader(input);

		String line = " ";
		String res = "";
		while ((line = buffreader.readLine()) != null) {
			String[] command = line.split(";");
			if (0 == command[0].compareTo("enterText")) {
				solo.enterText(Integer.parseInt(command[1]), command[2]);
			} else if (0 == command[0].compareTo("clickOnButton")) {
				solo.clickOnButton(command[1]);
			}
		}
		in.close();

	}

	public void testConnection() {
		Log.d(TAG, "About to create connection");
		TcpServer server = new TcpServer();
		Thread t = new Thread(server);
		t.start();
		Log.d(TAG, "Connection is up");
	}

}
