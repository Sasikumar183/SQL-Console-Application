
import org.json.JSONArray;
import org.json.JSONObject;

public class AlterTable {

    public static void alterTable(JSONArray database, String query) {
        query = query.substring(11).trim();
        
        int addInd = query.toUpperCase().indexOf("ADD");
        int dropInd = query.toUpperCase().indexOf("DROP COLUMN");
        int alterInd = query.toUpperCase().indexOf("ALTER COLUMN");
        int addConstr=query.toUpperCase().indexOf("ADD CONSTRAINT");

        String tablename;
        if(addConstr!=-1) {
        	tablename=query.substring(0,addConstr).trim();
        	addConstraint(database,query,tablename+"schema");
        }
         
        else if (dropInd != -1) { 
            tablename = query.substring(0, dropInd).trim();
            query = query.substring(dropInd + 11).trim();
            dropColumn(database, query, tablename);
        }
       
        else if(alterInd!=-1) {
            tablename = query.substring(0,alterInd).trim();
            query = query.substring(alterInd + 12).trim();
            modifyDT(database, query, tablename+"schema");

        }
        else if (addInd != -1) { 
            tablename = query.substring(0, addInd).trim();
            query = query.substring(addInd + 3).trim();
            addColumn(database, query, tablename);
            addToDesc(database,query,tablename+"schema");
        }
        else {
            System.out.println("Error: Invalid ALTER TABLE query");
        }
    }
    private static void addColumn(JSONArray database, String query, String table_name) {
        for (int i = 0; i < database.length(); i++) {
            JSONObject table = database.getJSONObject(i);
            if (table.has(table_name)) {
                JSONArray tableData=table.getJSONArray(table_name);
                String newCol=query.split(" ")[0];
                if (tableData.length()==0) {
                    System.out.println("Error: Table structure is invalid");
                    return;
                }
                
                JSONArray tableColumns=tableData.getJSONArray(0);
                tableColumns.put(newCol);

                for (int j=1;j<tableData.length();j++) {
                    JSONArray tableRow = tableData.getJSONArray(j);
                    tableRow.put("null");
                }
                
                System.out.println("Column added to table");
                return;
            }
        }
        System.out.println("Table not found");
    }

    private static void dropColumn(JSONArray database, String query, String table_name) {
        int ind = -1;

        for (int i = 0; i < database.length(); i++) {
            JSONObject table = database.getJSONObject(i);
            
            if (!table.has(table_name)) {
                continue;
            }

            JSONArray tableData = table.getJSONArray(table_name);
            String dropCol = query.split(" ")[0];

            if (tableData.length() == 0) {
                System.out.println("Error: Table structure is invalid");
                return;
            }

            JSONArray tableColumns = tableData.getJSONArray(0);
            ind = tableColumns.toList().indexOf(dropCol);

            if (ind == -1) {
                System.out.println("Error: Column '" + dropCol + "' does not exist in table '" + table_name + "'");
                return;
            }
            tableColumns.remove(ind);

            for (int j = 1; j < tableData.length(); j++) {
                JSONArray tableRow = tableData.getJSONArray(j);
                if (ind < tableRow.length()) {
                    tableRow.remove(ind);
                }
            }
            DropFromDesc(database,table_name,ind);

            System.out.println("Column " + dropCol + " dropped from table '" + table_name + "'");
            return;
        }

        System.out.println("Error: Table '" + table_name + "' not found in database.");
    }
    
    private static void DropFromDesc(JSONArray database,String tablename,int index) {
    	try {
    	for(int i=0;i<database.length();i++) {
    		JSONObject obj=database.getJSONObject(i);
    		if(obj.has(tablename+"schema")) {
              JSONArray tableSchema = obj.getJSONArray(tablename + "schema");
              JSONArray tableSchemaColumns = tableSchema.getJSONArray(0);
              tableSchemaColumns.remove(index);
              return;
    		}
    	}
    	}
    	catch(Exception e) {
    		System.out.println("Something went wrong");
    	}
    }
    
    private static void addToDesc(JSONArray database,String query,String tablename) {
    	for(int i=0;i<database.length();i++) {
    		JSONObject obj=database.getJSONObject(i);
    		if(obj.has(tablename)) {
    			JSONArray col=obj.getJSONArray(tablename);
    			JSONArray val=col.getJSONArray(0);
    			val.put(query);
    		}
    	}
    }
    
    private static void modifyDT(JSONArray  database, String query, String tablename) {
        String colName = query.trim().split(" ")[0];
        for (int i = 0; i < database.length(); i++) {
            JSONObject obj = database.getJSONObject(i);
            if (obj.has(tablename)) {
                JSONArray col = obj.getJSONArray(tablename);
                if (col.length() > 0) { 
                    JSONArray val = col.getJSONArray(0);
                    for (int j = 0; j < val.length(); j++) {
                        String curCol = val.getString(j).trim().split(" ")[0];
                        if (curCol.equals(colName)){
                            val.put(j, query);
                            System.out.println("Column Datatype Changed");
                            return;
                        }
                    }
                }
            }
        }
        System.out.println("Column not found in table: " + tablename);
    }
    private static void addConstraint(JSONArray database, String query, String tableName) {
    	query=query.trim().substring(14);
    	
        int startIdx = query.indexOf("(");
        int endIdx = query.indexOf(")");
        if (startIdx == -1 || endIdx == -1 || startIdx >= endIdx) {
            System.out.println("Invalid query format");
            return;
        }
        
        String colName = query.substring(startIdx + 1, endIdx).trim();
        int PK = query.trim().indexOf(" ");
        String queryPart=query.substring(PK,startIdx);
        if (queryPart.equals("")) {
            System.out.println("Invalid query format");
            return;
        }
        
        String constraint = queryPart;
        System.out.println(constraint);

        for (int i = 0; i < database.length(); i++) {
            JSONObject obj = database.getJSONObject(i);
            if (obj.has(tableName)) {
                JSONArray col = obj.getJSONArray(tableName);
                if (col.length() > 0) {
                    JSONArray val = col.getJSONArray(0);
                    for (int j = 0; j < val.length(); j++) {
                        String[] colParts = val.getString(j).trim().split(" ");
                        if (colParts.length > 0 && colParts[0].equals(colName)) {
                            val.put(j, val.getString(j)+" "+colParts[0] + " " + constraint); 
                            System.out.println("Constraint added to" + colName);
                            return;
                        }
                    }
                }
            }
        }
        System.out.println("Column not found in table: " + tableName);
    }

    
}
    

