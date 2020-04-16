package com.server.command;

import java.util.ArrayList;

public class Command
{
	public enum CommandType
	{
		LOGIN("\\login"),
		LOGOUT("\\logout"),
		VERSION("\\version"),
		SAY("\\say"),
		TELL("\\tell"),
		ECHO(""),
		USERS("\\users"),
		INVALID("");

		public final String matchedString;

		CommandType(String matchedString) {
			this.matchedString = matchedString;
		}
	}

	private ArrayList<String> arguments;
	private CommandType type;

	public Command(Command.CommandType type, ArrayList<String> args)
	{
		arguments = args;
		this.type = type;
	}

	public CommandType getType()
	{
		return type;
	}

	public ArrayList<String> getArguments()
	{
		return arguments;
	}
}
