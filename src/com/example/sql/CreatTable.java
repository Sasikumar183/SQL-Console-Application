package com.example.sql;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class CreatTable {

	public static void createTable(JSONArray database,Scanner in) {
		System.out.print("Enter the table name: ");
		String tableName=in.nextLine();
		
		System.out.println("Enter the columns you want to add (by comma separated");
		String[] columnName = in.nextLine().split(",");
		JSONArray columns = new JSONArray();
		
        for (String column : columnName) {
            columns.put(column.trim());
        }
		JSONArray tableData= new JSONArray();
		tableData.put(columns);
		JSONObject table = new JSONObject();
        table.put(tableName, tableData);

        database.put(table);
        System.out.println("Table created");
	}
	
}
