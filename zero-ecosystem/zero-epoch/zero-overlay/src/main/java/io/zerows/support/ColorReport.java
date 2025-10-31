package io.zerows.support;

import java.util.Set;

/**
 * @author lang : 2025-10-31
 */
public class ColorReport {

    public static void main(final String[] args) {
        Set.of("33", "34", "35", "36", "37", "38", "39").forEach(item -> {
            System.out.println(item);
            for (int i = 1; i < 255; i++) {
                final String color = "\033[" + item + ";5;" + i + "m[ " + String.format("%3s", i) + " ]\033[0m";
                System.out.print(color + " ");
                if (i % 20 == 0) {
                    System.out.println();
                }
            }
            System.out.println();
        });
    }
}
