package com.example.sql;

import org.json.JSONArray;
import org.json.JSONObject;

public class Update {
	static int updatedRows=0;
    public static void update(JSONArray database, String query) {
        query = query.trim();
        int setIndex=query.toUpperCase().indexOf("SET");
        int whereIndex=query.toUpperCase().indexOf("WHERE");

        if(setIndex==-1) {
            System.out.println("SET condition is missing.");
            return;
        }

        String tableName=query.substring(6, setIndex).trim();
        String setPart=(whereIndex!=-1)?query.substring(setIndex+3, whereIndex).trim():query.substring(setIndex+3).trim();
        String wherePart=(whereIndex!=-1)?query.substring(whereIndex+5).trim():null;

        JSONObject targetTable=null;
        for(int i=0; i<database.length(); i++) {
            JSONObject table=database.getJSONObject(i);
            if(table.has(tableName)) {
                targetTable=table;
                break;
            }
        }

        if(targetTable==null) {
            System.out.println("Table not found.");
            return;
        }

        JSONArray tableData=targetTable.getJSONArray(tableName);
        JSONArray columns=tableData.getJSONArray(0);

        String[] setConditions=setPart.split(",");
        if(!validateConditions(setConditions, columns)) {
            System.out.println("Invalid SET conditions. Must be in column=value format.");
            return;
        }

        String[] whereConditions=(wherePart!=null)?wherePart.split("and"):new String[0];
        if(whereConditions.length>0 && !validateConditions(whereConditions, columns)) {
            System.out.println("Invalid WHERE conditions. Must be in column=value, column>value, or column<value format.");
            return;
        }

        
        for(int row=1; row<tableData.length(); row++) {
            JSONArray rowData=tableData.getJSONArray(row);
            boolean match=(whereConditions.length==0)||evaluateConditions(whereConditions, columns, rowData);

            if(match) {
                applySetConditions(database,tableName,setConditions, columns, rowData);
                updatedRows++;
            }
        }

        if(updatedRows>0) {
            System.out.println(updatedRows+" row(s) updated successfully.");
        } else {
            System.out.println("No rows matched the conditions || constraints may failed.");
        }
    }

    private static boolean validateConditions(String[] conditions, JSONArray columns) {
        for(String condition:conditions) {
            String operator=condition.contains("=")?"=":condition.contains("<")?"<":condition.contains(">")?">":null;
            if(operator==null) return false;

            String[] parts=condition.split(operator);
            if(parts.length!=2 || columns.toList().indexOf(parts[0].trim())==-1) return false;
        }
        return true;
    }

    private static boolean evaluateConditions(String[] whereConditions, JSONArray columns, JSONArray rowData) {
        for(String condition:whereConditions) {
            String operator=condition.contains("=")?"=":condition.contains("<")?"<":">";
            String[] parts=condition.split(operator);
            String column=parts[0].trim();
            String value=parts[1].trim();

            int colIndex=columns.toList().indexOf(column);
            if(colIndex==-1) return false;

            String rowValue=rowData.getString(colIndex);
            if(operator.equals("=") && !rowValue.equals(value)) return false;
            if(operator.equals(">") && !(Integer.parseInt(rowValue)>Integer.parseInt(value))) return false;
            if(operator.equals("<") && !(Integer.parseInt(rowValue)<Integer.parseInt(value))) return false;
        }
        return true;
    }

    private static void applySetConditions(JSONArray database ,String tableName,String[] setConditions, JSONArray columns, JSONArray rowData) {
        for(String condition:setConditions) {
            String[] parts=condition.split("=");
            String column=parts[0].trim();
            String value=parts[1].trim();

            int colIndex=columns.toList().indexOf(column);
            if(colIndex!=-1) {
            	if((InsertData.isPrimary(database,column,tableName+"schema")||InsertData.isUnique(database,column,tableName+"schema")) && PrimaryKeyCheck.primaryCheck(database, tableName, value, column)==false) {
            		updatedRows--;
            		return;
            	}
            	rowData.put(colIndex, value);
            }
        }
    }
}