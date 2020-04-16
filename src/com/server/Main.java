package com.server;

import com.server.serverprocess.Server;

import java.net.InetAddress;
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
        Server server = new Server(1500, address, 32);
        new Thread(server).start();
    }
}
