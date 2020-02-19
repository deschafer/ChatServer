package com.server;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandParser
{
	public static Command parseLine(String line)
	{
		Command command;

		// split into tokens
		String[] tokens = line.split(" ");

		// isolate the command string itself
		String commandString = tokens[0];

		// then get each of the arguments
		ArrayList<String> arguments = new ArrayList<>(Arrays.asList(tokens).subList(1, tokens.length));

		// parse the line and create a command
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
			command = handleVersionCommand(commandString, arguments);
		}
		else if (commandString.equals(Command.CommandType.SAY.matchedString))
		{
			command = handleSayCommand(commandString, arguments);
		}
		else if (commandString.equals(Command.CommandType.TELL.matchedString))
		{
			command = handleTellCommand(commandString, arguments);
		}
		else if (commandString.contains("\\"))
		{
			command = new Command(Command.CommandType.INVALID, arguments);
		}
		else
		{
			command = new Command(Command.CommandType.ECHO, arguments);
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

	private static Command handleSayCommand (String commandString, ArrayList<String> arguments)
	{
		Command command = null;

		// then we only want two args in total, the command and the username
		if (arguments.size() == 0)
		{
			// give a warning, but we can still proceed
			System.out.println("Say: nothing to say");
		}
		else
		{
			String sayString = "";

			// concatenate all the strings
			for (String string : arguments)
			{
				sayString += " " + string;
			}

			arguments.clear();
			arguments.add(sayString);

			command = new Command(Command.CommandType.SAY, arguments);
		}

		return command;
	}

	private static Command handleTellCommand(String commandString, ArrayList<String> arguments)
	{
		Command command = null;

		// then we only want two args in total, the command and the username
		if (arguments.size() == 0)
		{
			// give a warning, but we can still proceed
			System.out.println("Tell: nothing to tell");
		}
		else if (arguments.size() == 1)
		{
			// give a warning, but we can still proceed
			System.out.println("Tell: no message to tell");
		}
		else
		{
			String userID = arguments.get(0);
			String tellString = "";
			// concatenate all the strings
			for (String string : arguments.subList(1, arguments.size()))
			{
				tellString += " " +  string;
			}

			arguments.clear();
			arguments.add(userID);
			arguments.add(tellString);

			command = new Command(Command.CommandType.TELL, arguments);
		}

		return command;
	}
}
