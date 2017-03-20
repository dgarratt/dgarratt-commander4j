package com.commander4j.messages;

/**
 * @author David Garratt
 * 
 * Project Name : Commander4j
 * 
 * Filename     : IncommingJourney.java
 * 
 * Package Name : com.commander4j.messages
 * 
 * License      : GNU General Public License
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * http://www.commander4j.com/website/license.html.
 * 
 */

import java.sql.Timestamp;

import com.commander4j.db.JDBDespatch;
import com.commander4j.db.JDBJourney;
import com.commander4j.util.JUtility;

public class IncommingJourney
{

	private String hostID;
	private String sessionID;
	private String errorMessage;

	public String getErrorMessage()
	{
		return errorMessage;
	}

	private void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public String getHostID()
	{
		return hostID;
	}

	public void setHostID(String hostID)
	{
		this.hostID = hostID;
	}

	public String getSessionID()
	{
		return sessionID;
	}

	public void setSessionID(String sessionID)
	{
		this.sessionID = sessionID;
	}

	public IncommingJourney(String host, String session)
	{
		setSessionID(session);
		setHostID(host);
	}

	public Boolean processMessage(GenericMessageHeader gmh)
	{
		Boolean result = false;

		JDBJourney journey = new JDBJourney(getHostID(), getSessionID());
		JDBDespatch despatch  = new JDBDespatch(getHostID(), getSessionID());
		
		String ref = "12345";
		String action = "";
		String timestampString = "";
		String location_to = "";
		String loadType = "";
		String loadTypeDesc = "";
		String haulier = "";
		Timestamp timeslot;
		int occur = 1;
		int created = 0;
		int deleted = 0;
		int updated = 0;

		while (ref.length() > 0)

		{
			ref = JUtility.replaceNullStringwithBlank(gmh.getXMLDocument().findXPath("//message/messageData/journeyDefinition[" + String.valueOf(occur) + "]/ref").trim());
			action = JUtility.replaceNullStringwithBlank(gmh.getXMLDocument().findXPath("//message/messageData/journeyDefinition[" + String.valueOf(occur) + "]/action").trim()).toLowerCase();
		    timestampString = gmh.getXMLDocument().findXPath("//message/messageData/journeyDefinition[" + String.valueOf(occur) + "]/timeslot").trim();			
			timeslot = JUtility.getTimeStampFromISOString(timestampString);	
			location_to = JUtility.replaceNullStringwithBlank(gmh.getXMLDocument().findXPath("//message/messageData/journeyDefinition[" + String.valueOf(occur) + "]/destination").trim());
			loadType = JUtility.replaceNullStringwithBlank(gmh.getXMLDocument().findXPath("//message/messageData/journeyDefinition[" + String.valueOf(occur) + "]/loadType").trim());
			loadTypeDesc = JUtility.replaceNullStringwithBlank(gmh.getXMLDocument().findXPath("//message/messageData/journeyDefinition[" + String.valueOf(occur) + "]/loadTypeDesc").trim());
			haulier = JUtility.replaceNullStringwithBlank(gmh.getXMLDocument().findXPath("//message/messageData/journeyDefinition[" + String.valueOf(occur) + "]/haulier").trim());
			
			if (ref.length() > 0)
			{

				if (action.equals("create"))
				{
					if (journey.getJourneyRefProperties(ref) == false)
					{
						journey.setJourneyRef(ref);
						journey.setStatus("Unassigned");
						journey.setTimeslot(timeslot);
						journey.setLocationTo(location_to);
						journey.setLoadType(loadType);
						journey.setLoadTypeDesc(loadTypeDesc);
						journey.setHaulier(haulier);
						result = journey.create();
						if (result)
						{
							created++;
						}
					} else
					{
						result = true;
					}
				}

				if (action.equals("delete"))
				{
					if (journey.getJourneyRefProperties(ref) == true)
					{
						if (journey.getStatus().equals("Unassigned"))
						{
							journey.setJourneyRef(ref);
							journey.delete();
							result = true;
							deleted++;
						}
						else
						{
							if (despatch.getDespatchProperties(journey.getDespatchNo()))
							{
								if (despatch.getStatus().equals("Unconfirmed"))
								{
									despatch.setJourneyRef("");
									despatch.update();
									
									journey.setJourneyRef(ref);
									journey.delete();
									result = true;
									deleted++;
								}
							}
						}
					}
				}

				if (action.equals("update"))
				{
					if (journey.getJourneyRefProperties(ref) == true)
					{
						if (journey.getStatus().equals("Unassigned"))
						{
							journey.setJourneyRef(ref);
							journey.setTimeslot(timeslot);
							journey.setLocationTo(location_to);
							journey.setLoadType(loadType);
							journey.setLoadTypeDesc(loadTypeDesc);
							journey.setHaulier(haulier);
							journey.update();
							result = true;
							updated++;
						}
						else
						{
							if (despatch.getDespatchProperties(journey.getDespatchNo()))
							{
								if (despatch.getStatus().equals("Unconfirmed"))
								{
									journey.setJourneyRef(ref);
									journey.setTimeslot(timeslot);
									journey.setLocationTo(location_to);
									journey.setLoadType(loadType);
									journey.setLoadTypeDesc(loadTypeDesc);
									journey.setHaulier(haulier);
									journey.update();
									result = true;
									updated++;
								}
							}
						}
					}
				}
				
				occur++;
			}
		}
		setErrorMessage(
				String.valueOf(created) + " Journey(s) created, " + String.valueOf(updated) + " Journey(s) updated, " +String.valueOf(deleted) + " Journey(s) deleted");

		journey = null;
		return result;

	}
}
