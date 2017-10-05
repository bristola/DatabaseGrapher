package DatabaseGrapher.Graphing;

import java.awt.*;
import java.util.Formatter;
import javax.swing.*;

public class LineGraph extends Graph {

  public LineGraph(String title) {
    super(title);
  }

  public void setData(Object data[][]) {
    this.data = data;
    for (int i = 0; i < data.length; i++) {
      if ((double)data[i][0] > maxX) {
        maxX = (double)data[i][0];
      }
      if ((double)data[i][1] > maxY) {
        maxY = (double)data[i][1];
      }
    }
  }

  public void plotData(Graphics g, Object[][] data) {
    int startingX = 80;
    int xAxisLength = window_width -80;
    for (int i = 0; i < data.length - 1; i++) {
      double x1, y1, x2, y2;

      x1 = ((double)data[i][0]/maxX)*(window_width-160) + 80;
      y1 = ((1-((double)data[i][1]/maxY))*(window_height - 160)) + 80;

      x2 = ((double)data[i+1][0]/maxX)*(window_width-160) + 80;
      y2 = ((1-((double)data[i+1][1]/maxY))*(window_height - 160)) + 80;

      g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
    }
  }

  public void smoothData(int points) {
    Object[][] d;
    smooth = true;
    if (data.length % points == 0) {
      d = new Object[points][2];
    } else {
      d = new Object[points][2];
    }
    int a = 0;
    for (int i = 0; i < data.length; i += data.length/points) {
      double sumx = 0;
      double sumy = 0;
      int count = 0;
      //System.out.println((a*(data.length/4))+" "+((a+1)*(data.length/4))+" "+(data.length));
      if (data.length - (a+1)*(data.length/points) < data.length / points) { // End
        for (int j = a*(data.length/points); j < data.length; j++) {
          sumx += (double) data[j][0];
          sumy += (double) data[j][1];
          count++;
        }
        i = data.length;
      } else {
        for (int j = a*(data.length/points); j < (a+1)*(data.length/points) && j < data.length; j++) {
          sumx += (double) data[j][0];
          sumy += (double) data[j][1];
          count++;
        }
      }
      double avgx = sumx/count;
      double avgy = sumy/count;
      System.out.println(avgx+" "+avgy);
      d[a][0] = (Object) avgx;
      d[a][1] = (Object) avgy;
      a++;
    }
    smoothedData = d;
  }

  public void placeNumbers(Graphics g) {
    // Y axis numbers
    for (double i = 0; i <= 1; i += .2) {
      double number = maxY * i;
      double dist = ((1-((double)i))*(window_height - 160)) + 80;
      g.drawLine(80, (int)dist, 75, (int)dist);
      String numString;
      if (number < 1) {
        numString = ("" + number).substring(0, 3);
      } else {
        numString = "" + (int)number;
      }
      g.drawString(numString, 50, (int)dist);
    }

    // X axis numbers
    for (double i = 0; i <= 1; i += .1) {
      double number = maxX * i;
      double dist = ((double)i)*(window_width-160) + 80;
      g.drawLine((int)dist, window_height-75,(int)dist, window_height-80);
      String numString;
      if (number < 1) {
        numString = ("" + number).substring(0, 3);
      } else {
        numString = "" + (int)number;
      }
      g.drawString(numString, (int)dist, window_height-60);
    }
  }

  public void start() {
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setContentPane(new LineGraph(title));
    frame.pack();
    frame.setVisible(true);
  }

}
