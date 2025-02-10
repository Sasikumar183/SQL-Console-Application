package com.example.sql;

import org.json.JSONArray;
import org.json.JSONObject;

public class DeleteData {
	public static void delete(JSONArray database, String query) {
        query=query.substring(12).trim();
        int whereInd=query.toUpperCase().indexOf("WHERE");


        if (whereInd == -1) {
            System.out.println("Missing WHERE clause");
//          String que= "DROP TABLE "+query;
//          Truncate.truncate(database,que);
            return;
        }

        String tableName = query.substring(0, whereInd).trim();
        String whereClause = query.substring(whereInd + 5).trim();
        String[] conditions = whereClause.split("AND");

        boolean flag = false;
        for (int i = 0; i < database.length(); i++) {
            JSONObject table = database.getJSONObject(i);
            if (table.has(tableName)) {
                flag = true;
                JSONArray tableData = table.getJSONArray(tableName);
                JSONArray columns = tableData.getJSONArray(0);

                boolean validConditions=true;
                JSONArray validRows=new JSONArray();
                validRows.put(columns);

                for (String condition:conditions) {
                    condition=condition.trim();
                    
                    String op=condition.contains("=") ? "=" :
                              condition.contains(">") ? ">" :
                              condition.contains("<") ? "<" : null;

                    if (op == null) {
                        validConditions=false;
                        break;
                    }

                    String[] parts=condition.split(op);
                    if (parts.length!=2) {
                        System.out.println("Invalid condition format. Must be column=value");
                        validConditions=false;
                        break;
                    }
                }

                if (!validConditions) {
                    System.out.println("Invalid WHERE conditions.");
                    return;
                }

                int deletedCount=0;
                for (int j=1;j<tableData.length();j++) {
                    JSONArray rowData=tableData.getJSONArray(j);
                    boolean match=true;

                    for (String condition:conditions) {
                        condition=condition.trim();

                        String op=condition.contains("=") ? "=" :
                                  condition.contains(">") ? ">" :
                                  condition.contains("<") ? "<" : null;

                        String[] parts=condition.split(op);
                        String whereCol=parts[0].trim();
                        String whereVal=parts[1].trim();

                        int whereIndx=-1;
                        for(int c=0;c<columns.length();c++) {
                            if(columns.getString(c).equals(whereCol)) {
                                whereIndx=c;
                                break;
                            }
                        }

                        if (whereIndx == -1) {
                            System.out.println("Column " + whereCol + " not found.");
                            return;
                        }

                        String rowValue = rowData.getString(whereIndx);

                        try {
                            int rowInt = Integer.parseInt(rowValue);
                            int condInt = Integer.parseInt(whereVal);

                            if ((op.equals("=") && rowInt != condInt) ||
                                (op.equals(">") && rowInt <= condInt) ||
                                (op.equals("<") && rowInt >= condInt)) {
                                match = false;
                                break;
                            }
                        } catch (NumberFormatException e) {
                            if (op.equals("=") && !rowValue.equals(whereVal)) {
                                match = false;
                                break;
                            }
                        }
                    }

                    if (match) {
                        deletedCount++;
                    } else {
                        validRows.put(rowData);
                    }
                }

                table.put(tableName, validRows);
                System.out.println(deletedCount > 0 ? deletedCount + " Rows Deleted" : "No Rows Deleted");
                return;
            }
        }
        if (!flag) {
            System.out.println("Table Not Found");
        }
    }
}
