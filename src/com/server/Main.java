package com.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main
{
    public static void main(String[] args)
    {
        InetAddress address = null;
        try
        {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        Server server = new Server(15, address, 32);
        server.run();
    }
}
