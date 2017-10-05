package DatabaseGrapher.Graphing;

import java.awt.*;
import java.util.Formatter;
import javax.swing.*;
import java.lang.Math;
import java.awt.geom.AffineTransform;

public abstract class Graph extends JPanel {
   static int window_width = 1000;
   static int window_height = 600;
   static String title;
   static String x_axis_label = "defaultX";
   static String y_axis_label = "defaultY";
   static Object data[][];
   static Object smoothedData[][];
   static double maxX;
   static double maxY;
   static boolean smooth = false;

   public Graph(String title) {
      this.setPreferredSize(new Dimension(window_width, window_height));
      this.title = title;
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);    // Paint background
      g.setColor(Color.white);
      g.fillRect(0, 0, window_width, window_height);
      g.setColor(new Color(0, 128, 255));
      drawAxis(g);
      plotData(g, data);
      placeNumbers(g);
      placeTitle(g);
      if (smooth) {
        g.setColor(Color.red);
        plotData(g, smoothedData);
      }
   }

   public void setXAxis(String x) {
     x_axis_label = x;
   }

   public void setYAxis(String y) {
     y_axis_label = y;
   }

   public void placeTitle(Graphics g) {
     g.setColor(new Color(0, 128, 255));
     g.setFont(new Font("Serif", Font.BOLD, 20));
     g.drawString(title, window_width / 2 - 10, 30);
   }

   public static void drawAxis(Graphics g) {
     // Sets color of axis
     g.setColor(new Color(0, 128, 255));

     // Draws lines for axis
     g.drawLine(80, window_height - 80, window_width - 80, window_height - 80);
     g.drawLine(80, window_height - 80, 80, 80);

     // Draws labels
     g.drawString(x_axis_label, window_width / 2 - 10, window_height - 20);

     Graphics2D g2 = (Graphics2D) g;
     double x = 20;
     double y = window_height / 2 + 10;
     g2.translate((float)x,(float)y);
     g2.rotate(Math.toRadians(-90));
     g2.drawString(y_axis_label, 0, 0);
     g2.rotate(-Math.toRadians(-90));
     g2.translate(-(float)x,-(float)y);
   }

   public abstract void start();

   public abstract void setData(Object data[][]);

   public abstract void plotData(Graphics g, Object[][] data);

   public abstract void placeNumbers(Graphics g);

   public abstract void smoothData(int points);
}
