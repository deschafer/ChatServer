package com.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class User implements Runnable
{
	private Socket clientSocket;
	private boolean running = true;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private String userID;
	private boolean logged = false;
	private Server server;

	public User(Socket clientSocket, Server server)
	{
		this.clientSocket = clientSocket;
		this.server = server;

		// create the input and output streams
		try
		{
			dataInputStream = new DataInputStream(clientSocket.getInputStream());
			dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
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

		// before any real commands can be processed, the user must log in

		// once logged in, then the user can process commands

		while (running)
		{
			logInUser();
		}
	}

	// does not return until the user is logged in
	private void logInUser()
	{
		String readLine = "";
		while(!logged)
		{
			// get input from the user
			// recognize it as a command
			// if its a login command, then check the server to see
			// if that name is available

			try
			{
				readLine = dataInputStream.readUTF();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

			Command command = CommandParser.parseLine(readLine);
			if (command != null)
			{
				// the user name is the first argument
				ArrayList<String> args = command.getArguments();
				userID = args.get(0);

				// verify that this server name is available
				if (server.registerUser(userID, this))
				{
					System.out.println("Login successful, user " + userID + " logged in.");
				} else
				{
					System.out.println("Login failed, user " + userID + " was already logged in.");
				}
			}
		}
	}
}
