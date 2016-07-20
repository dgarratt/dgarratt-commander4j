package com.commander4j.db;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.commander4j.sys.Common;

/**
 */
public class JDBQuery2
{

	public static void closeStatement(PreparedStatement stmt)
	{
		try
		{
			stmt.close();
		} catch (Exception e)
		{

		}
	}

	public String sqltext;
	private Integer criteriaCount;
	private Vector<Object> params = new Vector<Object>();
	private Integer numberofparams;
	private PreparedStatement prepStatement;
	private String dbErrorMessage;
	private String hostID;
	private String sessionID;
	private String sortFields = "";
	private String sortDirection = "";
	private final Logger logger = Logger.getLogger(JDBQuery2.class);
	private String selectLimitType = "";
	private String sqlTemplate = "";
	private String sqlFrom = "";
	private String sqlWhat = "";
	private String sqlRestriction = "";
	private String sqlWhere = "";
	private Object sqlLimit = "";
	private String sqlSort = "";

	public JDBQuery2(String host, String session)
	{
		clear();
		setHostID(host);
		setSessionID(session);
		selectLimitType = Common.hostList.getHost(host).getDatabaseParameters().getjdbcDatabaseSelectLimit().toUpperCase();
		if (selectLimitType.equals("TOP"))
		{
			sqlTemplate = "SELECT {restriction} {what} FROM {source} {where} {orderby}";
		}
		if (selectLimitType.equals("ROWNUM"))
		{
			sqlTemplate = "SELECT {what} FROM {source} {where} {restriction} {orderby}";
		}
		if (selectLimitType.equals("LIMIT"))
		{
			sqlTemplate = "SELECT {what} FROM {source} {where} {orderby} {restriction}";
		}
		sqltext = sqlTemplate;

	}

	public void applyFrom(String from)
	{
		sqlFrom = from.toUpperCase();
	}

	private void applyParameter(Object param)
	{
		params.add(param);
	}

	public void applyRestriction(boolean active, Object limit)
	{
		sqlLimit = limit.toString();
		if (active)
		{
			if (selectLimitType.equals("TOP"))
			{
				sqlRestriction = "TOP " + String.valueOf(limit);
			}

			if (selectLimitType.equals("LIMIT"))
			{
				sqlRestriction = "LIMIT " + String.valueOf(limit);
			}

			if (selectLimitType.equals("ROWNUM"))
			{
				sqlRestriction = selectLimitType;
			}
		} else
		{
			sqlRestriction = "";
		}
	}

	public void applySort(String fields, Boolean direction)
	{
		if (direction)
		{
			applySort(fields, "DESC");
		} else
		{
			applySort(fields, "ASC");
		}
	}

	public void applySort(String fields, String direction)
	{
		
		setSortFields(fields.toUpperCase());
		setSortDirection(direction.toUpperCase());
		
		sqlSort = "ORDER BY " + getSortFields().replace(","," "+ getSortDirection()+",") + " " + getSortDirection();

	}

	public void applyWhat(String what)
	{
		sqlWhat = what.toUpperCase();
	}

	public void applyWhere(String field, Object param)
	{
		field = field.toUpperCase();
		if (field != null)
		{
			if (field.equals("") == false)
			{
				if (param != null)
				{
					if (param.toString().equals("") == false)
					{
						criteriaCount++;

						if ((criteriaCount == 1) & (sqlFrom.contains(" WHERE ") == false))
						{
							sqlWhere = "WHERE ";
						} else
						{
							sqlWhere = sqlWhere + " AND ";
						}
						sqlWhere = sqlWhere + field + "?";

						applyParameter(param);
					}
				}
			}
		}
	}

