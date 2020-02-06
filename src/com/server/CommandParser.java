package com.server;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandParser
{

	public static Command parseLine(String line)
	{
		Command command = null;

		// parse the line
		if (line.contains("\\login"))
		{
			command = handleLoginCommand(line);
		}
		else if (line.contains("\\logout"))
		{

		}
		else
		{
			System.out.println("Unrecognized Command " + line);
		}

		return command;
	}

	private static Command handleLoginCommand(String line)
	{
		Command command = null;
		String tokens[] = line.split(" ");

		// proceed only is we have more than one argument
		if (tokens.length > 1)
		{
			ArrayList<String> usernames = new ArrayList<>();

			// TODO: remove once complete
			System.out.println("Login command with " + tokens.length + " arguments " + line);

			// verify the first matches our requirement
			if (tokens[0].equals("\\login"))
			{
				// then get each of the arguments
				usernames.addAll(Arrays.asList(tokens).subList(1, tokens.length));
			}

			// then we only want two args in total, the command and the username
			if (usernames.size() > 1)
			{
				// give a warning, but we can still proceed
				System.out.println("Login: Two many args passed, only using first argument " + usernames.get(0) + " as login");
			}

			// create the command
			command = new Command(Command.CommandType.LOGIN, usernames);
		}
		else
		{
			System.out.println("Login: no username provided, attempt failed");
		}
		return command;
	}
}
