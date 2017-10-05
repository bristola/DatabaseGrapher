package DatabaseGrapher.Graphing;

import java.awt.*;
import java.util.Formatter;
import javax.swing.*;

public class BarGraph extends Graph {

  public BarGraph(String title) {
    super(title);
  }

  public void setData(Object data[][]) {
    this.data = data;
    for (int i = 0; i < data.length; i++) {
      if ((double)data[i][1] > maxY) {
        maxY = (double)data[i][1];
      }
    }
    if (data.length > 12) {
      window_width += 500;
    }
  }

  public void plotData(Graphics g, Object[][] data) {
    g.setColor(new Color(0, 128, 255));
    int totalEntries = data.length;
    double percent = (data.length-1)*((double)1/data.length);
    double widthOfBars = (1/(double)totalEntries)*(window_width-160)*.6;
    int axisLength = window_width - 160;
    double offset = (((double)axisLength - (percent*axisLength+widthOfBars))/2)+80;
    percent = 0;
    for (int i = 0; i < data.length; i++) {
      double x1, y1, x2, y2;
      x1 = percent*axisLength + offset;
      y1 = (1 - ((double)data[i][1]/maxY))*(window_height-160) + 80;
      x2 = widthOfBars;
      y2 =(((double)data[i][1]/maxY))*(window_height-160);
      g.fillRect((int)x1, (int)y1, (int)x2, (int)y2);
      percent = (double)(i+1)*(1/(double)totalEntries);
    }
  }

  public void placeNumbers(Graphics g) {
    // Y - axis
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

    // X - axis
    int totalEntries = data.length;
    double percent = (data.length-1)*((double)1/data.length);
    double widthOfBars = (1/(double)totalEntries)*(window_width-160)*.6;
    int axisLength = window_width - 160;
    double offset = (((double)axisLength - (percent*axisLength+widthOfBars))/2)+80;
    percent = 0;
    Graphics2D g2 = (Graphics2D) g;
    g2.setFont(new Font("Serif", Font.PLAIN, 11));

    for (int i = 0; i < data.length; i++) {
      double x = percent*axisLength + offset + (widthOfBars / 2);
      double y = window_height - 35;
      g2.translate((float)x,(float)y);
      g2.rotate(Math.toRadians(-90));
      g2.drawString((String)data[i][0], 0, 0);
      g2.rotate(-Math.toRadians(-90));
      g2.translate(-(float)x,-(float)y);

      percent = (double)(i+1)*(1/(double)totalEntries);
    }

  }

  public void start() {
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setContentPane(new BarGraph(title));
    frame.pack();
    frame.setVisible(true);
  }

  public void smoothData(int points) {
    System.out.println("Cannot smooth bar graph data");
    return;
  }

}
