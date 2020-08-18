package sxb180026;
import java.io.RandomAccessFile;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


/* Team Blue's DavisBaseDemo */


public class DavisBaseDemo {

	static String prompt = "Bluedbsql >";
	static String dir_catalog = "data/catalog";
	static String dir_userdata = "data/user_data";
	static String version = "V1.0";
	
	static boolean isExit = false;
		
	public static int pageSize = 512;
	
	static Scanner scanner = new Scanner(System.in).useDelimiter(";");
	
    public static void main(String[] args) {
		splashScreen();
		initbase();

		String userCommand = ""; 

		while(!isExit) {
			System.out.print(prompt);
			userCommand = scanner.next().replace("\n", " ").replace("\r", "").trim().toLowerCase();
			parseUserCommand(userCommand);
		}
		System.out.println("Exiting...");


	}
	
    public static void splashScreen() {
		System.out.println(line("*",80));
        System.out.println("Welcome to DavisBase");
		System.out.println("DavisBase Version " + version);
		System.out.println("\nType \"help;\" to display supported commands.");
		System.out.println(line("*",80));
	}
	

	
	public static String line(String s,int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}
	
	
	public static void help() {
		System.out.println(line("*",80));
		System.out.println("SUPPORTED COMMANDS");
		System.out.println("All commands below are case insensitive");
		System.out.println(line("-",50));
		System.out.println("\tSHOW TABLES;                                               Display all the tables in the database.");
		System.out.println("\tCREATE TABLE table_name (<column_name datatype> <NOT NULL/UNIQUE>);   Create a new table in the database. First record should be primary key of type Int.");
		System.out.println("\tCREATE INDEX ON table_name (<column_name>);       	     Create a new index for the table in the database.");
		System.out.println("\tINSERT INTO table_name VALUES (value1,value2,..);          Insert a new record into the table. First Column is primary key which has inbuilt auto increment function.");
		System.out.println("\tDELETE FROM TABLE table_name WHERE row_id = key_value;     Delete a record from the table whose rowid is <key_value>.");
		System.out.println("\tUPDATE table_name SET column_name = value WHERE condition; Modifies the records in the table.");
		System.out.println("\tSELECT * FROM table_name;                                  Display all records in the table.");
		System.out.println("\tSELECT * FROM table_name WHERE column_name operator value; Display records in the table where the given condition is satisfied.");
		System.out.println("\tDROP TABLE table_name;                                     Remove table data and its schema.");
		System.out.println("\tVERSION;                                                   Show the program version.");
		System.out.println("\tHELP;                                                      Show this help information.");
		System.out.println("\tEXIT;                                                      Exit the program.");
		System.out.println(line("-",50));
		System.out.println(line("*",80));
	}


	
	public static boolean is_table(String tablename){
		tablename = tablename+".tbl";
		
		try {
			
			
			File data_path = new File(dir_userdata);
			if (tablename.equalsIgnoreCase("davisbase_tables.tbl") || tablename.equalsIgnoreCase("davisbase_columns.tbl"))
				data_path = new File(dir_catalog) ;
			
			String[] existingtablesF;
			existingtablesF = data_path.list();
	
			for (int i=0; i<existingtablesF.length; i++) {
				if(existingtablesF[i].equals(tablename))
					return true;
			}
		}
		catch (SecurityException se) {
			System.out.println("data folder could not be created....try again");
			System.out.println(se);
		}

		return false;
	}

