package com.server.session;

// static class
// has an initialize method
// creates a new file based off the given session name, and a timestamp
// has a record(String) method to record the data

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SessionManager
{
	private static String sessionName;
	private static String fileName;
	private static File file;

	public static void initialize(String sessionName)
	{
		SessionManager.sessionName = sessionName;

		// set up the file name
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM_dd_yyyy_HH_mm_ss");
		LocalDateTime time = LocalDateTime.now();
		fileName = sessionName + "_" + formatter.format(time) + ".txt";

		// create the file object
		file = new File(fileName);
		try
		{
			// create the new file
			file.createNewFile();
		} catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("File unable to be made.");
		}
	}

	public static void record(String recordString)
	{
		try
		{
			recordString += "\n";
			Files.write(Paths.get(fileName), recordString.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void close()
	{
	}
}
