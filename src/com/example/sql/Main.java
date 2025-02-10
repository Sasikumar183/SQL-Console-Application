import java.util.Scanner;

import org.json.JSONArray;


public class Main {
    private static JSONArray database = new JSONArray(); 
    

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
        	
			while (true) {
				System.out.println("Enter the SQL Query");
			    String query=scanner.nextLine();
			    
			    try {
					    if(query.toUpperCase().startsWith("CREATE TABLE")) {
					        CreateTable.create(database,query);
					        //System.out.println(database);
					    }
					    else if(query.toUpperCase().startsWith("INSERT INTO")) {
					        InsertData.insert(database,query);
					    }
					    
					    else if(query.toUpperCase().startsWith("DELETE FROM")) {
					        DeleteTable2.delete(database,query);
					    }
					    else if(query.toUpperCase().startsWith("UPDATE ")) {
					        UpdateTable2.update(database, query);
					    }
					    else if(query.toUpperCase().startsWith("SELECT ")) {
					        SelectTable2.select(database,query);
					    }
					    else if(query.toUpperCase().startsWith("TRUNCATE TABLE")) {
					        Truncate.truncate(database,query);
					    }
					    else if(query.toUpperCase().startsWith("DROP ")) {
					        Drop.drop(database,query);
					    }
					    else if(query.toUpperCase().startsWith("DESC ")) {
					    	TableDesc.description(database, query.substring(5).trim());
					    }
					    else if(query.toUpperCase().startsWith("ALTER TABLE")) {
					    	AlterTable.alterTable(database, query);
					    }
					    else {
					    	System.out.println("Check Syntax");
					    }
					
				} 
			    catch (Exception e) {
					e.printStackTrace();
				}
			   
			}
		}
    }
}
