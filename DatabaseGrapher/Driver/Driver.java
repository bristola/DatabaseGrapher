package DatabaseGrapher.Driver;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.sql.DatabaseMetaData;
import java.util.Iterator;

import DatabaseGrapher.Graphing.*;

public class Driver {

    private Connection conn = null;
    private int tableInput;
    private ArrayList<String> databases;
    private ArrayList<String> tables;
    private Scanner scan;
    private Statement statement;
    private ArrayList<String> valid_columns;
    private ArrayList<String> tableColumns;
    private ArrayList<String> foreignColumns;
    private ArrayList<String> keys;

    public void run() throws IOException {

        scan = new Scanner(System.in);

        try {
            String input = getInput();
            if (input.equals("-1"))
                return;
            connect(input);
            getTable();
            int graphType = getGraphType();

            switch(graphType) {
                case 1:
                    int[] axis = getColumns();
                    Object[][] data = getData(axis);
                    createLineGraph(data, axis, input);
                    break;
                case 2:
                    getTableForeignColumns(getRelations());
                    int columnInput = getColumnInput();
                    createBarGraph(columnInput);
                    break;
                default:
                    System.out.println("Invalid graph type");
                    break;
            }

        } catch(SQLException e) {
            System.out.println("An error has occured while connecting!\nExiting program!");
            e.printStackTrace();
            return;
        } catch(Exception e) {
            System.out.println("An error has occured");
            e.printStackTrace();
            return;
        }
    }

    public void createBarGraph(int columnInput) throws SQLException {
        Object data[][];
        Graph g = new BarGraph(tables.get(tableInput-1));
        if (columnInput >= tableColumns.size()) { // in foreign column
          // Gets amount of entries in table for data array
          ResultSet rs2 = statement.executeQuery("Select COUNT(*) AS total FROM "+tables.get(tableInput - 1));
          int count = rs2.getInt("total");
          data = new Object[count][2];

          // Executes correct query
          ResultSet rs3 = statement.executeQuery("SELECT "+keys.get(0)+", "+foreignColumns.get(columnInput-1-tableColumns.size()).split(" ")[1]+" AS val FROM "+foreignColumns.get(columnInput-1-tableColumns.size()).split(" ")[0]+" GROUP BY "+keys.get(0));
          // Goes through every entry and enters it into data array
          for (int i = 0; i < data.length; i++) {
            data[i][0] = (Object)rs3.getString(keys.get(0));
            data[i][1] = (Object)rs3.getDouble("val");
            rs3.next();
          }
          g.setYAxis(foreignColumns.get(columnInput-1-tableColumns.size()).split(" ")[1]);
          g.setXAxis(keys.get(0));

        } else { // in internal column
          // Gets amount of entries in table for data array
          ResultSet rs2 = statement.executeQuery("Select COUNT(*) AS total FROM "+tables.get(tableInput - 1));
          int count = rs2.getInt("total");
          data = new Object[count][2];

          // Executes correct query
          ResultSet rs3 = statement.executeQuery("SELECT "+keys.get(0)+", "+tableColumns.get(columnInput-1)+" FROM "+tables.get(tableInput-1));
          // Goes through every entry and enters it into data array
          for (int i = 0; i < data.length; i++) {
            data[i][0] = (Object)rs3.getString(keys.get(0));
            data[i][1] = (Object)rs3.getDouble(tableColumns.get(columnInput-1));
            rs3.next();
          }
          g.setYAxis(tableColumns.get(columnInput-1));
          g.setXAxis(keys.get(0));
        }
        g.setData(data);
        g.start();
    }

    public int getColumnInput() throws SQLException {
        System.out.println("\n\n");
        for (int i = 0; i < tableColumns.size(); i++) {
          System.out.println((i+1)+": "+tableColumns.get(i));
        }
        for (int i = 0; i < foreignColumns.size(); i++) {
          System.out.println((tableColumns.size()+i+1)+": "+foreignColumns.get(i).split(" ")[1]);
        }

        System.out.println("What value would you like to make the bar graph with?");
        int columnInput;
        do {
          columnInput = Integer.parseInt(scan.nextLine());
      } while (columnInput-1 < 0 || columnInput-1 >= (tableColumns.size() + foreignColumns.size()));

        return columnInput;
    }

