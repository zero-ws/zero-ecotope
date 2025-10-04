package io.zerows.cosmic.plugins.validation;

import io.r2mo.typed.exception.WebException;
import io.zerows.cortex.metadata.WebRule;

import java.util.List;
import java.util.Map;

public class RigorJArray implements Rigor {
    @Override
    public WebException verify(final Map<String, List<WebRule>> rulers,
                               final Object body) {
        final WebException error = null;
        if (!rulers.isEmpty()) {

        }
        return error;
    }
}
