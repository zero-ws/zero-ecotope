package io.zerows.extension.development;

import io.r2mo.SourceError;

import java.util.Set;

public class ErrorCode {

    public static void main(final String[] args) {
        Set.of(
            80246,
            80247,
            80248,
            80249,
            80250,
            80251,
            80252,
            80253,
            80254
        ).forEach(code -> SourceError.printExist(code,
            "io.zerows", "io.r2mo"));
    }
}
