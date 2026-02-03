package io.zerows.extension.development;

import io.r2mo.SourceError;

import java.util.Set;

public class ErrorCode {

    public static void main(final String[] args) {
        Set.of(
            80311
        ).forEach(code -> SourceError.printExist(code,
            "io.zerows", "io.r2mo"));
    }
}
