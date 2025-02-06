package com.example.sql;

import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class SelectTable {

	public static void selectFromTable(JSONArray database, Scanner in) {
        System.out.print("Enter the table name: ");
        String tableName=in.nextLine();

        boolean tableFound=false;

        for (int i=0;i<database.length();i++) {
            JSONObject table=database.getJSONObject(i);

            if (table.has(tableName)) {
                tableFound=true;
                JSONArray tableData=table.getJSONArray(tableName);
                JSONArray columns=tableData.getJSONArray(0);

                System.out.print("Enter columns to select (comma separated), or '*' for all columns: ");
                System.out.println("Available columns are: "+columns);

                String selectedColumns=in.nextLine();
                String[] columnNames=selectedColumns.split(",");

                if (columnNames[0].trim().equals("*")) {
                    columnNames=new String[columns.length()];
                    for (int j=0;j<columns.length();j++) {
                        columnNames[j]=columns.getString(j);
                    }
                }

                System.out.println("Enter the condition (if any) as column=value (comma separated), or type 'none' for no conditions:");
                System.out.println("Available columns are: "+columns);
                String whereConditions=in.nextLine();

                if (!whereConditions.equals("none")) {
                    String[] conditions=whereConditions.split(",");
                    boolean validConditions=true;
                    JSONArray validRows=new JSONArray();
                    validRows.put(getSelectedColumns(columns,columnNames)); 
                    String op = null;
                    for (String condition:conditions) {
                    	if(condition.contains("=")) {	
                    		op="=";
                    	}
                    	else if(condition.contains(">")) {
                    		op=">";
                    	}
                    	else if(condition.contains("<")) {
                    		op="<";
                    	}     
                    	String parts[]=condition.split(op);
                    	if(parts.length !=2) {
                    		validConditions=false;
                    	}
                    }

                    if (!validConditions) {
                        return;
                    }

                    for (int row=1;row<tableData.length();row++) {
                        JSONArray rowData=tableData.getJSONArray(row);
                        boolean match=true;

                        for (String condition:conditions) {
                        	if(condition.contains("=")) {	
                        		op="=";
                        	}
                        	else if(condition.contains(">")) {
                        		op=">";
                        	}
                        	else if(condition.contains("<")) {
                        		op="<";
                        	} 
                            String[] parts=condition.split(op);
                            String whereColumn=parts[0].trim();
                            String whereValue=parts[1].trim();

                            int whereIndex=columns.toList().indexOf(whereColumn);
                            if (whereIndex==-1||op.equals("=")&&!rowData.getString(whereIndex).equals(whereValue)) {
                                match=false;
                                break;
                            }
                            else if (whereIndex==-1||op.equals(">")&&!(Integer.parseInt(rowData.getString(whereIndex))>Integer.parseInt(whereValue))) {
                                match=false;
                                break;
                            }
                            else if (whereIndex==-1||op.equals("<")&&!(Integer.parseInt(rowData.getString(whereIndex))<Integer.parseInt(whereValue))) {
                                match=false;
                                break;
                            }
                        }

                        if (match) {
                            JSONArray selectedRow=new JSONArray();
                            for (String column:columnNames) {
                                int columnIndex=columns.toList().indexOf(column.trim());
                                if (columnIndex!=-1) {
                                    selectedRow.put(rowData.get(columnIndex));
                                }
                            }
                            validRows.put(selectedRow);
                        }
                    }
                    displayTable(validRows);
                } else {
                    JSONArray validRows=new JSONArray();
                    validRows.put(getSelectedColumns(columns,columnNames));

                    for (int row=1;row<tableData.length();row++) {
                        JSONArray rowData=tableData.getJSONArray(row);
                        JSONArray selectedRow=new JSONArray();
                        for (String column:columnNames) {
                            int columnIndex=columns.toList().indexOf(column.trim());
                            if (columnIndex!=-1) {
                                selectedRow.put(rowData.get(columnIndex));
                            }
                        }
                        validRows.put(selectedRow);
                    }

                    displayTable(validRows);
                }
                return;
            }
        }

        if (!tableFound) {
            System.out.println("Table not found");
        }
    }

    private static JSONArray getSelectedColumns(JSONArray columns, String[] columnNames) {
        JSONArray selectedColumns=new JSONArray();
        for (String column:columnNames) {
            int columnIndex=columns.toList().indexOf(column.trim());
            if (columnIndex!=-1) {
                selectedColumns.put(columns.getString(columnIndex));
            }
        }
        return selectedColumns;
    }

    public static void displayTable(JSONArray tableData) {
        for (int i=0;i<tableData.length();i++) {
            JSONArray rowData=tableData.getJSONArray(i);
            for (int j=0;j<rowData.length();j++) {
                System.out.print(rowData.getString(j)+"\t"+"|");

            }
            System.out.println();
        }
    }
} 