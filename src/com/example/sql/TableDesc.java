import org.json.JSONArray;
import org.json.JSONObject;

public class TableDesc {
    public static void description(JSONArray database, String tableName) {
        JSONArray res = new JSONArray();
        boolean found = false;
        tableName+="schema";
        for (int i = 0; i < database.length(); i++) {
            JSONObject table = database.getJSONObject(i);

            if (table.has(tableName)) {
                found = true;
                JSONArray tableData = table.getJSONArray(tableName);

                if (tableData.length() > 0 && tableData.get(0) instanceof JSONArray) {
                    tableData = tableData.getJSONArray(0);
                }
                for (int j = 0; j < tableData.length(); j++) {
                    String curCol = tableData.getString(j);
                    String[] current = curCol.split("\\s+");
                    JSONArray curRow = new JSONArray();

                    if (current.length >= 2) {
                        curRow.put(current[0]);
                        curRow.put(current[1]);

                        String isNullable = "NO", primaryKey = "  ", autoIncrement = "  ";
                        String upperCol = curCol.toUpperCase();

                        if (!upperCol.contains("NULL")||(upperCol.contains("NULL")&& !upperCol.contains("NOT NULL"))) isNullable = "YES";
                        if (upperCol.contains("PRIMARY KEY")) primaryKey = "PRI";
                        if (upperCol.contains("AUTO_INCREMENT")) autoIncrement = "auto_increment";

                        curRow.put(isNullable);
                        curRow.put(primaryKey);
                        curRow.put(autoIncrement);
                    }

                    res.put(curRow);
                }
                System.out.printf("%-15s | %-15s | %-6s | %-10s | %-12s |\n", "FIELD", "TYPE", "NULL", "PRIMARY", "AUTO_INC");
                System.out.println("----------------------------------------------------------------------------");

                for (int k = 0; k < res.length(); k++) {
                    JSONArray types = res.getJSONArray(k);
                    System.out.printf("%-15s | %-15s | %-6s | %-10s | %-12s |\n",
                            types.getString(0), // Field Name
                            types.getString(1), // Type
                            types.getString(2), // NULL Allowed
                            types.getString(3), // Primary Key
                            types.getString(4)  // Auto Increment
                    );
                }

            }
        }

        if (!found) System.out.println("Table Not Found");
    }
}
