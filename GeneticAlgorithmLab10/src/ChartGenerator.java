import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChartGenerator {

    // 1. ГРАФИК ФУНКЦИИ с точками (уже был)
    public static void saveFunctionChart(double optimalX, double optimalY,
                                         List<Double> historyX, List<Double> historyY,
                                         String filename) {
        int width = 900;
        int height = 600;
        int padding = 60;
        int plotWidth = width - 2 * padding;
        int plotHeight = height - 2 * padding;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(padding, padding, plotWidth, plotHeight);

        double minX = 2.5;
        double maxX = 7.5;
        double minY = -1.0;
        double maxY = 8.0;

        // Сетка
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= 10; i++) {
            int x = padding + (int) ((i / 10.0) * plotWidth);
            g2d.drawLine(x, padding, x, padding + plotHeight);
            int y = padding + (int) ((i / 10.0) * plotHeight);
            g2d.drawLine(padding, y, padding + plotWidth, y);
        }

        // Подписи осей
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        for (double x = 3.0; x <= 7.0; x += 0.5) {
            int xPix = padding + (int) (((x - minX) / (maxX - minX)) * plotWidth);
            g2d.drawLine(xPix, padding + plotHeight - 5, xPix, padding + plotHeight + 5);
            g2d.drawString(String.format("%.1f", x), xPix - 10, padding + plotHeight + 20);
        }
        for (double y = 0.0; y <= 7.0; y += 1.0) {
            int yPix = padding + plotHeight - (int) (((y - minY) / (maxY - minY)) * plotHeight);
            g2d.drawLine(padding - 5, yPix, padding + 5, yPix);
            g2d.drawString(String.format("%.0f", y), padding - 35, yPix + 5);
        }

        g2d.drawString("x", width / 2, height - 10);
        g2d.rotate(-Math.PI / 2);
        g2d.drawString("f(x)", -height / 2, 20);
        g2d.rotate(Math.PI / 2);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Минимизация функции f(x) = x² - 5x + 6", width / 2 - 200, 30);

        // Функция
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(2));
        int prevX = -1, prevY = -1;
        for (double x = minX; x <= maxX; x += 0.02) {
            double y = x * x - 5 * x + 6;
            int xPix = padding + (int) (((x - minX) / (maxX - minX)) * plotWidth);
            int yPix = padding + plotHeight - (int) (((y - minY) / (maxY - minY)) * plotHeight);
            yPix = Math.max(padding, Math.min(padding + plotHeight, yPix));
            if (prevX != -1) {
                g2d.drawLine(prevX, prevY, xPix, yPix);
            }
            prevX = xPix;
            prevY = yPix;
        }

        // История поколений
        if (historyX != null && !historyX.isEmpty()) {
            g2d.setColor(new Color(255, 165, 0));
            for (int i = 0; i < historyX.size(); i++) {
                double x = historyX.get(i);
                double y = historyY.get(i);
                int xPix = padding + (int) (((x - minX) / (maxX - minX)) * plotWidth);
                int yPix = padding + plotHeight - (int) (((y - minY) / (maxY - minY)) * plotHeight);
                int size = 6;
                int[] xPoints = {xPix, xPix + size, xPix, xPix - size};
                int[] yPoints = {yPix - size, yPix, yPix + size, yPix};
                g2d.fillPolygon(xPoints, yPoints, 4);
            }
        }

        // Оптимум
        g2d.setColor(Color.RED);
        int optXPix = padding + (int) (((optimalX - minX) / (maxX - minX)) * plotWidth);
        int optYPix = padding + plotHeight - (int) (((optimalY - minY) / (maxY - minY)) * plotHeight);
        g2d.fillOval(optXPix - 6, optYPix - 6, 12, 12);
        g2d.setColor(Color.WHITE);
        g2d.fillOval(optXPix - 3, optYPix - 3, 6, 6);

        // Легенда
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(Color.BLUE);
        g2d.drawLine(width - 150, 50, width - 120, 50);
        g2d.setColor(Color.BLACK);
        g2d.drawString("f(x) = x² - 5x + 6", width - 145, 65);
        g2d.setColor(new Color(255, 165, 0));
        int[] xLeg = {width - 135, width - 129, width - 135, width - 141};
        int[] yLeg = {90, 96, 102, 96};
        g2d.fillPolygon(xLeg, yLeg, 4);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Лучшие решения по поколениям", width - 145, 110);
        g2d.setColor(Color.RED);
        g2d.fillOval(width - 138, 120, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString(String.format("Найденный минимум (%.2f; %.3f)", optimalX, optimalY), width - 145, 140);

        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.setColor(Color.GRAY);
        g2d.drawString("Параметры: популяция=10, поколения=50, кроссинговер=70%, мутация=20%", padding, height - 25);
        g2d.drawString("Операторы: кроссинговер Фибоначчи, мутация транслокацией", padding, height - 10);

        g2d.dispose();
        saveImage(image, filename);
    }

    // 2. ГРАФИК СХОДИМОСТИ (f(x) от поколения)
    public static void saveConvergenceChart(List<Double> history, String filename) {
        int width = 800;
        int height = 500;
        int padding = 60;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(padding, padding, width - 2 * padding, height - 2 * padding);

        double maxY = history.stream().max(Double::compare).orElse(1.0);
        double minY = history.stream().min(Double::compare).orElse(0.0);
        int generations = history.size();

        // Сетка
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= 10; i++) {
            int x = padding + (int) ((i / 10.0) * (width - 2 * padding));
            g2d.drawLine(x, padding, x, height - padding);
            int y = padding + (int) ((i / 10.0) * (height - 2 * padding));
            g2d.drawLine(padding, y, width - padding, y);
        }

        // Подписи осей
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        for (int i = 0; i <= 50; i += 10) {
            int x = padding + (int) ((i / 50.0) * (width - 2 * padding));
            g2d.drawLine(x, height - padding - 5, x, height - padding + 5);
            g2d.drawString(String.valueOf(i), x - 10, height - padding + 20);
        }
        for (double y = 0; y <= 0.6; y += 0.1) {
            int yPix = padding + (int) ((1 - (y - minY) / (maxY - minY)) * (height - 2 * padding));
            g2d.drawLine(padding - 5, yPix, padding + 5, yPix);
            g2d.drawString(String.format("%.1f", y), padding - 35, yPix + 5);
        }

        g2d.drawString("Поколение", width / 2 - 30, height - 10);
        g2d.rotate(-Math.PI / 2);
        g2d.drawString("Значение f(x) лучшей особи", -height / 2, 25);
        g2d.rotate(Math.PI / 2);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Сходимость генетического алгоритма", width / 2 - 150, 30);

        // Линия сходимости
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));
        int prevX = -1, prevY = -1;
        for (int i = 0; i < generations; i++) {
            int x = padding + (int) ((double) i / generations * (width - 2 * padding));
            int y = padding + (int) ((1 - (history.get(i) - minY) / (maxY - minY)) * (height - 2 * padding));
            if (prevX != -1) {
                g2d.drawLine(prevX, prevY, x, y);
            }
            // Рисуем точки
            g2d.fillOval(x - 3, y - 3, 6, 6);
            prevX = x;
            prevY = y;
        }

        g2d.dispose();
        saveImage(image, filename);
    }

    // 3. ГРАФИК ВЛИЯНИЯ ПАРАМЕТРА (баровый)
    public static void saveParameterChart(String[] labels, double[] values,
                                          String xLabel, String yLabel,
                                          String title, String filename) {
        int width = 700;
        int height = 500;
        int padding = 60;
        int barWidth = (width - 2 * padding) / labels.length - 10;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(padding, padding, width - 2 * padding, height - 2 * padding);

        double maxValue = 0;
        for (double v : values) {
            if (v > maxValue) maxValue = v;
        }
        maxValue = maxValue * 1.2;

        // Сетка
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= 5; i++) {
            int y = padding + (int) ((1 - i / 5.0) * (height - 2 * padding));
            g2d.drawLine(padding, y, width - padding, y);
            g2d.drawString(String.format("%.2f", maxValue * i / 5), padding - 40, y + 5);
        }

        // Столбцы
        g2d.setColor(new Color(70, 130, 200));
        for (int i = 0; i < labels.length; i++) {
            int x = padding + i * (barWidth + 15) + 15;
            int barHeight = (int) ((values[i] / maxValue) * (height - 2 * padding));
            int y = height - padding - barHeight;
            g2d.fillRect(x, y, barWidth, barHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawString(labels[i], x + barWidth / 4, height - padding + 15);
            g2d.drawString(String.format("%.3f", values[i]), x + barWidth / 4, y - 5);
            g2d.setColor(new Color(70, 130, 200));
        }

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString(title, width / 2 - 100, 30);
        g2d.drawString(xLabel, width / 2 - 50, height - 10);
        g2d.rotate(-Math.PI / 2);
        g2d.drawString(yLabel, -height / 2, 25);
        g2d.rotate(Math.PI / 2);

        g2d.dispose();
        saveImage(image, filename);
    }

    private static void saveImage(BufferedImage image, String filename) {
        try {
            ImageIO.write(image, "PNG", new File(filename));
            System.out.println(" Сохранен: " + filename);
        } catch (IOException e) {
            System.err.println(" Ошибка сохранения " + filename + ": " + e.getMessage());
        }
    }
}