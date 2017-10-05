/**
  All work done by Austin Bristol

  This program will read any sqlite3 database that is in the current directory.
  Allows user to select a table, and two columns from that table.
  It then displays the data in a graph showing the relation of those two tables.
  The graph is created using a custom Graph library made by me.
  You can either create a line graph or a bar graph.

  To run this program:
    1: Traverse to the project's root directory
    2: compile with javac -cp . DatabaseGrapher\Graphing\*.java DatabaseGrapher\Driver\*.java DatabaseGrapher\*.java
    3: run with java -classpath ".;sqlite-jdbc-3.16.1.jar" DatabaseGrapher.Main
**/

package DatabaseGrapher;

import DatabaseGrapher.Driver.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Driver driver = new Driver();
        driver.run();
    }
}
