package net.graysenko.com.MathProject;

import net.graysenko.com.MathProject.ui.ConsoleUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Использую Jansi для цветного вывода и JLine для интерактивного ввода команд.
 * Поддерживает ввод формулы вида: y = +-a*(x +- b)^+-n +- d
 * Команды:
 *   plot <формула>     — построить новую одиночную функцию
 *   mplot <формула>    — поддержка вывода нескольких парабол одновременно
 *   xrange <min> <max> — диапазон по X
 *   yrange <min> <max> — диапазон по Y
 *   size <width> <height> — изменить размеры канвы
 *   clear              — очистить экран
 *   mclear             — очистка экрана и списка функций
 *   exit               — выход
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

        ConsoleUI console = new ConsoleUI();
        console.start();
    }
}
