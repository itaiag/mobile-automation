package org.topq.jsystem.mobile;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Instrumentation;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class SoloExecutor {

	private static final String TAG = "SoloExecutor";
	private static final String SUCCESS_STRING = "SUCCESS:";
	private static final String ERROR_STRING = "ERROR:";
	private static final String RESULT_STRING = "RESULT";
	private Instrumentation instrumentation;
	private Solo solo;
	private final ISoloProvider soloProvider;

	public SoloExecutor(final ISoloProvider soloProvider, Instrumentation instrumentation) {
		super();
		this.soloProvider = soloProvider;
		this.instrumentation = instrumentation;
	}

	public JSONObject execute(final String data) throws JSONException  {
		ScriptParser parser = null;
		JSONObject result = new JSONObject();
		try{
			parser = new ScriptParser(data);
		}catch (JSONException e) {
			result.put(RESULT_STRING, handleException(" new ScriptParser("+data+")",e));
			return result;

		}
		Log.d(TAG, data);
		for (CommandParser command : parser.getCommands()) {
			if (command.getCommand().equals("enterText")) {
				result.put(RESULT_STRING, enterText(command.getArguments()));
			} else if (command.getCommand().equals("clickOnButton")) {
				result.put(RESULT_STRING, clickOnButton(command.getArguments()));
			} else if (command.getCommand().equals("launch")) {
				result.put(RESULT_STRING, launch());
			} else if (command.getCommand().equals("clickInList")) {
				result.put(RESULT_STRING, clickInList(command.getArguments()));
			} else if (command.getCommand().equals("clearEditText")) {
				result.put(RESULT_STRING, clearEditText(command.getArguments()));
			} else if (command.getCommand().equals("clickOnButtonWithText")) {
				result.put(RESULT_STRING, clickOnButtonWithText(command.getArguments()));
			} else if (command.getCommand().equals("clickOnView")) {
				result.put(RESULT_STRING, clickOnView(command.getArguments()));
			} else if (command.getCommand().equals("clickOnText")) {
				result.put(RESULT_STRING, clickOnText(command.getArguments()));
			} else if (command.getCommand().equals("sendKey")) {
				result.put(RESULT_STRING, sendKey(command.getArguments()));
			} else if (command.getCommand().equals("clickOnMenuItem")) {
				result.put(RESULT_STRING, clickOnMenuItem(command.getArguments()));
			} else if (command.getCommand().equals("getText")) {
				result.put(RESULT_STRING, getText(command.getArguments()));
			} else if (command.getCommand().equals("getTextViewIndex")) {
				result.put(RESULT_STRING, getTextViewIndex(command.getArguments()));
			} else if (command.getCommand().equals("getTextView")) {
				result.put(RESULT_STRING, getTextView(command.getArguments()));
			} else if (command.getCommand().equals("getCurrentTextViews")) {
				result.put(RESULT_STRING, getCurrentTextViews(command.getArguments()));
			} else if (command.getCommand().equals("clickOnHardware")) {
				result.put(RESULT_STRING, clickOnHardware(command.getArguments()));

			} else if (command.getCommand().equals("createFileInServer")) {
				result.put(RESULT_STRING, createFileInServer(command.getArguments()));
			} else if (command.getCommand().equals("pull")) {
				return pull(command.getArguments());
			} else if (command.getCommand().equals("activateIntent")) {
				result.put(RESULT_STRING, activateIntent(command.getArguments()));
			}
		}
		Log.e(TAG,"The Result is:"+result);
		return result;

	}


	private String activateIntent(JSONArray arguments) {
		String command = null;
		try {
			command = "the command  activateIntent(" + arguments.getString(0) + " " + arguments.getString(1) + " "
					+ arguments.getString(2) + " " + arguments.getString(3) + " " + arguments.getString(4) + ")";

			/*
			 * if (arguments[0].equals("ACTION_VIEW")) { Intent webIntent = new
			 * Intent(Intent.ACTION_VIEW, Uri.parse(arguments[1])); Log.d(TAG,
			 * "Sending intent to " + solo.getClass().getSimpleName());
			 * solo.getCurrentActivity().startActivityForResult(webIntent, 1);
			 * }else if (arguments[0].equals("com.greenroad.PlayTrip")) {
			 */
			Log.d(TAG, "Sending intent to com.greenroad.PlayTrip");
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(arguments.getString(0));
			for (int i = 1; i < arguments.length(); i = i + 2) {

				broadcastIntent.putExtra(/*
										 * solo.getCurrentActivity().
										 * getCallingPackage()+
										 */arguments.getString(i), arguments.getString(i + 1));
			}
			solo.getCurrentActivity().sendBroadcast(broadcastIntent);

			// }
			/*
			 * else if (arguments[0].equals("ACTION_SEND")) { Intent sendIntent
			 * = new Intent(); sendIntent.setAction(arguments[0]); for(int i =
			 * 1;i<arguments.length;i=i+2){ Log.i(TAG, "check: " + arguments[i]
			 * + " " + arguments[i+1]);
			 * sendIntent.putExtra(solo.getCurrentActivity
			 * ().getCallingPackage()+
			 * "."+arguments[i],android.content.Intent.EXTRA_TEXT,
			 * arguments[i+1]); }
			 * solo.getCurrentActivity().startActivity(sendIntent); }
			 */
		} catch (JSONException e) {
			handleException(command, e);
			return null;
		}
		return SUCCESS_STRING + command;
	}

	private JSONObject pull(JSONArray arguments) {
		String command = "the command  pull";
		String allText = "";
		JSONObject result = null;
		DataInputStream in = null;
		FileInputStream fstream = null;
		try {
			command += "(" + arguments.getString(0) + ")";
			fstream = new FileInputStream(arguments.getString(0));
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) {
				allText += line;
			}
			result = new JSONObject();
			result.put("file", allText);
			in.close();
			fstream.close();
		} catch (Exception e) {
			handleException(command, e);
			return null;
		} 
		return result;
	}

	private String createFileInServer(JSONArray arguments) {
		String command = "the command  createFileInServer";
		try {
			if (arguments.getBoolean(2)) {
				byte[] data = Base64.decode(arguments.getString(1), Base64.URL_SAFE);
				command += "(" + arguments.getString(0) + ", " + data + ")";
				command += "(" + arguments.getString(0) + ", " + data + ")";
				FileOutputStream fos = new FileOutputStream(arguments.getString(0));
				fos.write(data);
				fos.close();
			} else {
				command += "(" + arguments.getString(0) + ", " + arguments.getString(1) + ")";
				FileWriter out = new FileWriter(arguments.getString(0));
				out.write(arguments.getString(1));
				out.close();
			}
			Log.d(TAG, "run the command:" + command);
		} catch (Exception e){
			return handleException(command, e);
		}
		return SUCCESS_STRING + command;
	}

	private String getCurrentTextViews(JSONArray arguments) {
		String command = "the command  getCurrentTextViews";
		StringBuilder response = new StringBuilder();
		try {

			command += "(" + arguments.getString(0) + ")";

			List<TextView> textViews = solo.getCurrentTextViews(null);
			for (int i = 0; i < textViews.size(); i++) {
				response.append(i).append(",").append(textViews.get(i).getText().toString()).append(";");
			}
		} catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + command + ",Response: " + response.toString();
	}

	private String getTextView(JSONArray arguments) {
		String command = "the command  getTextView";
		String response = "";
		try {
			command += "(" + arguments.getInt(0) + ")";
			response = solo.getCurrentTextViews(null).get(arguments.getInt(0)).getText().toString();
		}  catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + command + ",Response: " + response;
	}

	private String getTextViewIndex(JSONArray arguments) {
		String command = "the command  getTextViewIndex";
		StringBuilder response = new StringBuilder();
		try {
			command += "(" + arguments.getString(0) + ")";
			List<TextView> textViews = solo.getCurrentTextViews(null);
			for (int i = 0; i < textViews.size(); i++) {
				if (arguments.getString(0).trim().equals(textViews.get(i).getText().toString())) {
					response.append(i).append(";");
				}
			}
		} catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + command + ",Response: " + response.toString();
	}

	private String getText(JSONArray arguments) {
		String command = "the command  getText";
		String response = "";
		try {
			command += "(" + arguments.getString(0) + ")";
			response = solo.getText(arguments.getInt(0)).getText().toString();
		}  catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + command + ",Response: " + response;
	}

	private String clickOnMenuItem(JSONArray arguments) {
		String command = "the command  clickOnMenuItem";
		try {
			command += "(" + arguments.getString(0) + ")";
			solo.clickOnMenuItem(arguments.getString(0));
		}  catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + command;
	}

	private String sendKey(JSONArray arguments) {
		String command = "the command  sendKey";
		try {
			command += "(" + arguments.getString(0) + ")";
			solo.sendKey(arguments.getInt(0));
		}  catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + command;
	}

	private String clickOnView(JSONArray arguments) {
		String command = "the command  clickOnView";
		try {
			command += "(" + arguments.getString(0) + ")";
			solo.clickOnView(solo.getCurrentViews().get((arguments.getInt(0))));
		}  catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + command;
	}

	private String clickOnButtonWithText(JSONArray arguments) {
		String command = "the command  clickOnButton";
		try {
			command += "(" + arguments.getString(0) + ")";
			solo.clickOnButton(arguments.getString(0));
		} catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + command;
	}

	private String clearEditText(JSONArray arguments) {
		String command = "the command  clearEditText";
		try {
			command += "(" + arguments.getString(0) + ")";
			solo.clearEditText(arguments.getInt(0));
		}  catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + command;
	}

	private String clickInList(JSONArray arguments) {
		String command = "the command  clickInList(";
		try {
			command += "(" + arguments.getString(0) + ")";
			solo.clickInList(arguments.getInt(0));
		}  catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + command;

	}

	private String clickOnButton(JSONArray params) {
		String command = "the command  clickOnButton";
		try {
			command += "(" + params.getString(0) + ")";
			solo.clickOnButton(params.getInt(0));
		}  catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + command;
	}

	private String enterText(JSONArray params) {
		String command = "the command  clickOnButton";
		try {
			command += "(" + params.getString(0) + ")";
			solo.enterText(params.getInt(0), params.getString(1));
		}  catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + command;

	}

	private String clickOnText(JSONArray params) {
		String command = "the command clickOnText";
		try {
			command += "(" + params.getString(0) + ")";
			solo.clickOnText(params.getString(0));

		}  catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + command;

	}

	private String clickOnHardware(JSONArray keyString) {
		String command = "the command clickOnHardware";
		try {
			command += "(" + keyString.getString(0) + ")";
			int key = (keyString.getString(0) == "HOME") ? KeyEvent.KEYCODE_HOME : KeyEvent.KEYCODE_BACK;
			instrumentation.sendKeyDownUpSync(key);
		} catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + "click on hardware";
	}

	private String launch() {
		Log.i(TAG, "Robotium: About to launch application");
		String command = "the command  launch";
		try {
			solo = soloProvider.getSolo();
		} catch (Throwable e) {
			return handleException(command, e);
		} 
		return SUCCESS_STRING + command;

	}
	

	private String handleException(final String command,Throwable e) {
		String error =ERROR_STRING + command+" failed due to " + e.getStackTrace();
		Log.e(TAG,error);
		return error;
	}

}
