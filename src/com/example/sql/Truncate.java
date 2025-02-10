package com.example.sql;
import org.json.JSONArray;
import org.json.JSONObject;

public class Truncate {

	public static void truncate(JSONArray database,String query) {
		String tableName=query.substring(14).trim();
		boolean flag=false;
		
		for(int i=0;i<database.length();i++) {
			JSONObject table=database.getJSONObject(i);
			JSONArray tableData=table.getJSONArray(tableName);
			if(table.has(tableName)) {
				flag=true;
				for(int j=tableData.length()-1;j>0;j--) {
					tableData.remove(j);
				}
				System.out.println("Table Truncated");
				break;
			}
			
		}
		
		if(flag==false) {
			System.out.println("Table not found");
		}
	
	}
}
