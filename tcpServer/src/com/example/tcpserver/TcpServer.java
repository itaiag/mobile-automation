package com.example.tcpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.ObjectOutputStream.PutField;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

/**
 * <b>TCP Server</b><br>
 * Responisible for receiving & sending JSON info from & to JSystem
 * 
 * @author topq
 * 
 */
public class TcpServer implements Runnable {

	private static final String TAG = "TcpServer -> Tcp Application";

	private static final int PORT = 6262;

	private ArrayList<IDataCallback> listeners;

	private String sendResponse;

	private boolean done = false;
	
//	private final Activity act;

	public TcpServer() {
		listeners = new ArrayList<IDataCallback>();
	}

	public void addTestListener(IDataCallback toAdd) {
		listeners.add(toAdd);
	}

	public void removeTestListener(IDataCallback toRemove) {
		for (IDataCallback current : listeners) {
			if (current.equals(toRemove)) {
				listeners.remove(toRemove);
			}
		}
	}

	public void gotTestResponse(String toAdd) {
		sendResponse = toAdd;
	}

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
			do {
				Log.d(TAG, "Server is waiting for connection");
				clientSocket = serverSocket.accept();
				
				
				PrintWriter clientOut = null;
				BufferedReader clientIn = null;
				try {
					Log.d(TAG, "Connection was established");
					clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
					clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String line = clientIn.readLine();
					PrintWriter serverOutput = null;
					BufferedReader serverInput = null;
					Socket socket = new Socket("localhost", 4321);
					socket.setTcpNoDelay(true);
					if (line != null) {
						
						Log.d(TAG, "Received: '" + line + "'");
						serverOutput = new PrintWriter(socket.getOutputStream());
						serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						serverOutput.append(line+"\n");
						serverOutput.flush();
						
					}
				
					clientOut.println(serverInput.readLine());
					
				}  catch (Exception e) {
					Log.e(TAG, "Failed to process request due to" + e.getMessage());
				} finally {
					// Closing resources
					if (null != clientOut) {
						clientOut.close();
					}
					try {
						if (null != clientIn) {
							clientIn.close();
						}
						if (null != clientSocket) {
							clientSocket.close();
						}
					} catch (Exception e) {
						Log.w(TAG, "exception was caught while closing resources", e);
					}
				} 
			} while (!done);
		} catch (Exception e) {
			Log.e(TAG,"Exception accored : "+e.getMessage());
		
		}finally {
			if (null != serverSocket ){
				try {
					serverSocket.close();
				} catch (IOException e) {
					Log.w(TAG, "exception was caught while closing resources", e);
				}
			}
		}
	}
				

	public void stop() {
		done = true;
	}

	public boolean isRunning() {
		return !done;
	}
}
