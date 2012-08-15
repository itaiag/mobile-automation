package org.topq.jsystem.mobile;

import java.util.Arrays;

public class CommandParser {

	private static final String ARGS_DELIMITER = ",";
	private final String command;
	private final String[] arguments;

	public CommandParser(final String data) {
		String[] cmds = data.split(ARGS_DELIMITER);
		command = cmds[0].trim();
		if (cmds.length == 1) {
			arguments = new String[0];
			return;
		}
		arguments = removeFirstElement(cmds);
	}
	

	private static String[] removeFirstElement(String[] commandParts) {
		return Arrays.copyOfRange(commandParts, 1, commandParts.length);
	}

	public String getCommand() {
		return command;
	}

	public String[] getArguments() {
		return arguments;
	}

}
