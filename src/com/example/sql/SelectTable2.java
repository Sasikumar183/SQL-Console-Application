
import org.json.JSONArray;
import org.json.JSONObject;

public class SelectTable2 {

    public static void select(JSONArray database, String query) {
        query = query.substring(6).trim(); 
        int fromInd = query.toUpperCase().indexOf("FROM");
        int whereInd = query.toUpperCase().indexOf("WHERE");
        int limitInd=query.toUpperCase().indexOf("LIMIT");
        int limitValue=limitInd==-1?-1:Integer.parseInt(query.trim().substring(limitInd+6));
        int firstKeywordInd = (whereInd == -1) ? limitInd :
            (limitInd == -1) ? whereInd :
            Math.min(whereInd, limitInd);

		String tableName = firstKeywordInd == -1 ? 
		         query.substring(fromInd + 4).trim() : 
		         query.substring(fromInd + 4, firstKeywordInd).trim();


        JSONObject targetTable = null;

        for (int i = 0; i < database.length(); i++) {
            JSONObject table = database.getJSONObject(i);
            if (table.has(tableName)) {
                targetTable = table;
                break;
            }
        }

        if (targetTable == null) {
            System.out.println("Table not found.");
            return;
        }

        JSONArray tableData = targetTable.getJSONArray(tableName);
        JSONArray columns = tableData.getJSONArray(0);

       
        String columnPart = query.substring(0, fromInd).trim();
        String[] selectedColumns;

        if (columnPart.equals("*")) {
            selectedColumns = new String[columns.length()];
            for (int i = 0; i < columns.length(); i++) {
                selectedColumns[i] = columns.getString(i);
            }
        } else {
            selectedColumns = columnPart.split(",");
        }

        String whereClause = whereInd == -1 ? null :limitInd==-1? query.substring(whereInd + 5).trim():query.substring(whereInd+5,limitInd).trim();
        String[] conditions = new String[0];
        boolean[] isOrCondition = new boolean[0];

        if (whereClause != null) {
            String[] words = whereClause.split(" ");
            int count = 0;

            for (String word : words) {
                if (word.equalsIgnoreCase("AND") || word.equalsIgnoreCase("OR")) {
                    count++;
                }
            }

            conditions = new String[count + 1];
            isOrCondition = new boolean[count];

            int index = 0;
            StringBuilder currentCondition = new StringBuilder();

            for (String word : words) {
                if (word.equalsIgnoreCase("AND")) {
                    conditions[index] = currentCondition.toString().trim();
                    isOrCondition[index] = false;
                    index++;
                    currentCondition.setLength(0);
                } else if (word.equalsIgnoreCase("OR")) {
                    conditions[index] = currentCondition.toString().trim();
                    isOrCondition[index] = true;
                    index++;
                    currentCondition.setLength(0);
                } else {
                    currentCondition.append(word).append(" ");
                }
            }
            conditions[index] = currentCondition.toString().trim();
        }

        JSONArray validRows = new JSONArray();
        validRows.put(getSelectedColumns(columns, selectedColumns));

        for (int row = 1; row < tableData.length(); row++) {
            JSONArray rowData = tableData.getJSONArray(row);
            boolean match = conditions.length == 0;
            boolean orMatch = false;

            for (int k = 0; k < conditions.length; k++) {
                String condition = conditions[k];
                String[] parts = parseCondition(condition);
                if (parts.length != 2) continue;

                String whereColumn = parts[0].trim();
                String whereValue = parts[1].trim();

                int whereIndex = indexOfColumn(columns, whereColumn);
                boolean conditionResult = (whereIndex != -1) && applyCondition(rowData, whereIndex, whereValue, condition);

                if (k == 0) {
                    match = conditionResult;
                } else {
                    if (isOrCondition[k - 1]) {
                        orMatch = orMatch || conditionResult;
                    } else {
                        match = match && conditionResult;
                    }
                }
            }

            match = match || orMatch;

            if (match) {
                JSONArray selectedRow = new JSONArray();
                for (String column : selectedColumns) {
                    int columnIndex = indexOfColumn(columns, column.trim());
                    if (columnIndex != -1) {
                        selectedRow.put(rowData.get(columnIndex));
                    }
                }
                validRows.put(selectedRow);
            }
        }

        displayTable(validRows,limitValue);
    }

    private static String[] parseCondition(String condition) {
        String op = condition.contains("=") ? "=" : (condition.contains(">") ? ">" : "<");
        return condition.split(op);
    }

    private static boolean applyCondition(JSONArray rowData, int columnIndex, String whereValue, String condition) {
        String rowValue = rowData.getString(columnIndex);
        String op = condition.contains("=") ? "=" : (condition.contains(">") ? ">" : "<");

        return (op.equals("=") && rowValue.equals(whereValue)) ||
               (op.equals(">") && Integer.parseInt(rowValue) > Integer.parseInt(whereValue)) ||
               (op.equals("<") && Integer.parseInt(rowValue) < Integer.parseInt(whereValue));
    }

    private static int indexOfColumn(JSONArray columns, String columnName) {
        for (int i = 0; i < columns.length(); i++) {
            if (columns.getString(i).equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    private static JSONArray getSelectedColumns(JSONArray columns, String[] selectedColumns) {
        JSONArray selectedColumnsArray = new JSONArray();
        for (String column : selectedColumns) {
            int columnIndex = indexOfColumn(columns, column.trim());
            if (columnIndex != -1) {
                selectedColumnsArray.put(columns.getString(columnIndex));
            }
        }
        return selectedColumnsArray;
    }

    public static void displayTable(JSONArray tableData,int limit) {
    	int n=limit==-1?tableData.length():limit+1;
        for (int i = 0; i < n; i++) {
            JSONArray rowData = tableData.getJSONArray(i);
            for (int j = 0; j < rowData.length(); j++) {
                System.out.printf("%-15s|",rowData.getString(j));
            }
            System.out.println();
        }
    }
    
} 