package net.graysenko.com.MathProject;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Использую Jansi для цветного вывода и JLine для интерактивного ввода команд.
 * Поддерживает ввод формулы вида: y = a*(x - b)^n + d
 * Команды:
 *   plot <формула>     — построить новую функцию
 *   xrange <min> <max> — диапазон по X
 *   yrange <min> <max> — диапазон по Y
 *   size <width> <height> — изменить размеры канвы
 *   clear              — очистить экран
 *   exit               — выход
 */
@SpringBootApplication
public class ConsoleFunctionPlotterJansi {

    private static double a = 1;
    private static double b = 0;
    private static double n = 1;
    private static double d = 0;

    private static int height = 25;
    private static int width = 80;
    private static double yMax = 20, yMin = -10;
    private static double xMax = 10, xMin = -10;

    public static void main(String[] args) {
        SpringApplication.run(ConsoleFunctionPlotterJansi.class, args);
        AnsiConsole.systemInstall();
        LineReader reader = LineReaderBuilder.builder().build();

        printHelp();
        draw();

        while (true) {
            String line = reader.readLine(ansi().fgYellow().a("> ").reset().toString());
            if (line == null) continue;
            line = line.trim();
            if (line.equalsIgnoreCase("exit")) break;
            if (line.isEmpty()) continue;

            if (line.startsWith("plot")) {
                parseFormula(line.substring(4).trim());
                draw();
            } else if (line.startsWith("xrange")) {
                String[] p = line.split("\\s+");
                if (p.length == 3) {
                    xMin = Double.parseDouble(p[1]);
                    xMax = Double.parseDouble(p[2]);
                }
                draw();
            } else if (line.startsWith("yrange")) {
                String[] p = line.split("\\s+");
                if (p.length == 3) {
                    yMin = Double.parseDouble(p[1]);
                    yMax = Double.parseDouble(p[2]);
                }
                draw();
            } else if (line.startsWith("size")) {
                String[] p = line.split("\\s+");
                if (p.length == 3) {
                    width = Integer.parseInt(p[1]);
                    height = Integer.parseInt(p[2]);
                }
                draw();
            } else if (line.equals("clear")) {
                System.out.print(ansi().eraseScreen().cursor(0,0));
            } else if (line.equals("help")) {
                printHelp();
            } else {
                System.out.println("Unknown command. Type 'help'.");
            }
        }

        AnsiConsole.systemUninstall();
    }

    private static void printHelp() {
        System.out.println("Commands:");
        System.out.println("  plot y = a*(x - b)^n + d");
        System.out.println("  xrange <min> <max>");
        System.out.println("  yrange <min> <max>");
        System.out.println("  size <width> <height>");
        System.out.println("  clear");
        System.out.println("  exit");
    }

    private static void parseFormula(String expr) {
        expr = expr.replaceAll("\\s+", "");
        Pattern p = Pattern.compile(
                "y=([+-]?\\d+(?:\\.\\d+)?)"                    // a
                        + "\\*?\\(x([+-]\\d+(?:\\.\\d+)?)?\\)" // (x ± b)
                        + "\\^([+-]?\\d+(?:\\.\\d+)?)"         // ^n (включая отрицательные и дробные)
                        + "([+-]\\d+(?:\\.\\d+)?)?"            // d
        );
        Matcher m = p.matcher(expr);
        if (m.matches()) {
            // Коэффициент a
            a = Double.parseDouble(m.group(1));
            b = 0;
            if (m.group(2) != null) {
                b = -Double.parseDouble(m.group(2));
                // Минус потому что в формуле (x - b), а строка хранится как (x+1)
            }
            n = Double.parseDouble(m.group(3));
            d = 0;
            if (m.group(4) != null) {
                d = Double.parseDouble(m.group(4));
            }

            System.out.println("Parsed: y = " + a + "*(x - " + b + ")^" + n + " + " + d);
        } else {
            System.out.println("Could not parse formula. Example: y = -1*(x-2)^2+3");
        }
    }


    private static void draw() {
        System.out.print(ansi().eraseScreen().cursor(0, 0));
        //Чистый лист для параболы
        char[][] cord = new char[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) cord[row][col] = ' ';
        }

        int prevCol = -1;
        int prevRow = -1;

        // Рисую функцию
        for (int col = 0; col < width; col++) {
            double x = xMin + (xMax - xMin) * col / (width - 1);
            double y = evaluate(x);
            int row = yToRow(y);

            if (row >= 0 && row < height) {
                if (prevCol != -1 && prevRow != -1) {
                    // Соединяем предыдущую точку с текущей
                    drawLine(cord, prevCol, prevRow, col, row);
                } else {
                    // Первая точка
                    cord[row][col] = '*';
                }
            }

            prevCol = col;
            prevRow = row;
        }


        Integer xZeroCol = null;
        if (xMin <= 0 && xMax >= 0) {
            xZeroCol = (int) Math.round((0 - xMin) / (xMax - xMin) * (width - 1));
        }

        Integer yZeroRow = null;
        if (yMin <= 0 && yMax >= 0) {
            yZeroRow = yToRow(0);
        }

        // Рисуем оси
        if (xZeroCol != null) {
            for (int r = 0; r < height; r++)
                if (cord[r][xZeroCol] == ' ') cord[r][xZeroCol] = '|';
        }
        if (yZeroRow != null) {
            for (int c = 0; c < width; c++)
                if (cord[yZeroRow][c] == ' ') cord[yZeroRow][c] = '-';
        }
        if (xZeroCol != null && yZeroRow != null) cord[yZeroRow][xZeroCol] = '+';

        // Выводим все приколы на экран
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                char ch = cord[r][c];
                if (ch == '*') {
                    System.out.print(ansi().fgGreen().a('*').reset());
                } else if (ch == '|' || ch == '-' || ch == '+') {
                    System.out.print(ansi().fgBlue().a(ch).reset());
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    private static void drawLine(char[][] cord, int x0, int y0, int x1, int y1) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int error = dx - dy;

        int x = x0;
        int y = y0;

        while (true) {
            if (x >= 0 && x < cord[0].length && y >= 0 && y < cord.length) {
                cord[y][x] = '*';
            }
            if (x == x1 && y == y1) break;

            int e2 = 2 * error;
            if (e2 > -dy) {
                error -= dy;
                x += sx;
            }
            if (e2 < dx) {
                error += dx;
                y += sy;
            }
        }
    }


    private static double evaluate(double x) {
        return a * Math.pow(x - b, n) + d;
    }

    private static int yToRow(double y) {
        double t = (yMax - y) / (yMax - yMin);
        return (int) Math.round(t * (height - 1));
    }
}