	public static void initbase(){
		try {
			File data_path = new File("data");
			if(data_path.mkdir()){
				System.out.println("initializing data base...");
				initializedb();
			}
			else {
				data_path = new File(dir_catalog);
				String[] existingtableF = data_path.list();
				boolean tableflag = false;
				boolean columnflag = false;
				for (int i=0; i<existingtableF.length; i++) {
					if(existingtableF[i].equals("davisbase_tables.tbl"))
						tableflag = true;
					if(existingtableF[i].equals("davisbase_columns.tbl"))
						columnflag = true;
				}
				
				if(!tableflag){
					System.out.println("The davisbase_tables does not exit, initializing data base...");
					System.out.println();
					initializedb();
				}
				
				if(!columnflag){
					System.out.println("The davisbase_columns table does not exit, initializing data base...");
					System.out.println();
					initializedb();
				}
				
			}
		}
		catch (SecurityException e) {
			System.out.println(e);
		}

	}
	
public static void initializedb() {

		
		try {
			File data_path = new File(dir_userdata);
			data_path.mkdir();
			data_path = new File(dir_catalog);
			data_path.mkdir();
			String[] existingtableF;
			existingtableF = data_path.list();
			for (int i=0; i<existingtableF.length; i++) {
				File old_file = new File(data_path, existingtableF[i]); 
				old_file.delete();
			}
		}
		catch (SecurityException e) {
			System.out.println(e);
		}

		try {
			RandomAccessFile catalog_tab = new RandomAccessFile(dir_catalog+"/davisbase_tables.tbl", "rw");
			catalog_tab.setLength(pageSize);
			catalog_tab.seek(0);
			catalog_tab.write(0x0D);
			catalog_tab.writeByte(0x02);
			
			int size1=24;
			int size2=25;
			
			int offsetT=pageSize-size1;
			int offsetC=offsetT-size2;
			
			catalog_tab.writeShort(offsetC);
			catalog_tab.writeInt(0);
			catalog_tab.writeInt(0);
			catalog_tab.writeShort(offsetT);
			catalog_tab.writeShort(offsetC);
			
			catalog_tab.seek(offsetT);
			catalog_tab.writeShort(20);
			catalog_tab.writeInt(1); 
			catalog_tab.writeByte(1);
			catalog_tab.writeByte(28);
			catalog_tab.writeBytes("davisbase_tables");
			
			catalog_tab.seek(offsetC);
			catalog_tab.writeShort(21);
			catalog_tab.writeInt(2); 
			catalog_tab.writeByte(1);
			catalog_tab.writeByte(29);
			catalog_tab.writeBytes("davisbase_columns");
			
			catalog_tab.close();
		}
		catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			RandomAccessFile catalog_c = new RandomAccessFile(dir_catalog+"/davisbase_columns.tbl", "rw");
			catalog_c.setLength(pageSize);
			catalog_c.seek(0);       
			catalog_c.writeByte(0x0D); 
			catalog_c.writeByte(0x09); 
			
			int[] offset=new int[9];
			offset[0]=pageSize-45;
			offset[1]=offset[0]-49;
			offset[2]=offset[1]-46;
			offset[3]=offset[2]-50;
			offset[4]=offset[3]-51;
			offset[5]=offset[4]-49;
			offset[6]=offset[5]-59;
			offset[7]=offset[6]-51;
			offset[8]=offset[7]-49;
			
			catalog_c.writeShort(offset[8]); 
			catalog_c.writeInt(0); 
			catalog_c.writeInt(0); 
			
			for(int i=0;i<offset.length;i++)
				catalog_c.writeShort(offset[i]);

			
			catalog_c.seek(offset[0]);
			catalog_c.writeShort(36);
			catalog_c.writeInt(1); 
			catalog_c.writeByte(6); 
			catalog_c.writeByte(28); 
			catalog_c.writeByte(17); 
			catalog_c.writeByte(15); 
			catalog_c.writeByte(4);
			catalog_c.writeByte(14);
			catalog_c.writeByte(14);
			catalog_c.writeBytes("davisbase_tables"); 
			catalog_c.writeBytes("rowid"); 
			catalog_c.writeBytes("INT"); 
			catalog_c.writeByte(1); 
			catalog_c.writeBytes("NO"); 
			catalog_c.writeBytes("NO"); 
			catalog_c.writeBytes("NO");
			
			catalog_c.seek(offset[1]);
			catalog_c.writeShort(42); 
			catalog_c.writeInt(2); 
			catalog_c.writeByte(6);
			catalog_c.writeByte(28);
			catalog_c.writeByte(22);
			catalog_c.writeByte(16);
			catalog_c.writeByte(4);
			catalog_c.writeByte(14);
			catalog_c.writeByte(14);
			catalog_c.writeBytes("davisbase_tables"); 
			catalog_c.writeBytes("table_name"); 
			catalog_c.writeBytes("TEXT"); 
			catalog_c.writeByte(2);
			catalog_c.writeBytes("NO"); 
			catalog_c.writeBytes("NO");
			
			catalog_c.seek(offset[2]);
			catalog_c.writeShort(37); 
			catalog_c.writeInt(3); 
			catalog_c.writeByte(6);
			catalog_c.writeByte(29);
			catalog_c.writeByte(17);
			catalog_c.writeByte(15);
			catalog_c.writeByte(4);
			catalog_c.writeByte(14);
			catalog_c.writeByte(14);
			catalog_c.writeBytes("davisbase_columns");
			catalog_c.writeBytes("rowid");
			catalog_c.writeBytes("INT");
			catalog_c.writeByte(1);
			catalog_c.writeBytes("NO");
			catalog_c.writeBytes("NO");
			
			catalog_c.seek(offset[3]);
			catalog_c.writeShort(43);
			catalog_c.writeInt(4); 
			catalog_c.writeByte(6);
			catalog_c.writeByte(29);
			catalog_c.writeByte(22);
			catalog_c.writeByte(16);
			catalog_c.writeByte(4);
			catalog_c.writeByte(14);
			catalog_c.writeByte(14);
			catalog_c.writeBytes("davisbase_columns");
			catalog_c.writeBytes("table_name");
			catalog_c.writeBytes("TEXT");
			catalog_c.writeByte(2);
			catalog_c.writeBytes("NO");
			catalog_c.writeBytes("NO");
			
			catalog_c.seek(offset[4]);
			catalog_c.writeShort(44);
			catalog_c.writeInt(5); 
			catalog_c.writeByte(6);
			catalog_c.writeByte(29);
			catalog_c.writeByte(23);
			catalog_c.writeByte(16);
			catalog_c.writeByte(4);
			catalog_c.writeByte(14);
			catalog_c.writeByte(14);
			catalog_c.writeBytes("davisbase_columns");
			catalog_c.writeBytes("column_name");
			catalog_c.writeBytes("TEXT");
			catalog_c.writeByte(3);
			catalog_c.writeBytes("NO");
			catalog_c.writeBytes("NO");
			
			catalog_c.seek(offset[5]);
			catalog_c.writeShort(42);
			catalog_c.writeInt(6); 
			catalog_c.writeByte(6);
			catalog_c.writeByte(29);
			catalog_c.writeByte(21);
			catalog_c.writeByte(16);
			catalog_c.writeByte(4);
			catalog_c.writeByte(14);
			catalog_c.writeByte(14);
			catalog_c.writeBytes("davisbase_columns");
			catalog_c.writeBytes("data_type");
			catalog_c.writeBytes("TEXT");
			catalog_c.writeByte(4);
			catalog_c.writeBytes("NO");
			catalog_c.writeBytes("NO");
			
			catalog_c.seek(offset[6]);
			catalog_c.writeShort(52); 
			catalog_c.writeInt(7); 
			catalog_c.writeByte(6);
			catalog_c.writeByte(29);
			catalog_c.writeByte(28);
			catalog_c.writeByte(19);
			catalog_c.writeByte(4);
			catalog_c.writeByte(14);
			catalog_c.writeByte(14);
			catalog_c.writeBytes("davisbase_columns");
			catalog_c.writeBytes("ordinal_position");
			catalog_c.writeBytes("TINYINT");
			catalog_c.writeByte(5);
			catalog_c.writeBytes("NO");
			catalog_c.writeBytes("NO");
			
			catalog_c.seek(offset[7]);
			catalog_c.writeShort(44); 
			catalog_c.writeInt(8); 
			catalog_c.writeByte(6);
			catalog_c.writeByte(29);
			catalog_c.writeByte(23);
			catalog_c.writeByte(16);
			catalog_c.writeByte(4);
			catalog_c.writeByte(14);
			catalog_c.writeByte(14);
			catalog_c.writeBytes("davisbase_columns");
			catalog_c.writeBytes("is_nullable");
			catalog_c.writeBytes("TEXT");
			catalog_c.writeByte(6);
			catalog_c.writeBytes("NO");
			catalog_c.writeBytes("NO");
		

			catalog_c.seek(offset[8]);
			catalog_c.writeShort(42); 
			catalog_c.writeInt(9); 
			catalog_c.writeByte(6);
			catalog_c.writeByte(29);
			catalog_c.writeByte(21);
			catalog_c.writeByte(16);
			catalog_c.writeByte(4);
			catalog_c.writeByte(14);
			catalog_c.writeByte(14);
			catalog_c.writeBytes("davisbase_columns");
			catalog_c.writeBytes("is_unique");
			catalog_c.writeBytes("TEXT");
			catalog_c.writeByte(7);
			catalog_c.writeBytes("NO");
			catalog_c.writeBytes("NO");
			
			catalog_c.close();
		}
		catch (Exception e) {
			System.out.println(e);
		}
}



	public static String[] check_equ(String st){
		String equt[] = new String[3];
		String temp[] = new String[2];
		if(st.contains("=")) {
			temp = st.split("=");
			equt[0] = temp[0].trim();
			equt[1] = "=";
			equt[2] = temp[1].trim();
		}
		
		if(st.contains("<")) {
			temp = st.split("<");
			equt[0] = temp[0].trim();
			equt[1] = "<";
			equt[2] = temp[1].trim();
		}
		
		if(st.contains(">")) {
			temp = st.split(">");
			equt[0] = temp[0].trim();
			equt[1] = ">";
			equt[2] = temp[1].trim();
		}
		
		if(st.contains("<=")) {
			temp = st.split("<=");
			equt[0] = temp[0].trim();
			equt[1] = "<=";
			equt[2] = temp[1].trim();
		}

		if(st.contains(">=")) {
			temp = st.split(">=");
			equt[0] = temp[0].trim();
			equt[1] = ">=";
			equt[2] = temp[1].trim();
		}
		
		if(st.contains("!=")) {
			temp = st.split("!=");
			equt[0] = temp[0].trim();
			equt[1] = "!=";
			equt[2] = temp[1].trim();
		}

		return equt;
	}
		
	public static void parseUserCommand (String userCommand) {
		
		ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(userCommand.split(" ")));

		switch (commandTokens.get(0)) {

		    case "show":
		    	if(commandTokens.get(1).equalsIgnoreCase("tables")) {
		    		showTables();
		    	}
		    	else {
		    		System.out.println("Syntax Error: error in command near "+ commandTokens.get(1));
		    	}
			    break;
			
		    case "create":
		    	switch (commandTokens.get(1)) {
		    	case "table": 
		    		parseCreateString(userCommand);
		    		break;
		    		
		    	case "index":
		    		parseIndexString(userCommand);
		    		break;
		    		
		    	default:
					System.out.println("syntax error:error in command" + commandTokens.get(1));
					System.out.println();
					break;
		    	}
		    	break;

			case "insert":
				parseInsertString(userCommand);
				break;
				
			case "delete":
				parseDeleteString(userCommand);
				break;	

			case "update":
				parseUpdateString(userCommand);
				break;
				
			case "select":
				parseQueryString(userCommand);
				break;

			case "drop":
				dropTable(userCommand);
				break;	

			case "help":
				help();
				break;

			case "version":
				System.out.println("DavisBase Version " + version);

				break;

			case "exit":
				isExit=true;
				break;
				
			case "quit":
				isExit=true;
				break;
	
			default:
				System.out.println("command not supported: \"" + userCommand + "\"");
				System.out.println();
				break;
		}
	} 

	public static void showTables() {
		String table = "davisbase_tables";
		String[] columns = {"table_name"};
		String[] colon = new String[0];
		Table.display(table, columns, colon,dir_userdata+"/");
	}
	
    public static void parseCreateString(String createString) {
		
		String[] createTokens=createString.split(" ");
		String table_name = createTokens[2];
		String[] temp = createString.split(table_name);
		String colns = temp[1].trim();
		String[] create_cols = colns.substring(1, colns.length()-1).split(",");
		
		for(int i = 0; i < create_cols.length; i++)
			create_cols[i] = create_cols[i].trim();
		
		if(is_table(table_name)){
			System.out.println("Table "+table_name+" already exists.");
		}
		else
			{
			Table.createTable(table_name, create_cols);	
			System.out.println("QueryOk...0 rows affected");
			}

	}
    
    public static void parseInsertString(String insertString) {
    	try{
		String[] insertToken=insertString.split(" ");
		String table = insertToken[2];
		String[] temp = insertString.split("values");
		String colns=temp[1].trim();
		String[] insert_vals = colns.substring(1, colns.length()-1).split(",");
		for(int i = 0; i < insert_vals.length; i++)
			insert_vals[i] = insert_vals[i].trim();
		
		
		
		if(table.contains("("))
		{
			String[] strarr=table.split("\\(");
			table=strarr[0];
		}
			
			
	   
		if(!is_table(table)){
			System.out.println("Table "+table+" does not exist.");
		}
		else
		{
			Table.insertInto(table, insert_vals,dir_userdata+"/");
			/*System.out.println("QueryOk...1 row affected");*/
		}
    	}
    	catch(Exception e)
    	{
    		System.out.println(e+e.toString());
    	}

	}
    
    public static void parseDeleteString(String deleteString) {
		
		String[] deleteTokens=deleteString.split(" ");
		String table = deleteTokens[2];
		String[] temp = deleteString.split("where");
		String cmpTemp = temp[1];
		String[] cmp = check_equ(cmpTemp);
		if(!is_table(table)){
			System.out.println("Table "+table+" does not exist.");
		}
		else
		{
			Table.delete(table, cmp);
			System.out.println("QueryOk...1 rows affected");
		}
		
		
	}
    
    public static void parseUpdateString(String updateString) {
		
		String[] updateTokens=updateString.split(" ");
		String table = updateTokens[1];
		String[] temp1 = updateString.split("set");
		String[] temp2 = temp1[1].split("where");
		String cmpval = temp2[1];
		String setval = temp2[0];
		String[] col_val = check_equ(cmpval);
		String[] set_new_col = check_equ(setval);
		if(!is_table(table)){
			System.out.println("Table "+table+" does not exist.");
		}
		else
		{
			Table.update(table, col_val, set_new_col);
			System.out.println("QueryOk...1 rows affected");
		}
		
	}
    
    public static void parseQueryString(String queryString) {
		
		String[] cmp;
		String[] column;
		String[] inserttokens = queryString.split("where");
		if(inserttokens.length > 1){
			String tmp = inserttokens[1].trim();
			cmp = check_equ(tmp);
		}
		else{
			cmp = new String[0];
		}
		String[] select = inserttokens[0].split("from");
		String tableName = select[1].trim();
		String cols = select[0].replace("select", "").trim();
		if(cols.contains("*")){
			column = new String[1];
			column[0] = "*";
		}
		else{
			column = cols.split(",");
			for(int i = 0; i < column.length; i++)
				column[i] = column[i].trim();
		}
		
		if(!is_table(tableName)){
			System.out.println("Table "+tableName+" does not exist.");
		}
		else
		{
		    Table.display(tableName, column, cmp,dir_userdata+"/");
		    System.out.println("QueryOk..");
		}
	}
	
	public static void dropTable(String dropTableString) {
		String[] dropTokens=dropTableString.split(" ");
		String table_name = dropTokens[2];
		if(!is_table(table_name)){
			System.out.println("Table "+table_name+" does not exist.");
		}
		else
		{
			Table.drop(table_name);
			System.out.println("QueryOk...");
		}		

	}
	
public static void parseIndexString(String createString) {
	
		String[] indexTokens=createString.split(" ");
		String table_name = indexTokens[3];
		String[] temp = createString.split(table_name);
		String cols = temp[1].trim();
		String[] create_cols = cols.substring(1, cols.length()-1).split(",");
		
		for(int i = 0; i < create_cols.length; i++)
			create_cols[i] = create_cols[i].trim();
		
		
		Table.index(table_name, create_cols);	
		System.out.println("QueryOk..");
			

	}
		

}