package net.graysenko.com.MathProject.ui;

import net.graysenko.com.MathProject.Core.Canvas;
import net.graysenko.com.MathProject.Core.Function;
import net.graysenko.com.MathProject.Core.Parser;
import net.graysenko.com.MathProject.Core.Plotter;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.fusesource.jansi.Ansi.ansi;

public class ConsoleUI {

    private int width = 80, height = 30;
    private double xMin = -10, xMax = 10, yMin = -10, yMax = 20;

    List<Function> formulas = new ArrayList<>();

    private final Parser parser = new Parser();
    private final Canvas canvas = new Canvas();
    private final Plotter plotter = new Plotter(width, height, xMin, xMax, yMin, yMax);

    public void start(){
       AnsiConsole.systemInstall();
       LineReader reader = LineReaderBuilder.builder().build();

       printHelp();

       while (true) {
           String line = reader.readLine(ansi().fgYellow().a("> ").reset().toString());
           if (line == null) continue;
           line = line.trim();
           if (line.equalsIgnoreCase("exit")) break;
           if (line.isEmpty()) continue;

           if (line.startsWith("plot")) {
               Function func = parser.parseFormula(line.substring(4).trim(), 0);
               if (func != null) {
                   formulas.clear();
                   formulas.add(func);
                   char[][] cord = plotter.plot(formulas);
                   canvas.render(cord);
               }
           } else if (line.startsWith("mplot")) {
               int clr = formulas.size() % 5;
               Function func = parser.parseFormula(line.substring(5).trim(), clr);
               if (func != null) {
                   formulas.add(func);
                   char[][] cord = plotter.plot(formulas);
                   canvas.render(cord);
                   drawList();
               }
           } else if (line.startsWith("xrange")) {
               String[] p = line.split("\\s+");
               if (p.length == 3) {
                   xMin = Double.parseDouble(p[1]);
                   xMax = Double.parseDouble(p[2]);
                   plotter.setXMin(Double.parseDouble(p[1]));
                   plotter.setXMax(Double.parseDouble(p[2]));
               }
               System.out.print(ansi().fgGreen().a("Changes applied!").reset().toString());
           } else if (line.startsWith("yrange")) {
               String[] p = line.split("\\s+");
               if (p.length == 3) {
                   yMin = Double.parseDouble(p[1]);
                   yMax = Double.parseDouble(p[2]);
                   plotter.setYMin(Double.parseDouble(p[1]));
                   plotter.setYMax(Double.parseDouble(p[2]));
               }
               System.out.print(ansi().fgGreen().a("Changes applied!").reset().toString());
           } else if (line.startsWith("size")) {
               String[] p = line.split("\\s+");
               if (p.length == 3) {
                   width = Integer.parseInt(p[1]);
                   height = Integer.parseInt(p[2]);
                   plotter.setWidth(Integer.parseInt(p[1]));
                   plotter.setHeight(Integer.parseInt(p[2]));
               }
               System.out.print(ansi().fgGreen().a("Changes applied!").reset().toString());
           } else if (line.equals("clear")) {
               System.out.print(ansi().eraseScreen().cursor(0, 0));
           } else if (line.equals("mclear")) {
               System.out.print(ansi().eraseScreen().cursor(0, 0));
               formulas.clear();
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
        System.out.println("  mplot y = a*(x - b)^n + d (width > 20 is required)");
        System.out.println("  mclear");
        System.out.println("  xrange <min> <max>");
        System.out.println("  yrange <min> <max>");
        System.out.println("  size <width> <height>");
        System.out.println("  clear");
        System.out.println("  exit");
    }

    public void drawList() {
        if (width <= 0 || height <= 0) return;
        if (formulas.isEmpty()) return;
        if (width <= 40) {
            System.out.print(ansi().cursor(1, 1)
                    .a("Multiplot is only available by width > 40").reset());
            return;
        }

        System.out.print(ansi().cursor(1, width - 20)
                .fgBrightDefault().a("# Active functions #").reset());

        for (int i = 0; i < formulas.size(); i++) {
            Function f = formulas.get(i);
            int row = 2 + i;

            Ansi color = getColor(f.getClr());
            System.out.print(ansi().cursor(row, width - 20));
            System.out.print(color.a(centerAlign(f.getExpr())).reset());
        }

        System.out.print(ansi().cursor(formulas.size() + 2, width - 20)
                .fgBrightDefault().a("####################").reset());
        System.out.print(ansi().cursor(height + 1, 1));
    }

    private String centerAlign(String str) {
        int totalWidth = 20;
        int len = str.length();
        if (len >= totalWidth) return str.substring(0, totalWidth);

        int left = (totalWidth - len) / 2;
        int right = totalWidth - len - left;
        return "#" + " ".repeat(Math.max(0, left)) + str + " ".repeat(Math.max(0, right -1)) + "#";
    }

    private Ansi getColor(int clr) {
        return switch (clr) {
            case 0 -> ansi().fgRed();
            case 1 -> ansi().fgGreen();
            case 2 -> ansi().fgCyan();
            case 3 -> ansi().fgYellow();
            case 4 -> ansi().fgMagenta();
            default -> ansi().fgDefault();
        };
    }
}
