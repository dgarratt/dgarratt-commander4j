package com.commander4j.Interface.Mapping;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.commander4j.Interface.Inbound.InboundInterface;
import com.commander4j.Interface.Outbound.OutboundInterface;
import com.commander4j.util.Utility;

public class Map {
	boolean enabled = true;
	InboundInterface inboundInterface;
	LinkedList<OutboundInterface> outboundInterface = new LinkedList<OutboundInterface>();
	Logger logger = Logger.getLogger(Map.class);
	private String id="";
	private String description="";
	private Long inboundMapCount = (long) 0;
	private Long outboundMapCount = (long) 0;
	
	public void run()
	{

	}
	
	public Long getInboundMapCount()
	{
		return inboundMapCount;
	}
	
	public Long getOutboundMapCount()
	{
		return outboundMapCount;
	}	
	
	public String toString()
	{
		return Utility.padString(getId(),true,10," ") + "  " + Utility.padString(getDescription(),true,30," ") + "  "+Utility.padString(getInboundInterface().getType(),true,3," ") + "  ["+getInboundInterface().getInputPath()+"]";
	}
	
	public void setId(String ID)
	{
		id = ID;
	}
	
	public void setDescription(String desc)
	{
		description = desc;
	}

	public String getId()
	{
		return id;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setEnabled(boolean yesno)
	{
		logger.debug("setEnabled "+String.valueOf(yesno));
		if (yesno==true)
		{
			for (int x = 0; x < outboundInterface.size(); x++)
			{
				outboundInterface.get(x).setEnabled(yesno);
			}
			this.inboundInterface.setEnabled(yesno);
		} else
		{
			this.inboundInterface.setEnabled(yesno);
			for (int x = 0; x < outboundInterface.size(); x++)
			{
				outboundInterface.get(x).setEnabled(yesno);
			}
		}

	}
	
	public InboundInterface getInboundInterface()
	{
		return this.inboundInterface;
	}

	public void setInboundInterface(InboundInterface inint)
	{
		this.inboundInterface = inint;
	}

	public void addOutboundInterface(OutboundInterface outint)
	{
		outboundInterface.addLast(outint);
	}

	public void processMapToOutboundInterface(String filename, OutboundInterface outint,Document data)
	{
		logger.debug(">> processMapToOutboundInterface [" + filename + " - " +  Utility.getStringFromDocument(data)+"]");
		outboundMapCount++;
		logger.debug(">> outboundMapMessages count [" + getOutboundMapCount().toString()+"]");
		outint.processInterfaceToConnector(filename,data);
	}
	
	public void processInboundInterfaceToMap(String filename,Document data)
	{

		logger.debug("<< processInboundInterfaceToMap  [" + filename + " - " + Utility.getStringFromDocument(data)+"]");
		inboundMapCount++;
		logger.debug(">> inboundMapMessages count [" + getInboundMapCount().toString()+"]");
		for (int x = 0; x < outboundInterface.size(); x++)
		{
			processMapToOutboundInterface(filename,outboundInterface.get(x),data);

		}

	}

}
