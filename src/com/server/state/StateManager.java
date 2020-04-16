package com.server.state;

import com.server.client.ClientLocationManager;
import com.server.client.User;
import com.server.object.Collidable;
import com.server.object.Event;
import com.server.serverprocess.Server;


import java.util.ArrayList;
import java.util.HashMap;

public class StateManager
{
	private static HashMap<String, Collidable> players = new HashMap<>();
	private static ArrayList<Collidable> enemies = new ArrayList<>();
	private static ArrayList<Collidable> projectiles = new ArrayList<>();

	private static ArrayList<Collidable> movedPlayers = new ArrayList<>();

	private static ArrayList<Event> eventsReceived = new ArrayList<Event>();	// events received from the client
	private static Object eventLock = new Object();

	private static Server server;

	public static void SetServer(Server server) { StateManager.server = server;}

	public static void AddPlayer(String name, Collidable player)
	{
		players.put(name, player);
	}

	public static void AddEnemy(Collidable enemy)
	{
		enemies.add(enemy);
	}
	public static void AddProjectile(Collidable projectile)
	{
		projectiles.add(projectile);
	}

	public static Collidable GetPlayer(String name)
	{
		return players.get(name);
	}

	public static Collidable GetEnemy(int index)
	{
		Collidable collidable = null;
		if (index < enemies.size())
		{
			collidable = enemies.get(index);
		}
		return collidable;
	}

	public static Collidable GetProjectile(int index)
	{
		Collidable collidable = null;
		if (index < projectiles.size())
		{
			collidable = projectiles.get(index);
		}
		return collidable;
	}

	public static void Update()
	{
		// check for collisions of collidables
		// for this game, this includes between players and
		// enemies, and projectiles between players and players
		// or also player and enemy.

		// check if any events have been added to the system
		synchronized (eventLock)
		{
			for (Event event : eventsReceived)
			{
				HandleEvent(event);
			}
			eventsReceived.clear();
		}

		for (Collidable player : movedPlayers)
		{
			// update and send a new event for every other user
			for (String userName : server.getRegisteredUsers())
			{
				if (!userName.equals(player.name))
				{
					String[] args = {userName, "" + player.position.x, "" + player.position.y, "" + player.position.z, "" + 0.0f};
					Event event = new Event(Event.EventType.MOVE_PLAYER, args, player.name);

					server.queueEvent(event);
				}
			}
		}
		movedPlayers.clear();
	}

	private static void HandleEvent(Event event)
	{
		// decode the event type
		// handle the event here
		if (event.Type() == Event.EventType.TEST)
		{
			System.out.println("Test event received");
		}
		else if (event.Type() == Event.EventType.ADD_PLAYER)
		{
			System.out.println("Player added event received");

			Collidable player = new Collidable();
			player.name = event.GetArgs().get(0);

			// need to add the player to the list
			players.put(event.GetArgs().get(0), player);


			// adding new player to all other players
			for (String userName : server.getRegisteredUsers())
			{
				// add this new player to all other clients
				if (!userName.equals(player.name))
				{
					// the args should have the new player details
					String[] args = {player.name, "" + 0, "" + 0, "" + 0, "" + 0};

					// it should be sent to the other player
					server.queueEvent(new Event(Event.EventType.ADD_PLAYER, args, userName));
				}
			}

			// then adding all other players to the new player
			for (String otherPlayerName : server.getRegisteredUsers())
			{
				// if this is another player
				if (!otherPlayerName.equals(player.name))
				{
					// the args should have the other player details
					String[] args = {otherPlayerName, "" + 0, "" + 0, "" + 0, "" + 0};

					// it should be sent to the new player
					server.queueEvent(new Event(Event.EventType.ADD_PLAYER, args, player.name));
				}
			}

		}
		else if (event.Type() == Event.EventType.MOVE_PLAYER)
		{
			// {playerName, newX, newY, newZ, orientation}

			// update the position of the player
			ClientLocationManager.Vector3 position = players.get(event.GetArgs().get(0)).position;
			ArrayList<String> args = event.GetArgs();
			Collidable player = players.get(event.GetArgs().get(0));
			player.position.x = Float.parseFloat(args.get(1));
			player.position.y = Float.parseFloat(args.get(2));
			player.position.z = Float.parseFloat(args.get(3));

			movedPlayers.add(player);
		}
	}

	public static void AddEventReceived(Event event)
	{
		synchronized (eventLock)
		{
			eventsReceived.add(event);
		}
	}
}
