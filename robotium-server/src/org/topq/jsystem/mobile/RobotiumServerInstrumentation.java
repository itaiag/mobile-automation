package org.topq.jsystem.mobile;

import java.io.IOException;
import java.io.Serializable;
import java.security.PrivilegedExceptionAction;

import org.json.JSONObject;
import org.topq.jsystem.util.ConfUtil;

import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.test.ActivityInstrumentationTestCase;
import android.util.Log;
/**
 * 
 * @author Bortman Limor
 *
 */
public class RobotiumServerInstrumentation extends Instrumentation implements IDataCallback, ISoloProvider {
	
	private static final String TAG = "RobotiumServerInstrumentation";
	private Activity myActive = null;
	private SoloExecutor executor = null;
	private Thread serverThread;
	private static boolean continueRunning = true;
	private static String launcherActivityClass;
	private Solo solo = null;
	
	@Override	public void onCreate(Bundle arguments) {	
		Log.d(TAG, "onCreate");	
		super.onCreate(arguments);
		if ( arguments != null ) {
			if ( arguments.containsKey ( "launcherActivityClass" ) ) {
				launcherActivityClass= arguments.getString ( "launcherActivityClass" );
				Log.d ( TAG, arguments.getString ( "launcherActivityClass" ) ); 
			} else {
				Log.e (TAG, "no launcherActivityClass here!" );
				System.exit(100);

			}

		}
		start();
	}
	@Override	public void onStart() {
		startServer();
	}
		
		public void startServer()  {	
			Log.d(TAG, "Start server");	
			TcpServer server = new TcpServer();
			server.addTestListener(this);
			Log.i(TAG, "About to launch server");
			serverThread = new Thread(server);
			serverThread.start();
			Log.i(TAG, "Server is up");
			try {
			while (continueRunning && server.isRunning()) {
					Thread.sleep(1000);
			}
			} catch (InterruptedException e) {
				Log.e(TAG,"InterruptedException" ,e);
			}
			Log.i(TAG, "Server is down");
		}

		void prepareLooper() {  
			Looper.prepare(); 
			}
		
		@Override	
		public Solo getSolo() {	
		if(myActive == null){
			Log.d(TAG, "Start app");
			Intent intent = new Intent(Intent.ACTION_MAIN);		
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);			    			
			intent.setClassName(getTargetContext().getPackageName(),launcherActivityClass);	
			myActive = startActivitySync(intent);
			Log.d(TAG, "App is started");
		}
		if(solo == null){
			prepareLooper();
			solo = new Solo(this,myActive);	
		}
			return solo;
		}
		
		public SoloExecutor getExecutor(){	
			if(executor == null){			
				executor = new SoloExecutor(this, this);
			}		
			return executor;	
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

	}




