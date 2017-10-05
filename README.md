# DatabaseGrapher

All work done by Austin Bristol

This program will read any sqlite3 database placed in the *Databases* directory.
Allows user to select a table, and two columns from that table.
It then displays the data in a graph showing the relation of those two columns.
The graph is created using a custom Graph library made by me. You can either
create a line graph or a bar graph. The bar graph option detects foreign key
relationships between tables, and can group statistics by multiple tables using
this relation.

## Execution

  1. Traverse to the project's root directory
  2. compile with `javac -cp . DatabaseGrapher\Graphing\*.java DatabaseGrapher\Driver\*.java DatabaseGrapher\*.java`
  3. run with `java -classpath ".;sqlite-jdbc-3.16.1.jar" DatabaseGrapher.Main`

### Line Graph Example

![alt text](https://github.com/bristola/DatabaseGrapher/ExampleOutput/LineGraph.png "Line Graph")

### Bar Graph Example

![alt text](https://github.com/bristola/DatabaseGrapher/ExampleOutput/BarGraph.png "Bar Graph")
