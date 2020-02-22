package com.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class User implements Runnable
{
	private Socket clientSocketRead;
	private Socket clientSocketWrite;
	private boolean running = true;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private String userID;
	private boolean logged = false;
	private Server server;

	public User(Socket clientSocketRead, Socket clientSocketWrite, Server server)
	{
		this.clientSocketRead = clientSocketRead;
		this.clientSocketWrite = clientSocketWrite;
		this.server = server;

		// create the input and output streams
		try
		{
			dataInputStream = new DataInputStream(clientSocketRead.getInputStream());
			dataOutputStream = new DataOutputStream(clientSocketRead.getOutputStream());
		} catch (IOException e)
		{
			System.out.println("Data stream unable to be made " + e);

			// prevent creation of the thread
			running = false;
			return;
		}
	}

	@Override
	public void run()
	{
		String readLine = "";

		while (running)
		{
			// log in the user if needed
			logInUser();

			// read in a command
			try
			{
				readLine = dataInputStream.readUTF();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

			// parse the command
			Command command = CommandParser.parseLine(readLine);

			if (command != null)
			{

				// action based on the command
				if (command.getType() == Command.CommandType.LOGIN)
				{
					// we cannot log in twice
					readLine = "Login: User cannot login twice. Log out, then log back in with the other username";
					System.out.println(readLine);
				} else if (command.getType() == Command.CommandType.LOGOUT)
				{
					// verify the user actually logged in
					if (logged)
					{
						// log out the user
						readLine = "Logging out user " + userID;
						System.out.println(readLine);
						server.removeUser(userID);
						userID = "";
						logged = false;
					} else
					{
						readLine = "User is not logged in " + userID;
						System.out.println(readLine);
					}
				} else if (command.getType() == Command.CommandType.VERSION)
				{
					readLine = Server.getVersionInfo();
				} else if (command.getType() == Command.CommandType.SAY)
				{
					ArrayList<String> args = command.getArguments();
					if (!args.isEmpty())
					{
						args.set(0, userID + " says " + args.get(0));
					}
					command = new Command(command.getType(), args);

					server.queueCommand(command);
				} else if (command.getType() == Command.CommandType.TELL)
				{
					ArrayList<String> args = command.getArguments();
					if (args.size() >= 2)
					{
						args.set(1, userID + " tells you " + args.get(1));
					}
					command = new Command(command.getType(), args);
					server.queueCommand(command);
				}  else if (command.getType() == Command.CommandType.ECHO)
				{
					readLine = "";
				} else
				{
					readLine = "Invalid command " + readLine;
				}
			}
			// write the response
			try
			{
				dataOutputStream.writeUTF(readLine);
			} catch (IOException e)
			{
				e.printStackTrace();
				break;
			}
			SocketOutputThread thread = new SocketOutputThread(clientSocketWrite, readLine);
			new Thread(thread).start();
		}
	}

	// does not return until the user is logged in
	private void logInUser()
	{
		String readLine = "";
		String response = "";
		while(!logged)
		{
			// read a command
			try
			{
				readLine = dataInputStream.readUTF();
			} catch (IOException e)
			{
				e.printStackTrace();
				running = false;
				break;
			}

			// process that command
			Command command = CommandParser.parseLine(readLine);
			if (command != null && command.getType() == Command.CommandType.LOGIN)
			{
				// if there were arguments passed
				if (command.getArguments().size() > 0)
				{
					// the user name is the first argument
					ArrayList<String> args = command.getArguments();
					userID = args.get(0);

					// verify that this server name is available
					if (server.registerUser(userID, this))
					{
						response = "Login successful, user " + userID + " logged in.";
						logged = true;

						// otherwise, welcome the user to our server
						response += "\nWelcome " + userID + " to the CVE ChatServer , Type a command to begin.";
						System.out.println(response);
					} else
					{
						response = "Login failed, user " + userID + " was already logged in.";
						System.out.println(response);
					}
				}
				else
				{
					response = "Login: no username provided, attempt failed";
					System.out.println(response);
				}
			}
			else
			{
				response = "Please log in to enter a command";
				System.out.println(response);
			}

			// respond to the command
			SocketOutputThread thread = new SocketOutputThread(clientSocketWrite, response);
			new Thread(thread).start();
		}
	}

	public Socket getWriteThread()
	{
		return clientSocketWrite;
	}
}
