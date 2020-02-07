package com.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements Runnable
{
	private InetAddress address;
	private ServerSocket serverSocket;
	private Socket newClientSocket;
	private int port;
	private int maxNumberUsers;
	private boolean online = true;
	private ArrayList<Command> queuedCommands = new ArrayList<>();
	private ArrayList<User> users = new ArrayList<>();
	private HashMap<String, User> registeredUsers = new HashMap<>();
	private final Object registerLock = new Object();
	private final Object onlineLock = new Object();
	private final Object queueLock = new Object();

	public Server(int port, InetAddress address, int maxNumberUsers)
	{
		this.address = address;
		this.port	= port;
		this.maxNumberUsers = maxNumberUsers;

		// attempt to create the server socket
		try
		{
			// create the socket
			serverSocket = new ServerSocket(port, maxNumberUsers, InetAddress.getLocalHost());

			// set the timeout of the server so we can do other things with this thread
			serverSocket.setSoTimeout(5);
		}
		catch (IOException e)
		{
			// no server was created, so return
			System.out.println("Server creation failed " + e);
			return;
		}

		System.out.println("Server creation was successful at port " + port + " and address " + address);
	}

	@Override
	public void run()
	{
		// run in a loop while we are online
		while (online)
		{
			// attempt to accept a new socket
			// note that this will timeout when no user is available, so
			// we need to handle that exception
			try
			{
				newClientSocket = serverSocket.accept();
			} catch (java.net.SocketTimeoutException e)
			{
				// we just need to catch the exception, but we
				// do not need to do anything here
			} catch (IOException e)
			{
				System.out.println("Server socket accept failed " + e);
			}

			if (newClientSocket != null)
			{
				System.out.println("New connection made");
				// handle the new client thread
				// create a new user class
				// pass in the new socket
				// save the user reference

				User user = new User(newClientSocket, this);
				user.run();

				// remove our reference
				newClientSocket = null;
			}

			// if there are queued commands
			// we only process one at a time
			if (!queuedCommands.isEmpty())
			{
				synchronized (queueLock)
				{
					// get the first command
					Command nextCommand = queuedCommands.remove(0);
					if (nextCommand != null)
					{
						// get the users affected by this command
						// write to those users the data within the command
					}
				}
			}
		}
	}

	public void setOnline(boolean online)
	{
		synchronized (onlineLock)
		{
			this.online = online;
		}
	}

	public boolean isOnline()
	{
		synchronized (onlineLock)
		{
			return online;
		}
	}

	public boolean registerUser(String user, User userThread)
	{
		boolean result = false;
		if (!registeredUsers.containsKey(user))
		{
			result = true;
			registeredUsers.put(user, userThread);
		}
		return result;
	}

	public void removeUser(String user)
	{
		if (registeredUsers.containsKey(user))
		{
			registeredUsers.remove(user);
		}
	}
}
