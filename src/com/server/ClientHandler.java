package com.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable
{
	private Socket clientSocket;
	private boolean running = true;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private String readLine;

	public ClientHandler(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
	}

	public void run()
	{
		while (running)
		{
			try
			{
				// takes input from the client socket
				dataInputStream = new DataInputStream(clientSocket.getInputStream());
				dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
			} catch (IOException e)
			{
				System.out.println(e.getMessage());
			}

			try
			{
				readLine = dataInputStream.readUTF();
			} catch(IOException e)
			{
				System.out.println(e.getMessage());
				break;
			}

			System.out.println("Client read in " + readLine);

			try
			{
				dataOutputStream.writeUTF("Test");
			} catch (IOException e)
			{
				System.out.println(e.getMessage());
				break;
			}
		}
	}
}