    public void getTableForeignColumns(ArrayList<String> relations) throws SQLException {

        tableColumns = new ArrayList<String>();
        foreignColumns = new ArrayList<String>();

        for (int i = 0; i < relations.size(); i++) {
          String table = relations.get(i);
          if (table.equals(tables.get(tableInput-1))) {
            // Displays all columns in table and gets input form user
            ResultSet rs1 = statement.executeQuery("SELECT * FROM "+tables.get(tableInput-1)+" LIMIT 1");
            ResultSetMetaData rsmd = rs1.getMetaData();
            // Makes sure that the column is a number
            for (int k = 1; k <= rsmd.getColumnCount(); k++) {
              if (rsmd.getColumnType(k) == 3 || rsmd.getColumnType(k) == 4) { // It is a number type
                // Adds valid columns to array list
                tableColumns.add(rsmd.getColumnName(k));
              }
            }
          } else {
            // Displays all columns in table and gets input form user
            ResultSet rs1 = statement.executeQuery("SELECT * FROM "+relations.get(i)+" LIMIT 1");
            ResultSetMetaData rsmd = rs1.getMetaData();
            valid_columns = new ArrayList<String>();
            // Makes sure that the column is a number
            for (int k = 1; k <= rsmd.getColumnCount(); k++) {
              if (rsmd.getColumnType(k) == 3 || rsmd.getColumnType(k) == 4) { // It is a number type
                // Adds valid columns to array list
                foreignColumns.add(relations.get(i) + " " + "avg("+rsmd.getColumnName(k)+")");
                foreignColumns.add(relations.get(i) + " " + "sum("+rsmd.getColumnName(k)+")");
              }
            }
          }
        }
    }

    public ArrayList<String> getRelations() throws SQLException {
        ResultSet set = null;
        DatabaseMetaData dmd = conn.getMetaData();
        set = dmd.getPrimaryKeys(null, null, tables.get(tableInput-1));
        keys = new ArrayList<String>();
        while (set.next()) {
          String columnName = set.getString("COLUMN_NAME");
          keys.add(columnName);
          System.out.println(columnName);
        }

        ArrayList<String> relations = new ArrayList<String>();
        relations.add(tables.get(tableInput-1));

        for (int i = 0; i < tables.size(); i++) {
          try {
            set = dmd.getExportedKeys(conn.getCatalog(), null, tables.get(i));
            while (set.next()) {
              String fkTableName = set.getString("FKTABLE_NAME");
              String fkColumnName = set.getString("FKCOLUMN_NAME");
              for (int k = 0; k < keys.size(); k++) {
                if (fkColumnName.equals(keys.get(k))) {
                  relations.add(fkTableName);
                }
              }
            }
          } catch (Exception e) {
            continue;
          }
        }
        return relations;
    }

    public void createLineGraph(Object[][] data, int axis[], String input) throws SQLException {
        // Creates graph with data and labels, and then displays it
        Graph g = new LineGraph(databases.get(Integer.parseInt(input)-1));
        g.setData((Object[][])data);
        System.out.println("Would you like to smooth your data to see the general trend? (yes/no)");
        if (scan.nextLine().equals("yes")) {
          System.out.println("How many points would you like your smooth graph to be?");
          g.smoothData(scan.nextInt());
        }
        g.setXAxis(valid_columns.get(axis[0] - 1));
        g.setYAxis(valid_columns.get(axis[1] - 1));
        g.start();
    }

    public Object[][] getData(int[] axis) throws SQLException {
        // Gets amount of entries in table for data array
        ResultSet rs2 = statement.executeQuery("Select COUNT(*) AS total FROM "+tables.get(tableInput - 1));
        int count = rs2.getInt("total");
        Object[][] data = new Object[count][2];

        System.out.println("valid_columns: "+valid_columns);

        // Executes correct query
        ResultSet rs3 = statement.executeQuery("SELECT "+valid_columns.get(axis[0] - 1)+", "+valid_columns.get(axis[1]-1)+" FROM "+tables.get(tableInput-1)+" ORDER BY "+valid_columns.get(axis[0] - 1));
        // Goes through every entry and enters it into data array
        for (int i = 0; i < data.length; i++) {
          data[i][0] = (Object)rs3.getDouble(valid_columns.get(axis[0] - 1));
          data[i][1] = (Object)rs3.getDouble(valid_columns.get(axis[1] - 1));
          rs3.next();
        }

        return data;
    }

