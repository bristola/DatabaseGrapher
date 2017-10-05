# DatabaseGrapher

All work done by Austin Bristol

This program will read any sqlite3 database placed in the *Databases* directory.
Allows user to select a table, and two columns from that table.
It then displays the data in a graph showing the relation of those two columns.
The graph is created using a custom Graph library made by me. You can either
create a line graph or a bar graph. The bar graph option detects foreign key
relationships between tables, and can group statistics by multiple tables using
this relation.

There has been supplied an demo sqlite3 database called *MLBData.sqlite3* that
can be used to test the program. Also, the *sqlite-jdbc-3.16.1.jar* JAR file is
required to run the program. This is also included in the repository in order
to allow the execution of the program. This JAR is from the downloads page of
the repository https://bitbucket.org/xerial/sqlite-jdbc. You must have this
included on your class path. You can either download it yourself or use the
supplied version of the jar file.

## Execution

  1. Traverse to the project's root directory
  2. compile with `javac -cp . DatabaseGrapher\Graphing\*.java DatabaseGrapher\Driver\*.java DatabaseGrapher\*.java`
  3. run with `java -classpath ".;sqlite-jdbc-3.16.1.jar" DatabaseGrapher.Main`

### Line Graph Example

![alt text](https://github.com/bristola/DatabaseGrapher/blob/master/ExampleOutput/LineGraph.PNG "Line Graph")

### Bar Graph Example

![alt text](https://github.com/bristola/DatabaseGrapher/blob/master/ExampleOutput/BarGraph.PNG "Bar Graph")
