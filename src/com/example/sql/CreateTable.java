
import org.json.JSONArray;
import org.json.JSONObject;

public class CreateTable {
    public static void create(JSONArray database,String query) {
        query=query.substring(12).trim();
        int stInd=query.indexOf('(');
        int endInd=query.lastIndexOf(')');

        if(stInd==-1||endInd==-1||stInd>endInd) {
            System.out.println("Check the syntax");
            return;
        }
        
        String tableName=query.substring(0,stInd).trim();
        for(int i=0; i<database.length(); i++) {
            JSONObject table=database.getJSONObject(i);
            if(table.has(tableName)) {
                System.out.println("Table already exist");
                return;
            }
        }
        int i=query.toUpperCase().indexOf("PRIMARY KEY"),j=query.toUpperCase().lastIndexOf("PRIMARY KEY");
        if(i!=j) {
        	System.out.println("Multiple primary key not allowed");
        	return;
        }
        String[] columns=query.substring(stInd+1,endInd).split(",");

        JSONArray columnData=new JSONArray();
        JSONArray columnType=new JSONArray();
        for(String column:columns) {
        	if(column.trim().split(" ").length<2) {
        		System.out.println("Syntax Wrong");
        		return;
        	}
            columnData.put(column.trim().split(" ")[0]);
            columnType.put(column);
        }

        JSONArray tableData=new JSONArray();
        JSONArray tableSchemaData=new JSONArray();        
        tableData.put(columnData);
        tableSchemaData.put(columnType);
        JSONObject table=new JSONObject();
        JSONObject tableSchema=new JSONObject();
        table.put(tableName,tableData);
        tableSchema.put(tableName+"schema",tableSchemaData);
        database.put(table);
        database.put(tableSchema);
        System.out.println("Table '"+tableName+"' created successfully!");
    }
}
