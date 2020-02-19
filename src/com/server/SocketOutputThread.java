package com.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketOutputThread implements Runnable
{
	private Socket writeSocket;
	private DataOutputStream dataOutputStream;
	String text;

	public SocketOutputThread(Socket writeSocket, String text)
	{
		this.writeSocket = writeSocket;
		this.text = text;
		try
		{
			dataOutputStream = new DataOutputStream(writeSocket.getOutputStream());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		try
		{
			dataOutputStream.writeUTF(text);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
