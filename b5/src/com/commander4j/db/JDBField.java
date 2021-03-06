package com.commander4j.db;

/**
 * @author David Garratt
 * 
 * Project Name : Commander4j
 * 
 * Filename     : JDBField.java
 * 
 * Package Name : com.commander4j.db
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

/**
 * The JDBField class is used in conjunction with JDBStucture and JDBTable by
 * the Clone4j Utility to copy data from one schema to another.
 *
 *
 * @see com.commander4j.db.JDBStructure JDBStructure
 * @see com.commander4j.db.JDBTable JDBTable
 */
public class JDBField
{
	private String fieldName = "";
	private String fieldType = "";
	private int fieldSize = 0;

	public JDBField(String name, String type, int size)
	{
		fieldName = name;
		fieldType = type;
		fieldSize = size;
	}

	public String getfieldName()
	{
		return fieldName;
	}

	public String getfieldType()
	{
		return fieldType;
	}

	public int getfieldSize()
	{
		return fieldSize;

	}

	public String toString()
	{
		return fieldName;
	}
}
