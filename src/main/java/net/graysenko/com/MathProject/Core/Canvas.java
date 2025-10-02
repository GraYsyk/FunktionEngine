package net.graysenko.com.MathProject.Core;

import static org.fusesource.jansi.Ansi.ansi;

public class Canvas {

    public void render(char[][] cord){
        System.out.print(ansi().eraseScreen().cursor(0,0));

        for (char[] chars : cord) {
            for (char ch : chars) {
                printColored(ch);
            }
            System.out.println();
        }
    }


    private void printColored(char ch){
        switch (ch) {
            case 'A': System.out.print(ansi().fgRed().a('*').reset()); break; //RED
            case 'B': System.out.print(ansi().fgGreen().a('*').reset()); break; //GREEN
            case 'C': System.out.print(ansi().fgCyan().a('*').reset()); break; //CYAN
            case 'D': System.out.print(ansi().fgYellow().a('*').reset()); break; //YELLOW
            case 'E': System.out.print(ansi().fgMagenta().a('*').reset()); break; //MAGENTA
            case '|': case '-': case '+':
                System.out.print(ansi().fgBlue().a(ch).reset()); break;
            default:
                System.out.print(ch);
        }
    }
}
