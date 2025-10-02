package net.graysenko.com.MathProject.Core;

import lombok.Data;

import java.util.List;

@Data
public class Plotter {

    private int width, height;
    private double xMin,xMax,yMin,yMax;

    public Plotter(int width, int height, double xMin, double xMax, double yMin, double yMax) {
        this.width = width;
        this.height = height;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    //Calculates function
    public char[][] plot(List<Function> functions){
        char[][] cord = new char[height][width];

        for(int r=0;r<height;r++){
            for(int c=0;c<width;c++){
                cord[r][c] = ' ';
            }
        }

        for (Function f: functions){
            int prevRow = -1, prevCol = -1;

            for (int col=0;col<width;col++) {
                double x = xMin + (xMax - xMin) * col / (width - 1); // X on the canvas detection
                double y = f.eval(x);

                int row = yToRow(y);
                if (row >= 0 && row < height) {
                    if (prevRow != -1 &&  prevCol != -1 ) {
                        drawLine(cord, prevCol, prevRow, col, row, f.getClr());
                    } else {
                        cord[row][col] = (char) ('A' + f.getClr());
                    }
                }
                prevRow = row;
                prevCol = col;
            }
        }

        drawAxes(cord);

        return cord;
    }

    //Calculates Y coordinate to the right console row
    int yToRow(double y) {
        return (int) Math.round((yMax - y) / (yMax - yMin) * (height - 1));
    }


    private void drawAxes(char[][] cord){
        Integer xZeroCol = null;
        if(xMin <= 0 && xMax >= 0){
            xZeroCol = (int) Math.round((0 - xMin) / (xMax - xMin) * (width - 1)); // Point where x=0
        }

        Integer yZeroRow = null;
        if(yMin <= 0 && yMax >= 0){
            yZeroRow = yToRow(0);
        }

        if (xZeroCol != null) {
            for(int r = 0;r<height;r++){
                if(cord[r][xZeroCol] == ' ') cord[r][xZeroCol] = '|'; // draw | all the way down on x(0)
            }
        }

        if(yZeroRow != null) {
            for (int c = 0; c < width; c++){
                if (cord[yZeroRow][c] == ' ') cord[yZeroRow][c] = '-';
            }
        }

        if(yZeroRow != null && xZeroCol != null) cord[yZeroRow][xZeroCol] = '+';

        // Деления по X каждое целое
        if (yZeroRow != null) {
            for (int x = (int) Math.ceil(xMin); x <= xMax; x++) {
                int col = (int) Math.round((x - xMin) / (xMax - xMin) * (width - 1));
                if (col >= 0 && col < width) {
                    cord[yZeroRow][col] = '+';
                }
            }
        }

        // Деления по Y каждое целое
        if (xZeroCol != null) {
            for (int y = (int) Math.ceil(yMin); y <= yMax; y++) {
                int row = yToRow(y);
                if (row >= 0 && row < height) {
                    cord[row][xZeroCol] = '+';
                }
            }
        }
    }

    //Bresenham, points connect, line draw
    private void drawLine(char[][] cord, int x0, int y0, int x1, int y1, int clr) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1: -1;
        int sy = y0 < y1 ? 1: -1;

        int error = dx - dy;
        int x = x0, y = y0;

        while (true){
            if (x >= 0 && x < cord[0].length && y >= 0 && y< cord.length) {
                cord[y][x] = (char) ('A' + clr);
            }
            if (x == x1 && y == y1) break;
            int e2 = 2 * error;
            if (e2 > -dy) { error -= dy; x += sx; }
            if (e2 < dx) { error += dx; y += sy; }
        }
    }
}
