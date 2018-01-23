package DatabaseGrapher.Driver;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public void run() throws IOException {

        DatabaseTools du = new DatabaseTools();

        try {

            List<String> databases = getDatabases();
            printList(databases, "There are no databases to connect to!");
            int input1 = getInput(databases.size(), "What database would you like to connect to?");
            if (input1 == -1) {
                return;
            }

            Connection conn = du.getConnection(input1, databases);

            List<String> tables = du.getTables(conn);
            printList(tables, "There are no tables to connect to!");
            int input2 = getInput(tables.size(), "Which table would you like to connect to?");
            if (input2 == -1) {
                return;
            }

            int input3 = getInput(2, "Which type of graph would you like to create?\n1: Line Graph\n2: Bar Graph");

            switch(input3) {
                case 1:
                    executeLineGraph(conn, tables, input2, databases.get(input1-1));
                    break;
                case 2:
                    executeBarGraph(conn, tables, input2);
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

    public void executeLineGraph(Connection conn, List<String> tables, int input2, String title) {

        DatabaseTools du = new DatabaseTools();

        try {
            List<String> columns = du.getColumns(conn, tables.get(input2-1));

            printList(columns, "There are no columns in the specified table!");
            int x_axis = getInput(columns.size(), "Which column would you like to be on the x-axis?");
            int y_axis = getInput(columns.size(), "Which column would you like to be on the y-axis?");
            if (x_axis == -1 || y_axis == -1) {
                return;
            }

            Object[][] data = du.getData(conn, tables.get(input2 - 1), columns.get(x_axis-1), columns.get(y_axis-1));
            createLineGraph(data, x_axis, y_axis, title, columns);
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

    public void executeBarGraph(Connection conn, List<String> tables, int input2) {

        DatabaseTools du = new DatabaseTools();

        try {
            List<String> relations = du.getRelations(conn, tables.get(input2-1), tables);
            List<String> columns2 = du.getColumns(conn, tables.get(input2-1));
            List<String> foreignColumns = du.getTableForeignColumns(conn, relations);
            printList(columns2, foreignColumns, "There are no columns for the specified table and its relations!");
            int barInput = getInput(columns2.size()+foreignColumns.size(), "Which value would you like to graph?");
            if (barInput == -1) {
                return;
            }
            Object[][] data2 = barInput >= columns2.size() ? du.getForeignData(conn, tables.get(input2-1), foreignColumns.get(barInput-1-columns2.size())) : du.getBarData(conn, tables.get(input2-1),columns2.get(barInput-1));
            String yAxis = barInput >= columns2.size() ? foreignColumns.get(barInput-1-columns2.size()).split(" ")[1] : columns2.get(barInput-1);
            List<String> keys = du.getPrimaryKeys(conn, tables.get(input2 - 1));
            createBarGraph(tables.get(input2-1), yAxis, keys.get(0), data2);
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

    public List<String> getDatabases() {

        // Gets all files in current directory
        File folder = new File("Databases/");
        File[] files = folder.listFiles();
        List<String> databases = new ArrayList<String>();

        for (int i = 0; i < files.length; i++) {
          String[] name = files[i].getName().split("\\.");
          if (name[name.length - 1].equals("sqlite3")) {
            databases.add(files[i].getName());
          }
        }

        return databases;

    }

    public void printList(List<String> lists, String errorMessage) {

        System.out.println("\n\n");

        if (lists.size() == 0) {
            System.out.println(errorMessage);
            return;
        }
        int i = 1;
        for (String l : lists) {
            System.out.println(i+++": "+l);
        }

    }

    public void printList(List<String> list1, List<String> list2, String errorMessage) {

        System.out.println("\n\n");

        if (list1.size() == 0 && list2.size() == 0) {
            System.out.println(errorMessage);
            return;
        }
        int i = 1;
        for (String l : list1) {
            System.out.println(i+++": "+l);
        }
        for (String l : list2) {
            System.out.println(i+++": "+l);
        }

    }

    public int getInput(int max, String message) {

        Scanner scan = new Scanner(System.in);

        String input;
        if (max == 0) {
            return -1;
        }
        do {
          System.out.println("\n"+message);
          input = scan.nextLine();
        } while (Integer.parseInt(input)-1 < 0 || Integer.parseInt(input)-1 >= max);

        return Integer.parseInt(input);
    }

    public void createLineGraph(Object[][] data, int x_axis, int y_axis, String title, List<String> valid_columns) throws SQLException{

        Scanner scan = new Scanner(System.in);

        Graph g = new LineGraph(title);
        g.setData((Object[][])data);
        System.out.println("Would you like to smooth your data to see the general trend? (yes/no)");
        if (scan.nextLine().equals("yes")) {
          System.out.println("How many points would you like your smooth graph to be?");
          g.smoothData(scan.nextInt());
        }
        g.setXAxis(valid_columns.get(x_axis - 1));
        g.setYAxis(valid_columns.get(y_axis - 1));
        g.start();

    }

    public void createBarGraph(String name, String yAxis, String xAxis, Object[][] data) {

        Graph g = new BarGraph(name);

        g.setYAxis(yAxis);
        g.setXAxis(xAxis);
        g.setData(data);

        g.start();

    }

}
