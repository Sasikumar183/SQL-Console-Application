package com.example.sql;

import org.json.JSONArray;
import java.util.Scanner;

public class Main {
    private static JSONArray database = new JSONArray(); // Stores all tables

    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Create Table");
            System.out.println("2. Insert Data into Table");
            System.out.println("3. Delete Table Data");
            System.out.println("4. Select Table");
            System.out.println("5. Update Table"); 
            System.out.println("6. Drop Table");             
            System.out.println("7. Truncate Table"); 
            System.out.println("8. View Database");
            System.out.println("9. Exit");
            System.out.print("Choose an option: ");
            
            int choice=scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    CreatTable.createTable(database,scanner);
                    break;
                case 2:
                    InsertData.insert(database,scanner);
                    break;
                case 3:
                    DeleteData.delete(database,scanner);
                    break;
                case 4:
                    SelectTable.selectFromTable(database,scanner);
                    break;
                case 5:
                    Update.updateTable(database, scanner);
                    break;
                case 6:
                	DropTable.drop(database, scanner);
                	break;
                case 7:
                	TruncateTable.trucate(database, scanner);
                	break;
                case 8:
                    System.out.println("Database Structure:\n"+database);
                    break;
                case 9:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                    
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
