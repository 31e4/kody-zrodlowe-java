import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class MathGraphPlotter extends JFrame {

    public MathGraphPlotter() {
        setTitle("Math Graph Plotter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new PlotPanel());
        setLocationRelativeTo(null); // Center the window
    }

    private class PlotPanel extends JPanel {
        // Define the range for x and y axes
        private final double xMin = -Math.PI * 2;
        private final double xMax = Math.PI * 2;
        private final double yMin = -1.5;
        private final double yMax = 1.5;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Enable better graphics
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Draw axes
            drawAxes(g2, width, height);

            // Plot the function y = sin(x)
            plotFunction(g2, width, height);
        }

        private void drawAxes(Graphics2D g2, int width, int height) {
            // Map logical x,y to pixel x,y
            // Draw X axis
            int xAxisY = mapY(0, height);
            g2.drawLine(0, xAxisY, width, xAxisY);
            // Draw Y axis
            int yAxisX = mapX(0, width);
            g2.drawLine(yAxisX, 0, yAxisX, height);
        }

        private void plotFunction(Graphics2D g2, int width, int height) {
            double step = (xMax - xMin) / width; // step size based on pixels for smoothness
            double prevX = xMin;
            double prevY = Math.sin(prevX);
            int prevPixelX = mapX(prevX, width);
            int prevPixelY = mapY(prevY, height);

            for (int pixelX = 1; pixelX <= width; pixelX++) {
                double x = xMin + (pixelX * step);
                double y = Math.sin(x);
                int pixelY = mapY(y, height);

                g2.draw(new Line2D.Double(prevPixelX, prevPixelY, pixelX, pixelY));

                prevPixelX = pixelX;
                prevPixelY = pixelY;
            }
        }

        private int mapX(double x, int width) {
            return (int) ((x - xMin) / (xMax - xMin) * width);
        }

        private int mapY(double y, int height) {
            // Invert y because graphics coordinate system starts at top-left
            return (int) ((yMax - y) / (yMax - yMin) * height);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MathGraphPlotter frame = new MathGraphPlotter();
            frame.setVisible(true);
        });
    }
}
