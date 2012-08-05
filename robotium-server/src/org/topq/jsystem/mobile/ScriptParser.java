package org.topq.jsystem.mobile;

import java.util.ArrayList;
import java.util.List;

public class ScriptParser {

	private static final String LINES_DELIMITER = ";";
	private final List<CommandParser> commands;

	public ScriptParser(final String data) {
		commands = new ArrayList<CommandParser>();
		String[] commandsStringsArr = normalizeData(data).split(LINES_DELIMITER);
		for (String commandStr : commandsStringsArr) {
			commands.add(new CommandParser(commandStr));
		}
	}

	private static String normalizeData(String data) {
		return data.replaceFirst("\\{", "").replaceFirst("\\}", "");
	}

	public List<CommandParser> getCommands() {
		return commands;
	}

}
