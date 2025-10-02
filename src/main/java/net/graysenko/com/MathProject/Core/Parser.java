package net.graysenko.com.MathProject.Core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public Function parseFormula(String expr, int clr) {
        expr = expr.replaceAll("\\s+", "");
        Pattern p = Pattern.compile(
                "y=([+-]?\\d+(?:\\.\\d+)?)"                    // a
                        + "\\*?\\(x([+-]\\d+(?:\\.\\d+)?)?\\)" // (x ± b)
                        + "\\^([+-]?\\d+(?:\\.\\d+)?)"         // ^n
                        + "([+-]\\d+(?:\\.\\d+)?)?"            // d
        );
        Matcher m = p.matcher(expr);
        if (m.matches()) {
            double a = Double.parseDouble(m.group(1));
            double b = 0;
            if (m.group(2) != null) {
                b = -Double.parseDouble(m.group(2));
            }
            double n = Double.parseDouble(m.group(3));
            double d = 0;
            if (m.group(4) != null) {
                d = Double.parseDouble(m.group(4));
            }

            Function func = new Function(expr, a, b, n, d, clr);

            System.out.println("Parsed: y = " + a + "*(x - " + b + ")^" + n + " + " + d);
            return func;
        } else {
            System.out.println("Could not parse formula. Example: y = -1*(x-2)^2+3");
            return null;
        }
    }
}
