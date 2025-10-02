package net.graysenko.com.MathProject.Core;

import lombok.Data;

@Data
public class Function {
    private String expr;
    private double a, b, n, d;
    private int clr;
    public Function(String expr, double a, double b, double n, double d, int clr) {
        this.expr = expr; this.a = a; this.b = b; this.n = n; this.d = d; this.clr = clr;
    }

    public double eval(double x){
        return a* Math.pow(x-b, n) + d;
    }
}
