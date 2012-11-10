package com.example.tcpserver;

import org.json.JSONObject;

public interface IDataCallback {

	public JSONObject dataReceived(String data);
}
