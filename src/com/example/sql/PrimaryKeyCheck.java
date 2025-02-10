package com.example.sql;
import org.json.JSONArray;
import org.json.JSONObject;

public class PrimaryKeyCheck {
	public static boolean primaryCheck(JSONArray database,String tableName,String colValue,String colName){
		for(int i=0;i<database.length();i++) {
			JSONObject table=database.getJSONObject(i);
			if (table.has(tableName)) {
                JSONArray tableData=table.getJSONArray(tableName);
                JSONArray firstRow=tableData.getJSONArray(0);
                int index=firstRow.toList().indexOf(colName);
                for(int j=1;j<tableData.length();j++) {
                	if(tableData.getJSONArray(j).getString(index).equals(colValue)) {
                		System.out.println("Primary Key or Unique constraint on column "+colName);
                		return false;
                	}
                }
		}
		
		
	}
		return true;
}
	
}