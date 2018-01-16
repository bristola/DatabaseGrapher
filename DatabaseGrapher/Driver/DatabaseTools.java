package DatabaseGrapher.Driver;

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

public class DatabaseTools {

    public Connection getConnection(int input, ArrayList<String> databases) throws SQLException {

        Connection conn = DriverManager.getConnection("jdbc:sqlite:Databases/"+databases.get(input-1));
        System.out.println("Connection Established!\n\n");
        return conn;

    }

    public Statement getStatement(Connection conn) throws SQLException {

        Statement statement = conn.createStatement();
        statement.setQueryTimeout(30);
        return statement;

    }

    public ArrayList<String> getTables(Connection conn) throws SQLException {

        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);
        ArrayList<String> tables = new ArrayList<String>();

        while (rs.next()) {
            tables.add(rs.getString(3));
        }

        return tables;

    }

    public ArrayList<String> getColumns(Statement statement, String tableName) throws SQLException {

        // Displays all columns in table and gets input from user
        ResultSet rs1 = statement.executeQuery("SELECT * FROM "+tableName+" LIMIT 1");
        ResultSetMetaData rsmd = rs1.getMetaData();
        ArrayList<String> valid_columns = new ArrayList<String>();
        // Makes sure that the column is a number
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
          if (rsmd.getColumnType(i) == 3 || rsmd.getColumnType(i) == 4) { // It is a number type
            // Adds valid columns to array list
            valid_columns.add(rsmd.getColumnName(i));
          }
        }

        return valid_columns;

    }

    public Object[][] getData(int x_axis, int y_axis, Statement statement, String tableName, ArrayList<String> valid_columns) throws SQLException {

        ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS total FROM "+tableName);
        int count = rs.getInt("total");
        Object[][] data = new Object[count][2];

        rs =statement.executeQuery("SELECT "+valid_columns.get(x_axis - 1)+", "+valid_columns.get(y_axis-1)+" FROM "+tableName+" ORDER BY "+valid_columns.get(x_axis - 1));

        for (int i = 0; i < data.length; i++) {
          data[i][0] = (Object)rs.getDouble(valid_columns.get(x_axis - 1));
          data[i][1] = (Object)rs.getDouble(valid_columns.get(y_axis - 1));
          rs.next();
        }

        return data;

    }

    public ArrayList<String> getPrimaryKeys(Connection conn, String tableName) throws SQLException {

        DatabaseMetaData dmd = conn.getMetaData();
        ResultSet set = dmd.getPrimaryKeys(null, null, tableName);
        ArrayList<String> keys = new ArrayList<String>();

        while (set.next()) {
          String columnName = set.getString("COLUMN_NAME");
          keys.add(columnName);
        }

        return keys;

    }

    public ArrayList<String> getRelations(Connection conn, String tableName, ArrayList<String> tables) throws SQLException {

        ArrayList<String> keys = getPrimaryKeys(conn, tableName);
        ArrayList<String> relations = new ArrayList<String>();
        DatabaseMetaData dmd = conn.getMetaData();

        for (int i = 0; i < tables.size(); i++) {
          try {
            ResultSet set = dmd.getExportedKeys(conn.getCatalog(), null, tables.get(i));
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

    public ArrayList<String> getTableForeignColumns(ArrayList<String> relations, Statement statement) throws SQLException {

        ArrayList<String> foreignColumns = new ArrayList<String>();

        for (int i = 0; i < relations.size(); i++) {
          String table = relations.get(i);

            // Displays all columns in table and gets input form user
            ResultSet rs1 = statement.executeQuery("SELECT * FROM "+relations.get(i)+" LIMIT 1");
            ResultSetMetaData rsmd = rs1.getMetaData();
            // Makes sure that the column is a number
            for (int k = 1; k <= rsmd.getColumnCount(); k++) {
              if (rsmd.getColumnType(k) == 3 || rsmd.getColumnType(k) == 4) { // It is a number type
                // Adds valid columns to array list
                foreignColumns.add(relations.get(i) + " " + "avg("+rsmd.getColumnName(k)+")");
                foreignColumns.add(relations.get(i) + " " + "sum("+rsmd.getColumnName(k)+")");
              }
            }

        }

        return foreignColumns;

    }

    public Object[][] getForeignData(Connection conn, Statement statement, ArrayList<String> foreignColumns, String tableName, int index) throws SQLException{

        Object[][] data;

        ResultSet rs2 = statement.executeQuery("Select COUNT(*) AS total FROM "+tableName);
        int count = rs2.getInt("total");
        data = new Object[count][2];

        ArrayList<String> keys = getPrimaryKeys(conn, tableName);

        // Executes correct query
        ResultSet rs3 = statement.executeQuery("SELECT "+keys.get(0)+", "+foreignColumns.get(index).split(" ")[1]+" AS val FROM "+foreignColumns.get(index).split(" ")[0]+" GROUP BY "+keys.get(0));
        // Goes through every entry and enters it into data array
        for (int i = 0; i < data.length; i++) {
          data[i][0] = (Object)rs3.getString(keys.get(0));
          data[i][1] = (Object)rs3.getDouble("val");
          rs3.next();
        }

        return data;

    }

    public Object[][] getBarData(Connection conn, Statement statement, ArrayList<String> tableColumns, int index, String tableName) throws SQLException {

        Object[][] data;

        ResultSet rs2 = statement.executeQuery("Select COUNT(*) AS total FROM "+tableName);
        int count = rs2.getInt("total");
        data = new Object[count][2];

        ArrayList<String> keys = getPrimaryKeys(conn, tableName);

        // Executes correct query
        ResultSet rs3 = statement.executeQuery("SELECT "+keys.get(0)+", "+tableColumns.get(index)+" FROM "+tableName);
        // Goes through every entry and enters it into data array
        for (int i = 0; i < data.length; i++) {
          data[i][0] = (Object)rs3.getString(keys.get(0));
          data[i][1] = (Object)rs3.getDouble(tableColumns.get(index));
          rs3.next();
        }

        return data;

    }

}
