package org.topq.jsystem.mobile;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

public class ScriptParser {

	private static final String LINES_DELIMITER = ";";
	private final List<CommandParser> commands;

	public ScriptParser(final String data) throws JSONException {
		commands = new ArrayList<CommandParser>();
		String[] commandsStringsArr = data.split(LINES_DELIMITER);
		for (String commandStr : commandsStringsArr) {
			commands.add(new CommandParser(commandStr));
		}
	}

	public List<CommandParser> getCommands() {
		return commands;
	}

}
