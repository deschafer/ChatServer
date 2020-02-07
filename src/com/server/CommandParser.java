package com.server;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandParser
{

	public static Command parseLine(String line)
	{
		Command command = null;

		// split into tokens
		String[] tokens = line.split(" ");

		// isolate the command string itself
		String commandString = tokens[0];

		// then get each of the arguments
		ArrayList<String> arguments = new ArrayList<>(Arrays.asList(tokens).subList(1, tokens.length));

		// parse the line
		if (commandString.equals(Command.CommandType.LOGIN.matchedString))
		{
			command = handleLoginCommand(commandString, arguments);
		}
		else if (commandString.equals(Command.CommandType.LOGOUT.matchedString))
		{
			command = handleLogoutCommand(commandString, arguments);
		}
		else if (commandString.equals(Command.CommandType.VERSION.matchedString))
		{
			command = handleLogoutCommand(commandString, arguments);
		}
		else
		{
			command = new Command(Command.CommandType.INVALID, arguments);
		}

		return command;
	}

	private static Command handleLoginCommand(String commandString, ArrayList<String> arguments)
	{
		Command command = null;

		// proceed only is we have more than one argument
		if (arguments.size() > 0)
		{
			// TODO: remove once complete
			System.out.println("Login command with " + arguments.size() + " arguments");

			// then we only want two args in total, the command and the username
			if (arguments.size() > 1)
			{
				// give a warning, but we can still proceed
				System.out.println("Login: Two many args passed, only using first argument " + arguments.get(0) + " as login");
			}

			// create the command
			command = new Command(Command.CommandType.LOGIN, arguments);
		} else
		{
			command = new Command(Command.CommandType.LOGIN, arguments);
		}
		return command;
	}

	private static Command handleLogoutCommand(String commandString, ArrayList<String> arguments)
	{
		Command command = null;

		// TODO: remove once complete
		System.out.println("Logout command with " + arguments.size() + " arguments");

		// then we only want two args in total, the command and the username
		if (arguments.size() > 0)
		{
			// give a warning, but we can still proceed
			System.out.println("Logout: arguments ignored");
		}

		// create the command
		command = new Command(Command.CommandType.LOGOUT, arguments);

		return command;
	}

	private static Command handleVersionCommand(String commandString, ArrayList<String> arguments)
	{
		Command command = null;

		// then we only want two args in total, the command and the username
		if (arguments.size() > 0)
		{
			// give a warning, but we can still proceed
			System.out.println("Version: arguments ignored");
		}

		return command = new Command(Command.CommandType.VERSION, arguments);
	}
}
