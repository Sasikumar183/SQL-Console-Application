
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InsertData {
    private static int increval=1;
	public static void insert(JSONArray database, String query) {
        query = query.substring(11).trim();
        int valInd = query.toUpperCase().indexOf("VALUES");
        if (valInd == -1) {
            System.out.println("Check the syntax");
            return;
        }

        String tablePart=query.substring(0, valInd).trim();
        int colStart=tablePart.indexOf('(');
        int colEnd=tablePart.indexOf(')');

        String tableName;
        String[] columns=null;

        if (colStart !=-1 && colEnd != -1 && colStart < colEnd) {
            tableName = tablePart.substring(0, colStart).trim();
            columns = tablePart.substring(colStart + 1, colEnd).split(",");
        } else {
            tableName = tablePart;
        }

        int valStart = query.indexOf('(', valInd);
        int valEnd = query.indexOf(')', valInd);
        if (valStart == -1 || valEnd == -1 || valStart > valEnd) {
            System.out.println("Check the syntax");
            return;
        }

        String[] values = query.substring(valStart + 1, valEnd).split(",");
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim().replaceAll("'", "");
        }

        for (int i = 0; i < database.length(); i++) {
            JSONObject table = database.getJSONObject(i);
            if (table.has(tableName)) {
                JSONArray tableData=table.getJSONArray(tableName);
                JSONArray firstRow=tableData.getJSONArray(0);
                int colCount=firstRow.length();

                JSONArray rowData=new JSONArray();
                Map<String, String> colValueMap = new HashMap<>();

                if (columns != null) {//for insert into user(col1,col2..) values(val1,val2...)
                    for (int j = 0; j < columns.length; j++) {
                        colValueMap.put(columns[j].trim(),j<values.length?values[j]:"NULL");
                    }
                } else { //for insert into user values(val1,val2...)
                    for (int j=0;j<colCount;j++) {
                    	if(j>=values.length) {
                    		System.out.println("Enter the column values correctly");
                    		return;
                    	}
                        colValueMap.put(firstRow.getString(j), j<values.length?values[j]:"NULL");
                    }
                }

                for (int j=0;j<colCount;j++) {
                    String columnName=firstRow.getString(j);
                    int autoInc=isAutoInc(database,columnName,tableName+"schema");
                    boolean notNull=isNotNull(database,columnName,tableName+"schema");
                    boolean isPrimary=isPrimary(database,columnName,tableName+"schema");
                    boolean isUnique=isUnique(database,columnName,tableName+"schema");                    
                    if(autoInc!=-1 && colValueMap.getOrDefault(columnName,"NULL").equals("NULL")) {
                        rowData.put(String.valueOf(autoInc));                  
                    }
                    else if(isPrimary && PrimaryKeyCheck.primaryCheck(database, tableName, colValueMap.getOrDefault(columnName,"NULL"), columnName)==false) {
                    	rowData.put(colValueMap.getOrDefault(columnName,"NULL"));
                    	return;
                    }
                    else if(isUnique && PrimaryKeyCheck.primaryCheck(database, tableName, colValueMap.getOrDefault(columnName,"NULL"), columnName)==false) {
                    	rowData.put(colValueMap.getOrDefault(columnName,"NULL"));
                    	return;
                    }
                    else if(colValueMap.getOrDefault(columnName,"NULL").equals("NULL")&&notNull) {
                    	System.out.println("NOT NULL constraint failed for column "+columnName);
                    	return;
                    }
                    else {
                        rowData.put(colValueMap.getOrDefault(columnName,"NULL"));
                    }
                }


                tableData.put(rowData);
                increval++;
                System.out.println("Inserted into '" + tableName + "' successfully!");
                return;
            }
        }
        System.out.println("Table not found");
    }

	
    private static int isAutoInc(JSONArray database, String columnName,String tableName) {
    	int autoinc=-1;
    	for (int i = 0; i < database.length(); i++) {
            JSONObject table = database.getJSONObject(i);
            if (table.has(tableName)) {
                JSONArray tableData=table.getJSONArray(tableName);
                JSONArray firstRow=tableData.getJSONArray(0);
                for(int j=0;j<firstRow.length();j++) {
                    String curCol=firstRow.getString(j);
                    if(curCol.split(" ")[0].equals(columnName)) {
                        if(curCol.toLowerCase().indexOf("auto_increment") == -1) {
                            return -1;
                        } else {
                            return increval;
                        }
                    }
                }
            }
    	}
    	return autoinc;   
}
    private static boolean isNotNull(JSONArray database, String columnName, String tableName) {
        for (int i = 0; i < database.length(); i++) {
            JSONObject table = database.getJSONObject(i);
            if (table.has(tableName)) {
                JSONArray tableData = table.getJSONArray(tableName);
             if (tableData.length() == 0) {
                    return false; 
                }
                JSONArray firstRow = tableData.getJSONArray(0);
                for (int j = 0; j < firstRow.length(); j++) {
                    String curCol = firstRow.getString(j).trim();
                    String[] colParts = curCol.split("\\s+");                    
                    if (colParts.length > 0 && colParts[0].equalsIgnoreCase(columnName)) {
                        return curCol.toLowerCase().contains("not null")||curCol.toLowerCase().contains("primary key");
                    }
                }
            }
        }
        return false;
    }
    static boolean isPrimary(JSONArray database, String columnName, String tableName) {
        for (int i = 0; i < database.length(); i++) {
            JSONObject table = database.getJSONObject(i);
            if (table.has(tableName)) {
                JSONArray tableData = table.getJSONArray(tableName);

                
                if (tableData.length() == 0) {
                    return false; 
                }

                JSONArray firstRow = tableData.getJSONArray(0);
                for (int j = 0; j < firstRow.length(); j++) {
                    String curCol = firstRow.getString(j).trim();
                    String[] colParts = curCol.split("\\s+");                    
                    if (colParts.length > 0 && colParts[0].equalsIgnoreCase(columnName)) {
                        return curCol.toLowerCase().contains("primary key");
                    }
                }
            }
        }
        return false;
    }
    static boolean isUnique(JSONArray database, String columnName, String tableName) {
        for (int i = 0; i < database.length(); i++) {
            JSONObject table = database.getJSONObject(i);
            if (table.has(tableName)) {
                JSONArray tableData = table.getJSONArray(tableName);

                
                if (tableData.length() == 0) {
                    return false; 
                }

                JSONArray firstRow = tableData.getJSONArray(0);
                for (int j = 0; j < firstRow.length(); j++) {
                    String curCol = firstRow.getString(j).trim();
                    String[] colParts = curCol.split("\\s+");                    
                    if (colParts.length > 0 && colParts[0].equalsIgnoreCase(columnName)) {
                        return curCol.toLowerCase().contains("unique");
                    }
                }
            }
        }
        return false;
    }

}
