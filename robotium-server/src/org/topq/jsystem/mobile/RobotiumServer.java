package org.topq.jsystem.mobile;

import java.io.IOException;

import org.json.JSONObject;
import org.topq.jsystem.util.ConfUtil;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.jayway.android.robotium.solo.Solo;

public class RobotiumServer extends ActivityInstrumentationTestCase2 implements IDataCallback, ISoloProvider {

	private static final String TAG = "RobotiumServer";
	private static boolean continueRunning = true;
	private static Class launcherActivityClass;
	private static SoloExecutor executor = null;
	private static ConfUtil confUtil = null;

	static {

		try {
			confUtil = ConfUtil.getInstance();
			Log.i(TAG, "Class name to launch is:" + confUtil.getConfigParameters("LAUNCHER_ACTIVITY_FULL_CLASSNAME"));
			launcherActivityClass = Class.forName(confUtil.getConfigParameters("LAUNCHER_ACTIVITY_FULL_CLASSNAME"));
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "Failed to load class " + e.getMessage());
			System.exit(200);
		} catch (IOException e) {
			Log.e(TAG, "Failed to find config file: " + e.getMessage());
			System.exit(100);
		}
	}

	private Thread serverThread;

	public RobotiumServer() {
		super(confUtil.getConfigParameters("TARGET_PACKAGE_ID"), launcherActivityClass);
		Log.i(TAG, "Target package ID is:" + confUtil.getConfigParameters("TARGET_PACKAGE_ID"));
	}

	public void testMain() throws InterruptedException {
		TcpServer server = new TcpServer();
		server.addTestListener(this);
		Log.i(TAG, "About to launch server");
		serverThread = new Thread(server);
		serverThread.start();
		Log.i(TAG, "Server is up");
		while (continueRunning && server.isRunning()) {
			Thread.sleep(1000);
		}
		Log.i(TAG, "Server is down");
	}

	public void testNewTest() throws Exception {
		Log.i(TAG, "**************HERE***************");
		for (int i = 0; i < 10; i++) {
			Log.w(TAG, "ANOTHER MESSAGE");
			System.out.println("ANOTHER MESSAGE");
		}
	}

	public void testDebug() {
		Solo solo = getSolo();
		solo.clickInList(1);
		solo.clearEditText(0);
		solo.enterText(0, "1");
		solo.enterText(0, "1");
	}

	@Override
	public JSONObject dataReceived(String data) {
		Log.i(TAG, "Recieved data " + data);
		try {

			return getExecutor().execute(data);

		} catch (Exception e) {
			Log.e(TAG, "Failed to process data " + data, e);
			e.printStackTrace();
		}
		return new JSONObject();

	}

	@Override
	public Solo getSolo() {
		return new Solo(getInstrumentation(), getActivity());
	}

	private SoloExecutor getExecutor(){
		if(executor == null){
			executor = new SoloExecutor(this,getInstrumentation());
		}
		return executor;
	}
}
