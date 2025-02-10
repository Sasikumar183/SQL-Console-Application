package com.example.sql;
import org.json.JSONArray;
import org.json.JSONObject;

public class Drop {
	public static void drop(JSONArray database,String query) {
		String tableName=query.substring(10).trim();
		boolean flag=false;
		for(int i=database.length()-1;i>=0;i--) {
			JSONObject table=database.getJSONObject(i);
			if(table.has(tableName)||table.has(tableName+"schema")) {
				flag=true;
				database.remove(i);
			}
		}
		
		if(flag==false)
			System.out.println("Table not found");
		else
			System.out.println("table dropped");

	}
}