package com.server.client;

import java.util.HashMap;

public class ClientLocationManager
{

	static public class Vector3
	{
		public float x;
		public float y;
		public float z;
	}

	static private HashMap<String, Vector3> positions = new HashMap<>();	// updated position of the logged in user
	static private HashMap<String, Float> orientations = new HashMap<>();	// updated position of the logged in user

	static public void updatePosition(String username, Vector3 position, float orientation)
	{
		positions.put(username, position);
		orientations.put(username, orientation);
	}

	static public Vector3 getPosition(String string)
	{
		return positions.get(string);
	}

	static public float getOrientation(String string)
	{
		return orientations.get(string);
	}
}
