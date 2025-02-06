package com.example.sql;

import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class Update{

    public static void updateTable(JSONArray database, Scanner in) {
        System.out.print("Enter the table name: ");
        String tableName=in.nextLine();
        boolean tableFound=false;

        for (int i = 0;i<database.length();i++) {
            JSONObject table=database.getJSONObject(i);
            if (table.has(tableName)) {
                tableFound=true;
                JSONArray tableData=table.getJSONArray(tableName);
                JSONArray columns=tableData.getJSONArray(0);

                System.out.println("Enter SET condition as column=value if multiple means (comma separated).");
                System.out.println("Available columns are "+columns);
                String setConditions = in.nextLine();
                String[] setConditionArray = setConditions.split(",");
                boolean validSetConditions = true;

                for (String condition : setConditionArray) {
                    String[] parts = condition.split("=");
                    if (parts.length != 2) {
                        System.out.println("Invalid SET condition format. Must be column=value");
                        validSetConditions = false;
                        break;
                    }
                }

                if (!validSetConditions) {
                    return;
                }

                System.out.println("Enter multiple WHERE conditions as column=value (comma separated).(or type 'none' for no conditions):");
                System.out.println("Available columns are "+columns);
                String whereConditions=in.nextLine();
                if (!whereConditions.equals("none")) {
                    String[] whereConditionArray = whereConditions.split(",");
                    boolean validWhereConditions = true;

                    for (String condition : whereConditionArray) {
                        String[] parts = condition.split("=");
                        if (parts.length != 2) {
                            System.out.println("Invalid WHERE condition format. Must be column=value");
                            validWhereConditions = false;
                            break;
                        }
                    }

                    if (!validWhereConditions) {
                        return;
                    }

                    for (int row = 1; row < tableData.length(); row++) {
                        JSONArray rowData = tableData.getJSONArray(row);
                        boolean match = true;

                        for (String condition:whereConditionArray) {
                            String[] parts=condition.split("=");
                            String whereColumn=parts[0].trim();
                            String whereValue=parts[1].trim();

                            int whereIndex=columns.toList().indexOf(whereColumn);
                            if (whereIndex==-1||!rowData.getString(whereIndex).equals(whereValue)) {
                                match=false;
                                break;
                            }
                        }

                        if (match) {
                            for (String condition:setConditionArray) {
                                String[] parts=condition.split("=");
                                String setColumn=parts[0].trim();
                                String setValue=parts[1].trim();

                                int setIndex=columns.toList().indexOf(setColumn);
                                if (setIndex!=-1) {
                                    rowData.put(setIndex, setValue);
                                }
                            }
                        }
                    }

                    SelectTable.displayTable(tableData);
                    System.out.println("Update successfully.");
                } else {
                    for (int row = 1; row < tableData.length(); row++) {
                        JSONArray rowData=tableData.getJSONArray(row);
                        for (String condition : setConditionArray) {
                            String[] parts=condition.split("=");
                            String setColumn=parts[0].trim();
                            String setValue=parts[1].trim();

                            int setIndex=columns.toList().indexOf(setColumn);
                            if (setIndex!=-1) {
                                rowData.put(setIndex,setValue);
                            }
                        }
                    }

                    SelectTable.displayTable(tableData);
                    System.out.println("Updated successfully.");
                }

                return;
            }
        }

        if (!tableFound) {
            System.out.println("Table not found");
        }
    }

   
}
