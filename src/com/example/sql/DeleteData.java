package com.example.sql;

import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class DeleteData {
    public static void delete(JSONArray database, Scanner in) {
        System.out.print("Enter the table name");
        String tableName = in.nextLine();
        boolean flag=false;
        
        for(int i=0;i<database.length();i++) {
        	JSONObject table=database.getJSONObject(i);
        	if(table.has(tableName)) {
        		flag=true;
        		JSONArray Data = table.getJSONArray(tableName);
        		JSONArray columns=Data.getJSONArray(0);
        		System.out.println("Available columns "+columns);
        		System.out.println("Enter the where condition if multiple means seperate by comma ");
        		String where=in.nextLine();
        		String conditions[]=where.trim().split(",");
        		boolean validConditions = true;
                JSONArray validRows = new JSONArray();
                validRows.put(columns);
                for (String condition : conditions) {
                    String[] parts=condition.split("=");
                    if (parts.length != 2) {
                        System.out.println("Invalid condition format. Must be column=value");
                        validConditions = false;
                        break;
                    }
                }

                if (!validConditions) {
                    return;
                }
                
                for(int j=1;j<Data.length();j++) {
                	JSONArray rowData=Data.getJSONArray(j);
                	boolean match=true;
                	for(String condition:conditions) {
                		String conPart[]=condition.split("=");
                		String whereCol=conPart[0].trim(),whereVal=conPart[1].trim();
                		
                		int whereInd=columns.toList().indexOf(whereCol);
                        if (whereInd==-1 || !rowData.getString(whereInd).equals(whereVal)){
                        	match=false;
                        	break;
                        }	
                	}
                	if(!match) {
                		validRows.put(rowData);
                	}
                }
                table.put(tableName,validRows);
                int DeletedCount=Data.length()-validRows.length();
                if(DeletedCount>0) {
                	System.out.println(DeletedCount+" Rows Deleted");
                }
                else {
                	System.out.println("No Rows Deleted");
                	
                }
        		
        		
        	}
        }
        if(!flag) {
        	System.out.println("Table Not Found");
        }
        

    }
  }