    public int[] getColumns() throws SQLException {

        // Displays all columns in table and gets input from user
        ResultSet rs1 = statement.executeQuery("SELECT * FROM "+tables.get(tableInput-1)+" LIMIT 1");
        ResultSetMetaData rsmd = rs1.getMetaData();
        valid_columns = new ArrayList<String>();
        // Makes sure that the column is a number
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
          if (rsmd.getColumnType(i) == 3 || rsmd.getColumnType(i) == 4) { // It is a number type
            // Adds valid columns to array list
            valid_columns.add(rsmd.getColumnName(i));
          }
        }

        // X-axis columns and user input
        System.out.println("\n\n");
        for (int i = 0; i < valid_columns.size(); i++) {
          System.out.println((i+1)+": "+valid_columns.get(i));
        }
        System.out.println("Which column would you like to be on the x-axis?");
        int x_axis;
        do {
          x_axis = Integer.parseInt(scan.nextLine());
        } while (x_axis - 1 < 0 || x_axis - 1 >= valid_columns.size());

        // Y-axis columns and user input
        System.out.println("\n\n");
        for (int i =0 ; i < valid_columns.size(); i++) {
          System.out.println((i+1)+": "+valid_columns.get(i));
        }
        System.out.println("Which column would you like to be on the y-axis?");
        int y_axis;
        do {
          y_axis = Integer.parseInt(scan.nextLine());
        } while (y_axis - 1 < 0 || y_axis - 1 >= valid_columns.size());

        int[] axis = {x_axis, y_axis};

        return axis;

    }

    public int getGraphType() {
        // Select type of Graph
        System.out.println("Which type of graph would you like to create?\n1: Line Graph\n2: Bar Graph");
        int type;
        do {
          type = Integer.parseInt(scan.nextLine());
        } while (type != 1 && type != 2);

        return type;
    }

    public int getTable() throws SQLException {

        // Get available tables
        System.out.println("Available tables:");
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);
        tables = new ArrayList<String>();
        int j = 1;
        // Adds each table into array list
        while (rs.next()) {
          tables.add(rs.getString(3));
          System.out.println(j+": "+rs.getString(3));
          j++;
        }
        // Gets user input for table input
        System.out.println("Which table would you like to connect to?");
        do { // Makes sure input is valid
          tableInput = Integer.parseInt(scan.nextLine());
      } while (tableInput-1 < 0 || tableInput-1 >= tables.size());

      System.out.println("\n\n");

      return tableInput;

    }

    public void connect(String input) throws SQLException {

        // Connects to user input database
        conn = DriverManager.getConnection("jdbc:sqlite:Databases/"+databases.get(Integer.parseInt(input)-1));
        System.out.println("Connection Established!\n\n");
        statement = conn.createStatement();
        statement.setQueryTimeout(30);

    }

    public String getInput() {

      // Gets all files in current directory
      File folder = new File("Databases/");
      File[] files = folder.listFiles();

      // Adds and displays files that are sqlite3 files
      databases = new ArrayList<String>();
      for (int i = 0; i < files.length; i++) {
        String[] name = files[i].getName().split("\\.");
        if (name[name.length - 1].equals("sqlite3")) {
          databases.add(files[i].getName());
          System.out.println(databases.size()+": "+files[i].getName());
        }
      }
      // Makes sure there is at least one database
      if (databases.size() == 0) {
        System.out.println("There are no databases to connect to!");
        return "-1";
      }

      // Gets input from user for specified database
      Scanner scan = new Scanner(System.in);
      String input;
      do {
        System.out.println("What database would you like to connect to?");
        input = scan.nextLine();
      } while (Integer.parseInt(input)-1 < 0 || Integer.parseInt(input)-1 >= databases.size());

      return input;

    }

}
