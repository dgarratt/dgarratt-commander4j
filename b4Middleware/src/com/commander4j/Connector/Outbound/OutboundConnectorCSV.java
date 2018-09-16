package com.commander4j.Connector.Outbound;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import com.commander4j.Interface.Outbound.OutboundInterface;
import com.commander4j.sys.Common;
import com.commander4j.util.JXMLDocument;
import com.commander4j.util.Utility;
import com.opencsv.CSVWriter;

import ABSTRACT.com.commander4j.Connector.OutboundConnectorABSTRACT;

public class OutboundConnectorCSV extends OutboundConnectorABSTRACT
{

	Logger logger = org.apache.logging.log4j.LogManager.getLogger((OutboundConnectorCSV.class));
	private LinkedList<String> parseOptions = new LinkedList<String>();
	boolean disableQuotes = false;
	char seperator = ',';
	char quote = '"';

	public OutboundConnectorCSV(OutboundInterface inter)
	{
		super(Connector_CSV, inter);
	}

	private void getCSVOptions()
	{
		String options = getOutboundInterface().getCSVOptions();
		String delimeter = getOutboundInterface().getOptionDelimeter();
		parsePattern(options, delimeter);
	}

	private void parsePattern(String pattern, String delim)
	{
		parseOptions.clear();
		delim = "\\" + delim;
		String[] opts = pattern.split(delim);
		for (int x = 0; x < opts.length; x++)
		{
			String two = opts[x];
			String[] three = two.split("=");

			String opt = three[0];
			String val = three[1];

			switch (opt)
			{
			case "separator":
				seperator = val.charAt(0);
				break;
			case "quote":
				if (val.equals("none"))
				{
					disableQuotes = true;
				}
				else
				{
					quote = val.charAt(0);
					disableQuotes = false;
				}
				break;
			default:
				opt = "";
				break;
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean connectorSave(String path,String filename)
	{
		boolean result = false;
		String fullPath = path+File.separator+filename;
		
		fullPath = fullPath + "." + getOutboundInterface().getOutputFileExtension().toLowerCase();

/*		if (fullPath.endsWith("." + getType().toLowerCase()) == false)
		{
			fullPath = fullPath + "." + getType().toLowerCase();
		}*/
		String tempFilename = fullPath + ".tmp";
		String finalFilename = fullPath;		
		
		getCSVOptions();

		logger.debug("connectorSave [" + fullPath + "]");

		JXMLDocument document = new JXMLDocument();
		document.setDocument(getData());

		String cl = Utility.replaceNullStringwithBlank(document.findXPath("//data/@cols").trim());
		if (cl.equals(""))
		{
			cl = "0";
		}
		int columns = Integer.valueOf(cl);

		String rw = Utility.replaceNullStringwithBlank(document.findXPath("//data/@rows").trim());
		if (rw.equals(""))
		{
			rw = "0";
		}
		int rows = Integer.valueOf(rw);

		try
		{
			System.out.println(document.documentToString(getData()));
		} catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (rows > 0)
		{
			if (columns > 0)
			{
				try
				{
					CSVWriter writer;
					if (disableQuotes)
					{
						writer = new CSVWriter(new FileWriter(tempFilename), seperator, CSVWriter.NO_QUOTE_CHARACTER);
					} else
					{
						writer = new CSVWriter(new FileWriter(tempFilename), seperator,quote);
					}

					String[] csvrow = new String[columns];
					for (int r = 1; r <= rows; r++)
					{

						for (int c = 1; c <= columns; c++)
						{
							String xpath = "//data/row[@id='" + String.valueOf(r) + "']/col[@id='" + String.valueOf(c) + "']";
							String dataString = Utility.replaceNullStringwithBlank(document.findXPath(xpath).trim());
							csvrow[c - 1] = dataString;
							//logger.debug("row=[" + String.valueOf(r) + "] col=[" + String.valueOf(c) + "] data=[" + dataString + "]");
						}

						writer.writeNext(csvrow);

					}
					writer.close();
					
					FileUtils.moveFile(new File(tempFilename), new File(finalFilename));
					
					result=true;
				} catch (Exception ex)
				{
					logger.error(ex.getMessage());
					Common.emailqueue.addToQueue("Error", "Error Writing File ["+fullPath+"]", ex.getMessage()+"\n\n", "");
				}

			}
		}

		document = null;

		return result;
	}

}
