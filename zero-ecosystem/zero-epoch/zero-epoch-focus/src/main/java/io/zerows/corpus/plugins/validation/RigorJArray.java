package io.zerows.corpus.plugins.validation;

import io.r2mo.typed.exception.WebException;
import io.zerows.epoch.corpus.model.Rule;

import java.util.List;
import java.util.Map;

public class RigorJArray implements Rigor {
    @Override
    public WebException verify(final Map<String, List<Rule>> rulers,
                               final Object body) {
        final WebException error = null;
        if (!rulers.isEmpty()) {

        }
        return error;
    }
}
