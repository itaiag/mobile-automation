package org.jsystemtest.mobile.robotium_client.infrastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * <b>ADB TCP Client</b><br>
 * Handles t he TCP send / receive data
 * 
 * @author topq
 * 
 */
public class TcpClient {
	private static Logger logger = Logger.getLogger(TcpClient.class);
	private String lastResult;
	private final String host;
	private final int port;

	public TcpClient(String host, int port) throws Exception {
		this.host = host;
		this.port = port;
	}

	public String sendData(JSONObject data) {
		Socket socket = null;
		BufferedReader input = null;
		try {
			socket = new Socket(host, port);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter output = new PrintWriter(socket.getOutputStream());
			output.println(data);
			output.flush();
			lastResult = input.readLine();
		} catch (UnknownHostException e) {
			logger.error("Uknown host ");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			logger.error("Failed sending data due to ", e);
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (socket != null) {
					socket.close();
				}

			} catch (Exception e) {
				logger.error("Failed closing resources due to ", e);
			}
		}
		return lastResult;
	}

	public String getData() throws IOException {
		return lastResult;
	}
}