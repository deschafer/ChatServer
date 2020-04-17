package com.server.serverprocess;

import com.server.client.SocketOutputThread;
import com.server.client.User;
import com.server.command.Command;
import com.server.object.Event;
import com.server.parsing.GsonParser;
import com.server.session.SessionManager;
import com.server.state.StateManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
	private HashMap<String, User> registeredUsers = new HashMap<>();
	private ArrayList<String> registeredUsersList = new ArrayList<>();
	private HashMap<String, Socket> singleSockets = new HashMap<>();
	private HashMap<String, Socket> writeSockets = new HashMap<>();
	private ArrayList<Socket> writeSocketsList = new ArrayList<>();
	private final Object onlineLock = new Object();
	private final Object queueLock = new Object();
	private final Object eventLock = new Object();
	private ArrayList<Event> queuedEvents = new ArrayList<>();

	public Server(int port, InetAddress address, int maxNumberUsers)
	{
		this.address = address;
		this.port	= port;
		this.maxNumberUsers = maxNumberUsers;

		SessionManager.initialize("RServerSession");

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

		String message = "Server creation was successful at port " + port + " and address " + address;
		SessionManager.record(message);
		System.out.println(message);
	}

	@Override
	public void run()
	{

		StateManager.SetServer(this);

		// run in a loop while we are online
		while (online)
		{
			// update the game state
			StateManager.Update();

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

				// create a temporay input stream for this socket
				DataInputStream temporaryInputStream = null;
				try
				{
					temporaryInputStream = new DataInputStream(newClientSocket.getInputStream());
				} catch (IOException e)
				{
					e.printStackTrace();
					System.exit(-1);
				}

				// wait for input from the new socket
				String readID = "";
				try
				{
					readID = temporaryInputStream.readUTF();
				} catch (IOException e)
				{
					e.printStackTrace();
					System.exit(-1);
				}

				if (singleSockets.containsKey(readID))
				{
					// then we create a new user with both sockets
					// write to the socket to signal success
					DataOutputStream outputStream = null;
					try
					{
						outputStream = new DataOutputStream(newClientSocket.getOutputStream());
					} catch (IOException e)
					{
						e.printStackTrace();
					}

					try
					{
						outputStream.writeUTF(readID);
					} catch (IOException e)
					{
						e.printStackTrace();
					}

					User user = new User(singleSockets.get(readID), newClientSocket, this);
					new Thread(user).start();
				}
				else
				{
					// otherwise add it as a single socket
					singleSockets.put(readID, newClientSocket);
				}

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
						if (nextCommand.getType() == Command.CommandType.SAY)
						{
							// create a string out of all the args

							for (Socket socket : writeSocketsList)
							{
								new Thread(new SocketOutputThread(socket, nextCommand.getArguments().get(0))).start();
							}
						} else if (nextCommand.getType() == Command.CommandType.TELL)
						{
							// create a string out of all the args
							ArrayList<String> arguments = nextCommand.getArguments();
							String userID = arguments.get(0);     // user id is the first argument
							Socket socket;

							if ((socket = writeSockets.get(userID)) != null)
							{
								new Thread(new SocketOutputThread(socket, nextCommand.getArguments().get(1))).start();
							}
						} else if (nextCommand.getType() == Command.CommandType.USERS)
						{
							StringBuilder message = new StringBuilder();
							for (String string : registeredUsersList)
							{
								message.append(string).append("     ");
							}

							String userID = nextCommand.getArguments().get(0);
							Socket socket = registeredUsers.get(userID).getWriteThread();

							new Thread(new SocketOutputThread(socket, message.toString())).start();
						}
					}
				}
			}

			synchronized (eventLock)
			{
				// look at queued events
				for (Event event : queuedEvents)
				{
					// get the json string
					String jsonFormat = GsonParser.ToJson(event);

					Socket writeSocket = writeSockets.get(event.getDestinationUser());

					System.out.println("Sending event");

					new Thread(new SocketOutputThread(writeSocket, jsonFormat)).start();

					SessionManager.record(jsonFormat);
				}
				queuedEvents.clear();
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
		if (registeredUsers.get(user) == null)
		{
			result = true;
			registeredUsers.put(user, userThread);

			writeSockets.put(user, userThread.getWriteThread());

			Socket writeSocket = userThread.getWriteThread();

			if (!writeSocketsList.contains(writeSocket))
			{
				writeSocketsList.add(writeSocket);
				registeredUsersList.add(user);
			}
		}
		return result;
	}

	public void removeUser(String user)
	{
		if (registeredUsers.get(user) != null)
		{
			registeredUsers.remove(user);
			registeredUsersList.remove(user);
		}
	}

	public void queueCommand(Command command)
	{
		synchronized (queueLock)
		{
			queuedCommands.add(command);
		}
	}

	public void queueEvent(Event event)
	{
		synchronized (eventLock)
		{
			queuedEvents.add(event);
		}
	}

	public static String getVersionInfo()
	{
		return "CVE Chatserver version 0.4, Build Feb. 2020";
	}

	public ArrayList<String> getRegisteredUsers()
	{
		return registeredUsersList;
	}
}
