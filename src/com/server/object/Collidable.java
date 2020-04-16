package com.server.object;

import com.server.client.ClientLocationManager;
import javafx.geometry.BoundingBox;

public class Collidable
{
	public String name;
	public int health = 100;
	public BoundingBox boundingBox;
	public ClientLocationManager.Vector3 position = new ClientLocationManager.Vector3();
	public boolean isAlive = true;
}
