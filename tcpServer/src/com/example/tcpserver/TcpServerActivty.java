package com.example.tcpserver;

import org.json.JSONObject;


import com.example.tcpserver.IDataCallback;


import android.os.Bundle;
import android.os.Looper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.Menu;

public class TcpServerActivty extends Activity {
	private static final String TAG = "TcpServerActivituy";
	private Thread serverThread;
	private static boolean continueRunning = true;


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	Log.i(TAG, "in on create");
    	if(savedInstanceState == null){
    		savedInstanceState = new Bundle();
    	}
    	savedInstanceState.putString("launcherActivityClass", "com.gettaxi.android.activities.login.LoadingActivity");
		startInstrumentation(new ComponentName("org.topq.jsystem.mobile", "org.topq.jsystem.mobile.RobotiumServerInstrumentation"), null, savedInstanceState);
		startServer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tcp_server, menu);
        return true;
    }
    public void startServer()  {	
		Log.d(TAG, "Start server");	
		TcpServer server = new TcpServer();
		Log.i(TAG, "About to launch server");
		serverThread = new Thread(server);
		serverThread.start();
		Log.i(TAG, "Server is up");
		try {
			finish();
		while (continueRunning && server.isRunning()) {
				Thread.sleep(1000);
		}
		} catch (InterruptedException e) {
			Log.e(TAG,"InterruptedException" ,e);
		}
		Log.i(TAG, "Server is down");
	}
	
	
}
