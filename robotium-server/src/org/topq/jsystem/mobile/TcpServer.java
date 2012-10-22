package org.topq.jsystem.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONObject;

import android.util.Log;

/**
 * <b>TCP Server</b><br>
 * Responisible for receiving & sending JSON info from & to JSystem
 * 
 * @author topq
 * 
 */
public class TcpServer implements Runnable {

	private static final String TAG = "TcpServer";

	private static final int PORT = 4321;

	private ArrayList<IDataCallback> listeners;

	private String sendResponse;

	private boolean done = false;

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
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			serverSocket = new ServerSocket(4321);
			String line = null;
			Log.d(TAG, "Waiting for connections");
			clientSocket = serverSocket.accept();
			do {
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				line = in.readLine();
				if (line != null) {
					if (line.equals("exit")) {
						break;
					}
					Log.d(TAG, "Received: '" + line + "'");
					JSONObject response = null;
					for (IDataCallback listener : listeners) {
						// TODO: This is not the best implementation. The
						// response should be handled differently.
						response = listener.dataReceived(line);
					}
					out.println(response);
				}
			} while (true);
		} catch (Exception e) {
			Log.e(TAG, "Failed due to " + e.getMessage());
		} finally {
			out.close();
			try {
				in.close();
				clientSocket.close();
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// String inputLine;
		// long startTime;
		//
		// while ((inputLine = in.readLine()) != null && !done) {
		//
		// Log.d(TAG, "Received: '" + inputLine + "'");
		//
		// // Check whether this is a JSON object
		// if (inputLine.startsWith("{") && (inputLine.endsWith("}"))) {
		// // Signal all listeners that we have a new test request
		// for (IDataCallback listener : listeners) {
		// // TODO: This is not the best implementation. The
		// // response should be handled differently.
		// sendResponse = listener.dataReceived(inputLine);
		// }
		//
		// startTime = System.currentTimeMillis();
		// // Wait for test response
		// while (sendResponse == null || sendResponse.equals("")) {
		// Thread.sleep(1000);
		// // Remove this if want to wait forever for test response
		// // // Un-comment if want to stop waiting after 5 minutes
		// if (System.currentTimeMillis() - startTime > 5 * 60 * 1000) {
		// sendResponse = "Waited over 5 minutes without response";
		// break;
		// }
		// }
		// out.println(sendResponse);
		// sendResponse = null;
		// }
		// }
		//
		// out.close();
		// in.close();
		// clientSocket.close();
		// serverSocket.close();
		// } catch (IOException e) {
		// Log.e(TAG, "IOException", e);
		// } catch (InterruptedException e) {
		// Log.e(TAG, "InterruptedException", e);
		// }
	}
	
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ ERAN CHANGES IMPL NEED TO CHANGE AGAIN IN THE FUTURE ONCE DECIDED ON STABLE CODE
//	@Override
//	public void run() {
//		ServerSocket serverSocket = null;
//		Socket clientSocket = null;
//		try {
//			serverSocket = new ServerSocket(PORT);
//			do {
//				Log.d(TAG, "Server is waiting for connection");
//				clientSocket = serverSocket.accept();
//				PrintWriter out = null;
//				BufferedReader in = null;
//				try {
//					Log.d(TAG, "Connection was established");
//					out = new PrintWriter(clientSocket.getOutputStream(), true);
//					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//					String line = in.readLine();
//					if (line != null) {
//						Log.d(TAG, "Received: '" + line + "'");
//						if (line.trim().equals("{\"Command\":\"exit\",\"Params\":[]}")) {
//							done = true;
//						}
//						JSONObject response = null;
//						for (IDataCallback listener : listeners) {
//							// TODO: This is not the best implementation. The
//							// response should be handled differently.
//							response = listener.dataReceived(line);
//						}
//						out.println(response);
//						out.flush();
//					}
//
//
//
//				}  catch (Exception e) {
//					Log.e(TAG, "Failed to process request due to" + e.getMessage());
//				} finally {
//					// Closing resources
//					if (null != out) {
//						out.close();
//					}
//					try {
//						if (null != in) {
//							in.close();
//						}
//						if (null != clientSocket) {
//							clientSocket.close();
//						}
//					} catch (Exception e) {
//						Log.w(TAG, "exception was caught while closing resources", e);
//					}
//				} 
//			} while (!done);
//		}
//		catch (IOException e){ 
//			Log.w(TAG, "exception was caught while handling server socket", e);
//		}
//
//		finally {
//			if (null != serverSocket ){
//				try {
//					serverSocket.close();
//				} catch (IOException e) {
//					Log.w(TAG, "exception was caught while closing resources", e);
//				}
//			}
//		}
//	}



	public void stop() {
		done = true;
	}

	public boolean isRunning() {
		return !done;
	}
}