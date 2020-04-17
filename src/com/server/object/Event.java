package com.server.object;

import java.util.ArrayList;
import java.util.Arrays;

public class Event
{
	public enum EventType
	{
		UNDEFINED("UND"),						// { }
		TEST("TEST"),							// { }
		ADD_PLAYER("ADD_PLAYER"),				// {playerName, newX, newY, newZ, orientation}
		ADD_ENEMY("ADD_ENEMY"),
		LOGIN("LOGIN"),
		LOGOUT("LOGOUT"),
		PLAYER_DAMAGE("PLAYER_DAMAGE"),
		ENEMY_DAMAGE("ENEMY_DAMAGE"),
		PLAYER_DEATH("PLAYER_DEATH"),
		REMOVE_PROJECTILE("REMOVE_PROJECTILE"),
		REMOVE_ENEMY("REMOVE_ENEMY"),
		REMOVE_PLAYER("REMOVE_PLAYER"),
		MOVE_PLAYER("MOVE_PLAYER"),
		MOVE_ENEMY("MOVE_ENEMY"),
		FIRE("FIRE"),
		MSG("MSG");

		public String name;

		EventType(String name)
		{
			this.name = name;
		}
	}

	private String destinationUser;
	private EventType type;
	private ArrayList<String> arguments = new ArrayList<>();

	public Event(EventType type, String[] args, String destinationUser)
	{
		this.type = type;
		this.destinationUser = destinationUser;
		arguments.addAll(Arrays.asList(args));
	}
	public Event(EventType type, ArrayList<String> args, String destinationUser)
	{
		this.type = type;
		this.destinationUser = destinationUser;
		arguments.addAll(args);
	}

	public EventType Type()
	{
		return type;
	}

	public ArrayList<String> GetArgs()
	{
		return arguments;
	}

	public String getDestinationUser()
	{
		return destinationUser;
	}

	public void setDestinationUser(String destinationUser)
	{
		this.destinationUser = destinationUser;
	}
}
