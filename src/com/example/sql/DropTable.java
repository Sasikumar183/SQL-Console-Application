package com.example.sql;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class DropTable {
	
	public static void drop(JSONArray database,Scanner in) {
		System.out.println("Enter the table name to drop");
		String tableName=in.nextLine();
		boolean flag=false;
		for(int i=0;i<database.length();i++) {
			JSONObject table=database.getJSONObject(i);
			if(table.has(tableName)) {
				flag=true;
				database.remove(i);
				System.out.println("Table dropped");
				break;
			}
		}
		if(flag==false)
			System.out.println("Table not found");
		
	}

}
