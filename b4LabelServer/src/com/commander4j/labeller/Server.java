package com.commander4j.labeller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.commander4j.xml.JXMLDocument;

public class Server extends Thread
{

	LabellerUtility utils = new LabellerUtility();
	public boolean started = false;
	public boolean shutdown = false;
	HashMap<String, Labeller> labellers = new HashMap<String, Labeller>();

	private void requestStop()
	{
		System.out.println("Server - requestStop");
		stopAllLabellers();
	}

	public void run()
	{
		System.out.println("Server - Run");
		startupInterface();
		started = true;
		while ((shutdown==false) && (started==true))
		{
			utils.pause(100);
		}

	}

	public int stop(int exitCode)
	{
		System.out.println("Server - stop");
		try
		{
			requestStop();
			utils.pause(100);
			started = false;

		} catch (Exception ex)
		{
		}

		return exitCode;
	}

	public void addLabeller(LabellerProperties prop, String script)
	{
		System.out.println("Server - addLabeller [" + prop.getId() + "]");
		Labeller labeller = new Labeller(prop, script);
		labellers.put(prop.getId(), labeller);
		// labellers.get(prop.getId()).start();
	}

	public void startLabeller(String id)
	{
		System.out.println("Server - startLabeller [" + id + "]");
		labellers.get(id).start();
		while (labellers.get(id).isAlive() == false)
		{
			System.out.println("Server - waiting for Labeller  [" + id + "] thread to start.");
			utils.pause(10);
		}
		System.out.println("Server - Labeller  [" + id + "] thread started.");
	}

	public void startAllLabellers()
	{
		System.out.println("Server - startAllLabellers");
		Iterator<Entry<String, Labeller>> it = labellers.entrySet().iterator();
		while (it.hasNext())
		{
			HashMap.Entry<String, Labeller> pair = (HashMap.Entry<String, Labeller>) it.next();
			startLabeller(pair.getKey().toString());
		}
	}

	public void stopLabeller(String id)
	{
		System.out.println("Server - stopLabeller - [" + id + "]");
		if (labellers.get(id).isAlive())
		{
			labellers.get(id).shutdown();

			while (labellers.get(id).isAlive())
			{
				utils.pause(10);
			}
		}
	}

	public void stopAllLabellers()
	{
		System.out.println("Server - stopAllLabellers");
		Iterator<Entry<String, Labeller>> it = labellers.entrySet().iterator();
		while (it.hasNext())
		{
			HashMap.Entry<String, Labeller> pair = (HashMap.Entry<String, Labeller>) it.next();
			stopLabeller(pair.getKey().toString());

			it.remove();
		}
	}

	public void deleteLabeller(String id)
	{
		if (labellers.get(id).isAlive() == false)
		{
			labellers.remove(id);
		}
	}

	public void startupInterface()
	{
		System.out.println("Server - startupInterface");
		loadLabellers();
		startAllLabellers();
	}

	public void requestPrint(String id)
	{
		if (labellers.containsKey(id))
		{
			if (labellers.get(id).isAlive())
			{
				System.out.println("Server - requestPrint - ["+id+"]");
				labellers.get(id).requestPrint();
			}
			else
			{
				System.out.println("Server - requestPrint - Labeller Thread not running for ["+id+"]");
			}
		}
		else
		{
			System.out.println("Server - requestPrint - Labeller Thread not loaded for ["+id+"]");
		}
	}

	public void loadLabellers()
	{
		System.out.println("Server - loadLabellers");

		JXMLDocument xmltest = new JXMLDocument("./xml/config/labellers.xml");

		int labeller = 1;

		while (xmltest.findXPath("//labellers/labeller[" + String.valueOf(labeller) + "]/@id").trim().equals("") == false)
		{
			String id = xmltest.findXPath("//labellers/labeller[" + String.valueOf(labeller) + "]/@id").trim();
			String enabled = xmltest.findXPath("//labellers/labeller[" + String.valueOf(labeller) + "]/enabled").trim();
			String ipAddress = xmltest.findXPath("//labellers/labeller[" + String.valueOf(labeller) + "]/ipAddres").trim();
			String portNumber = xmltest.findXPath("//labellers/labeller[" + String.valueOf(labeller) + "]/portNumber").trim();
			String commandFile = xmltest.findXPath("//labellers/labeller[" + String.valueOf(labeller) + "]/commandFile").trim();
			String inputPath = xmltest.findXPath("//labellers/labeller[" + String.valueOf(labeller) + "]/inputPath").trim();
			String inputFile = xmltest.findXPath("//labellers/labeller[" + String.valueOf(labeller) + "]/inputFile").trim();
			System.out.println(id);

			if (enabled.toUpperCase().equals("Y"))
			{

				LabellerProperties prop1 = new LabellerProperties();

				prop1.setId(id);
				prop1.setIpAddress(ipAddress);
				prop1.setPortNumber(Integer.valueOf(portNumber));
				prop1.setInputPath(inputPath);
				prop1.setInputFile(inputFile);
				addLabeller(prop1, commandFile);
			}

			labeller++;
		}

	}

	public static void main(String[] args)
	{

		System.out.println("Application starting");

		System.out.println("Server starting");
		System.out.println("");
		LabellerUtility utils = new LabellerUtility();
		Server server = new Server();

		// server.startupInterface();

		server.start();

		while (server.isAlive() == false)
		{
			utils.pause(10);
		}
		utils.pause(250);

		// consider adding loop here waiting for shutdown ****

		System.out.println(server);
		System.out.println(server.labellers);
		server.labellers.get("Labeller 1").requestPrint();

		while (server.labellers.get("Labeller 1").requestRunning() == true)
		{
			utils.pause(10);
		}

		server.requestStop();

		System.out.println("");
		System.out.println("Server finished.");
		System.exit(0);
	}

}