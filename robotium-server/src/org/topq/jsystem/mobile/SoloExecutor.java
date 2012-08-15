package org.topq.jsystem.mobile;

import java.util.List;

import android.util.Log;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class SoloExecutor {

	private static final String TAG = "SoloExecutor";
	private static final String SUCCESS_STRING = "SUCCESS:";
	private static final String ERROR_STRING = "ERROR:";
	private Solo solo;
	private final ISoloProvider soloProvider;

	public SoloExecutor(final ISoloProvider soloProvider) {
		super();
		this.soloProvider = soloProvider;
	}

	public String execute(final String data) {
		ScriptParser parser = new ScriptParser(data);
		String result = "";
		for (CommandParser command : parser.getCommands()) {
			Log.d(TAG, "Process command: " + command.getCommand());
			if (command.getCommand().equals("enterText")) {
				result += SUCCESS_STRING + "Mock tests";
			}
			if (command.getCommand().equals("enterText")) {
				result += enterText(command.getArguments());
			} else if (command.getCommand().equals("clickOnButton")) {
				result += clickOnButton(command.getArguments());
			} else if (command.getCommand().equals("launch")) {
				result += launch();
			} else if (command.getCommand().equals("clickInList")) {
				result += clickInList(command.getArguments());
			} else if (command.getCommand().equals("clearEditText")) {
				result += clearEditText(command.getArguments());
			} else if (command.getCommand().equals("clickOnButtonWithText")) {
				result += clickOnButtonWithText(command.getArguments());
			} else if (command.getCommand().equals("clickOnView")) {
				result += clickOnView(command.getArguments());
			} else if (command.getCommand().equals("clickOnText")) {
				result += clickOnText(command.getArguments());
			} else if (command.getCommand().equals("goBack")) {
				result += goBack();
			} else if (command.getCommand().equals("sendKey")) {
				result += sendKey(command.getArguments());
			} else if (command.getCommand().equals("clickOnMenuItem")) {
				result += clickOnMenuItem(command.getArguments());
			} else if (command.getCommand().equals("getText")) {
				result += getText(command.getArguments());
			} else if (command.getCommand().equals("getTextViewIndex")) {
				result += getTextViewIndex(command.getArguments());
			} else if (command.getCommand().equals("getTextView")) {
				result += getTextView(command.getArguments());
			} else if (command.getCommand().equals("getCurrentTextViews")) {
				result += getCurrentTextViews(command.getArguments());
			}
		}
		return result;

	}

	private String getCurrentTextViews(String[] arguments) {
		String command = "the command  getCurrentTextViews(" + arguments[0] + ")";
		StringBuilder response = new StringBuilder();
		try {
			List<TextView> textViews = solo.getCurrentTextViews(null);
			for (int i = 0; i < textViews.size(); i++) {
				response.append(i).append(",").append(textViews.get(i).getText().toString()).append(";");
			}
		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;

		}
		return SUCCESS_STRING + command + ",Response: " + response.toString();
	}

	private String getTextView(String[] arguments) {
		String command = "the command  getTextView(" + arguments[0] + ")";
		String response = "";
		try {
			response = solo.getCurrentTextViews(null).get(Integer.parseInt(arguments[0])).getText().toString();
		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;

		}
		return SUCCESS_STRING + command + ",Response: " + response;
	}

	private String getTextViewIndex(String[] arguments) {
		String command = "the command  getTextViewIndex(" + arguments[0] + ")";
		StringBuilder response = new StringBuilder();
		try {
			List<TextView> textViews = solo.getCurrentTextViews(null);
			for (int i = 0; i < textViews.size(); i++) {
				if (arguments[0].trim().equals(textViews.get(i).getText().toString())) {
					response.append(i).append(";");
				}
			}
		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;

		}
		return SUCCESS_STRING + command + ",Response: " + response.toString();
	}

	private String getText(String[] arguments) {
		String command = "the command  getText(" + arguments[0] + ")";
		String response = "";
		try {
			response = solo.getText(Integer.parseInt(arguments[0])).getText().toString();
		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;

		}
		return SUCCESS_STRING + command + ",Response: " + response;
	}

	private String clickOnMenuItem(String[] arguments) {
		String command = "the command  clickOnMenuItem(" + arguments[0] + ")";
		try {
			solo.clickOnMenuItem(arguments[0]);
		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;

		}
		return SUCCESS_STRING + command;
	}

	private String sendKey(String[] arguments) {
		String command = "the command  sendKey(" + arguments[0] + ")";
		try {
			solo.sendKey(Integer.parseInt(arguments[0]));
		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;

		}
		return SUCCESS_STRING + command;
	}

	private String goBack() {
		String command = "the command  goBack()";
		try {
			solo.goBack();
		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;

		}
		return SUCCESS_STRING + command;
	}

	private String clickOnView(String[] arguments) {
		String command = "the command  clickOnView(" + arguments[0] + ")";
		try {
			solo.clickOnView(solo.getCurrentViews().get(Integer.parseInt(arguments[0])));
		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;

		}
		return SUCCESS_STRING + command;
	}

	private String clickOnButtonWithText(String[] arguments) {
		String command = "the command  clickOnButton(" + arguments[0] + ")";
		try {
			solo.clickOnButton(arguments[0]);
		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;
		}
		return SUCCESS_STRING + command;
	}

	private String clearEditText(String[] arguments) {
		String command = "the command  clearEditText(" + arguments[0] + ")";
		try {
			solo.clearEditText(Integer.parseInt(arguments[0]));
		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;
		}
		return SUCCESS_STRING + command;
	}

	private String clickInList(String[] arguments) {
		String command = "the command  clickInList(" + arguments[0] + ")";
		try {
			solo.clickInList(Integer.parseInt(arguments[0]));
		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;
		}
		return SUCCESS_STRING + command;

	}

	private String clickOnButton(String[] params) {
		String command = "the command  clickOnButton(" + params[0] + ")";
		try {

			solo.clickOnButton(Integer.parseInt(params[0]));
		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;
		}
		return SUCCESS_STRING + command;
	}

	private String enterText(String[] params) {
		String command = "the command  clickOnButton(" + params[0] + ")";
		try {
			solo.enterText(Integer.parseInt(params[0]), params[1]);
		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;
		}
		return SUCCESS_STRING + command;

	}

	private String clickOnText(String[] params) {
		String command = "the command clickOnText(" + params[0] + ")";
		try {
			solo.clickOnText(params[0]);

		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;
		}
		return SUCCESS_STRING + command;

	}

	private String launch() {
		Log.i(TAG, "Robotium: About to launch application");
		String command = "the command  launch";
		try {
			solo = soloProvider.getSolo();
		} catch (Error e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;
		}
		return SUCCESS_STRING + command;

	}

}