	private void bindParam(Integer pos, Object obj)
	{
		if (obj != null)
		{
			try
			{
				if (obj.getClass().equals(java.lang.Character.class))
				{
					prepStatement.setString(pos, obj.toString());
				}
				if (obj.getClass().equals(String.class))
				{
					prepStatement.setString(pos, (String) obj);
				}
				if (obj.getClass().equals(Integer.class))
				{
					prepStatement.setInt(pos, (Integer) obj);
				}
				if (obj.getClass().equals(Double.class))
				{
					prepStatement.setDouble(pos, (Double) obj);
				}
				if (obj.getClass().equals(Float.class))
				{
					prepStatement.setFloat(pos, (Float) obj);
				}
				if (obj.getClass().equals(Timestamp.class))
				{
					prepStatement.setTimestamp(pos, (Timestamp) obj);
				}
				if (obj.getClass().equals(BigDecimal.class))
				{
					prepStatement.setBigDecimal(pos, (BigDecimal) obj);
				}
				if (obj.getClass().equals(java.sql.Date.class))
				{
					prepStatement.setDate(pos, (java.sql.Date) obj);
				}
				if (obj.getClass().equals(Long.class))
				{
					prepStatement.setLong(pos, (Long) obj);
				}
			} catch (SQLException e)
			{
				setErrorMessage(e.getMessage());
			}
		}

	}

	private void bindParams()
	{

		numberofparams = params.size();
		try
		{

			prepStatement = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(sqltext, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			prepStatement.setFetchSize(25);

			for (int i = 0; i < numberofparams; i++)
			{
				bindParam(i + 1, params.elementAt(i));
			}
		} catch (Exception ex)
		{
			setErrorMessage(ex.getMessage());
		}

	}

	public void clear()
	{
		selectLimitType = "";
		sqlTemplate = "";
		sqlFrom = "";
		sqlWhat = "";
		sqlRestriction = "";
		sqlWhere = "";
		sqlLimit = "";
		sqlSort = "";
		sqltext = "";
		criteriaCount = 0;
		params.clear();
	}

	public void applySQL()
	{
		sqltext = sqltext.replace("{what}", sqlWhat);
		sqltext = sqltext.replace("{source}", sqlFrom);
		if (sqlRestriction.equals("") == false)
		{
			if (selectLimitType.equals("ROWNUM"))
			{
				applyWhere("ROWNUM<=", sqlLimit);
				sqltext = sqltext.replace("{restriction}", "");
			} else
			{
				sqltext = sqltext.replace("{restriction}", sqlRestriction);
			}
		}
		else
		{
			sqltext = sqltext.replace("{restriction}", sqlRestriction);
		}
		sqltext = sqltext.replace("{where}", sqlWhere);
		sqltext = sqltext.replace("{orderby}", sqlSort);
		sqltext = sqltext.replace("{SCHEMA}", Common.hostList.getHost(getHostID()).getDatabaseParameters().getjdbcDatabaseSchema());
		bindParams();
		logger.debug(sqltext);
	}

	public Integer getCriteriaCount()
	{
		return criteriaCount;
	}

	public String getErrorMessage()
	{
		return dbErrorMessage;
	}

	private String getHostID()
	{
		return hostID;
	}

	public PreparedStatement getPreparedStatement()
	{
		return prepStatement;
	}

	private String getSessionID()
	{
		return sessionID;
	}

	private String getSortDirection()
	{
		return sortDirection;
	}

	private String getSortFields()
	{
		return sortFields;
	}

	public String getSqlText()
	{
		return sqltext;
	}

	public void setCriterialCount(Integer cnt)
	{
		criteriaCount = cnt;
	}

	private void setErrorMessage(String errorMsg)
	{
		if (errorMsg.isEmpty() == false)
		{
		}
		dbErrorMessage = errorMsg;
	}

	private void setHostID(String host)
	{
		hostID = host;
	}

	private void setSessionID(String session)
	{
		sessionID = session;
	}

	private void setSortDirection(String direction)
	{
		sortDirection = direction;
	}

	private void setSortFields(String fields)
	{
		sortFields = fields;
	}

	public void setSqlText(String txt)
	{
		sqltext = txt;
	}

}
