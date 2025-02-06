package com.example.sql;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class InsertData {
	public static void insert(JSONArray database,Scanner in) {
		System.out.println("Enter the table name");
		String tableName=in.nextLine();
		int flag=0;
		
		for(int i=0;i<database.length();i++) {
			JSONObject table=database.getJSONObject(i);
			if(table.has(tableName)) {
				flag=1;
				JSONArray newRow=new JSONArray();
				JSONArray tabledata=table.getJSONArray(tableName);
				JSONArray tablecolumns=tabledata.getJSONArray(0);
				System.out.println("Enter the values for table "+tablecolumns);
				for(int j=0;j<tablecolumns.length();j++) {
					System.out.print("Enter the value for "+tablecolumns.getString(j)+" ");
					String val=in.nextLine();
					newRow.put(val);
				}
				tabledata.put(newRow);
                System.out.println("Data inserted successfully!");
                break;
			}
		}
		if(flag==0) {
			System.out.println("Table Not found");
		}
		
	}
}
