package com.server.parsing;

import com.google.gson.*;
import com.server.object.Event;

import java.util.ArrayList;

public class GsonParser
{
	private static Gson gsonParser = new Gson();
	private static JsonParser parser = new JsonParser();

	public static String ToJson(Object object)
	{
		return gsonParser.toJson(object);
	}

	public static boolean IsInJsonFormat(String string)
	{
		boolean result = true;

		try
		{
			JsonObject rootObject = parser.parse(string).getAsJsonObject();
		} catch (com.google.gson.JsonSyntaxException|java.lang.IllegalStateException e)
	   	{
			result = false;
	   	}

		return result;
	}

	public static Event GetEvent(String jsonString)
	{
		// get the event from the object
		JsonObject rootObject = parser.parse(jsonString).getAsJsonObject();

		Event.EventType type = Event.EventType.UNDEFINED;

		String aux = rootObject.get("type").toString();
		aux = aux.replace("\"", "");

		if (aux.equals(Event.EventType.TEST.name))
		{
			type = Event.EventType.TEST;
		}
		else if (aux.equals(Event.EventType.ADD_PLAYER.name))
		{
			type = Event.EventType.ADD_PLAYER;
		}
		else if (aux.equals(Event.EventType.MOVE_PLAYER.name))
		{
			type = Event.EventType.MOVE_PLAYER;
		}
		else if (aux.equals(Event.EventType.FIRE.name))
		{
			type = Event.EventType.FIRE;
		}
		else if (aux.equals(Event.EventType.PLAYER_DEATH.name))
		{
			type = Event.EventType.PLAYER_DEATH;
		}
		else if (aux.equals(Event.EventType.MSG.name))
		{
			type = Event.EventType.MSG;
		}

		ArrayList<String> arguments = new ArrayList<>();

		if (type != Event.EventType.UNDEFINED)
		{
			JsonArray array = rootObject.get("arguments").getAsJsonArray();
			for (JsonElement element : array)
			{
				arguments.add(element.toString().replace("\"", ""));
			}
		}
		Event parsedEvent = new Event(type, arguments, "");
		return parsedEvent;
	}
}
