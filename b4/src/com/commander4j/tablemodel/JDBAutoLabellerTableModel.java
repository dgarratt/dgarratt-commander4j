package com.commander4j.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import com.commander4j.db.JDBAutoLabeller;
import com.commander4j.db.JDBControl;
import com.commander4j.db.JDBLabelData;
import com.commander4j.sys.Common;

/**
 */
public class JDBAutoLabellerTableModel extends AbstractTableModel
{

	private static final long serialVersionUID = 1;
	public static final int Line_Col = 0;
	public static final int Group_Col = 1;
	public static final int Description_Col = 2;
	public static final int ProcessOrder_Col = 3;
	public static final int Material_Col = 4;
	public static final int Batch_Col = 5;
	public static final int Expiry_Col = 6;
	public static final int Modified_Col = 7;
	public static final int Use_SSCC_Range_Col = 8;
	public static final int SSCC_Prefix_Col = 9;
	public static final int SSCC_Range_Col = 10;

	private String ssccPrefix = "";
	private String hostID;
	private String sessionID;
    private JDBControl ctrl;
    private JDBLabelData labdata;
	private String[] mcolNames = { "Line","Group","Description", "Process Order", "Material", "Batch","Expiry Date","Modified","Use SSCC","SSCC Prefix","Sequence No" };
	private String lastUniqueFound = "";
	private ResultSet mResultSet;

	private int prowCount = -1;
	private HashMap<Integer,JDBAutoLabeller> cache = new HashMap<Integer,JDBAutoLabeller>();
	
	public JDBAutoLabellerTableModel(String host,String session)
	{
		setHostID(host);
		setSessionID(session);
		ctrl = new JDBControl(getHostID(),getSessionID());
		labdata = new JDBLabelData(getHostID(),getSessionID());
		ssccPrefix = ctrl.getKeyValue("SSCC PREFIX");
	}
	
	private String getSessionID() {
		return sessionID;
	}

	private String getHostID() {
		return hostID;
	}
	
	private void setHostID(String host) {
		hostID = host;
	}

	private void setSessionID(String session) {
		sessionID = session;
	}
	
	public void setResultSet(ResultSet rs)
	{
		
		try
		{
			cache.clear();
			rs.setFetchSize(20);
		}
		catch (SQLException e)
		{

			e.printStackTrace();
		}
		prowCount = -1;
		mResultSet = rs;
	}

	public int getColumnCount() {
		return mcolNames.length;
	}

	public int getRowCount() {
		try
		{
			if (prowCount <= 0)
			{
				mResultSet.last();
				prowCount = mResultSet.getRow();
				mResultSet.beforeFirst();
			}
			return prowCount;

		}
		catch (Exception e)
		{
			return 0;
		}
	}

	public void setValueAt(Object value, int row, int col) {

	}

	public String getColumnName(int col) {
		return mcolNames[col];
	}
    
	
	public Object getValueAt(int row, int col) {

		try
		{
			if (cache.containsKey(row)==false)
			{
				mResultSet.absolute(row + 1);
				final JDBAutoLabeller prow = new JDBAutoLabeller(Common.selectedHostID, Common.sessionID);
				prow.getPropertiesfromResultSet(mResultSet);
				cache.put(row, prow);
			}

			switch (col)
			{
			case Line_Col:
				return cache.get(row).getLine();
			case Group_Col:
				return cache.get(row).getGroup();
			case Description_Col:
				return cache.get(row).getDescription();
			case ProcessOrder_Col:
				if ( cache.get(row).getUniqueID().equals(lastUniqueFound)==false)
				{
					if (labdata.getProperties(cache.get(row).getUniqueID()))
					{
						lastUniqueFound = cache.get(row).getUniqueID();
						return labdata.getProcessOrder();
					}
					else
					{
						lastUniqueFound = "none";
						return "";
					}
				}
				else
				{
					return labdata.getProcessOrder();
				}
			case Material_Col:
				if ( cache.get(row).getUniqueID().equals(lastUniqueFound)==false)
				{
					if (labdata.getProperties(cache.get(row).getUniqueID()))
					{
						lastUniqueFound = cache.get(row).getUniqueID();
						return labdata.getMaterial();
					}
					else
					{
						lastUniqueFound = "none";
						return "";
					}
				}
				else
				{
					return labdata.getMaterial();
				}		
			case Batch_Col:
				if ( cache.get(row).getUniqueID().equals(lastUniqueFound)==false)
				{
					if (labdata.getProperties(cache.get(row).getUniqueID()))
					{
						lastUniqueFound = cache.get(row).getUniqueID();
						return labdata.getBatchNumber();
					}
					else
					{
						lastUniqueFound = "none";
						return "";
					}
				}
				else
				{
					return labdata.getBatchNumber();
				}		
			case Expiry_Col:
				if ( cache.get(row).getUniqueID().equals(lastUniqueFound)==false)
				{
					if (labdata.getProperties(cache.get(row).getUniqueID()))
					{
						lastUniqueFound = cache.get(row).getUniqueID();
						return labdata.getExpirtDate().toString().substring(0, 16);
					}
					else
					{
						lastUniqueFound = "none";
						return "";
					}
				}
				else
				{
					return labdata.getExpirtDate().toString().substring(0, 16);
				}					
			case SSCC_Prefix_Col:
				return ssccPrefix;
			case Use_SSCC_Range_Col:
				return cache.get(row).isSSCCRangeEnabled();
			case SSCC_Range_Col:
				return cache.get(row).getSSCCSequence().toString();
			case Modified_Col:
				return cache.get(row).isModified();
			}


		}
		catch (Exception ex)
		{
			return "Error";
		}

		return new String();
	}
}
