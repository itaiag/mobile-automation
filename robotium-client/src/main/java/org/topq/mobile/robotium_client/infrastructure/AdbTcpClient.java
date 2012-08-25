package org.topq.mobile.robotium_client.infrastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONML;
import org.json.JSONObject;

/**
 * <b>ADB TCP Client</b><br>
 * Handles the TCP send / receive data
 * @author topq
 *
 */
public class AdbTcpClient {

	private Socket socket = null;
	private PrintWriter output = null;
	private BufferedReader input = null;

	public AdbTcpClient(String host, int port) throws Exception {
		// Create a new socket
		socket = new Socket(host, port);
		OutputStream out = socket.getOutputStream();
		// Create the output stream writer
		output = new PrintWriter(out);
		// Create the input stream reader
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
//		output.flush();
	}

	public String sendData(JSONObject data) throws Exception {
		if (output == null){
			throw new IllegalStateException("The output stream is not valid!");
		}
		// Write to socket
		output.println(data);
		output.flush();
		// Wait for response
		return input.readLine();
	}

	public String getData() throws IOException {
		return input.readLine();
	}
	
	public void closeConnection() throws Exception {
		input.close();
		output.close();
		socket.close();
	}

}
