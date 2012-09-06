package org.topq.jsystem.mobile;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class CommandParser {

	private static final String TAG = "CommandParser";
	private  JSONObject command;

	public CommandParser(final String data) throws JSONException{
		Log.d(TAG, "Pars command: " + data);
		 command = new JSONObject(data);
	}

	public String getCommand() throws JSONException {
		Log.d(TAG, "command is:" + command.getString("Command"));
		return command.getString("Command");
	}

	public JSONArray getArguments() throws JSONException {
		Log.d(TAG, "Params are:" +  command.get("Params"));
		return (JSONArray) command.get("Params");
	}

}
