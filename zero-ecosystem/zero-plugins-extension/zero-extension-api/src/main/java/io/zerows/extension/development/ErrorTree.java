package io.zerows.extension.development;

import io.r2mo.SourceError;

public class ErrorTree {

    public static void main(final String[] args) {
        SourceError.printTree("io.zerows", "io.r2mo");
    }
}
