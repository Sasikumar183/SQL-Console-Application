package com.example.sql;

import org.json.JSONArray;
import org.json.JSONObject;

public class ShowTable {
	public static void showTable(JSONArray database,String query) {
		if(database.length()==0) {
    		System.out.println("No Table Available");
    		return;
    	}
    	else {
        	for(int i=0;i<database.length();i++) {
        		JSONObject obj=database.getJSONObject(i);
        		String table=obj.keySet().toString();
        		if(table.length()>6) {
        			if(table.substring(table.length()-7,table.length()-1).equals("schema"))
        				continue;
        			else
        				System.out.println(table.substring(1,table.length()-1));
        		}
        		else
    				System.out.println(table.substring(1,table.length()-1));
        	}
    	}	
	}
}
