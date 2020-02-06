package com.server;

import java.util.ArrayList;

public class Command
{
	public enum CommandType
	{
		LOGIN,
		LOGOUT
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
