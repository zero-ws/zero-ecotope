package io.zerows.core.web.validation.uca.combine;

import io.r2mo.typed.exception.WebException;
import io.zerows.core.web.model.atom.Rule;

import java.util.List;
import java.util.Map;

public class JArrayRigor implements Rigor {
    @Override
    public WebException verify(final Map<String, List<Rule>> rulers,
                               final Object body) {
        final WebException error = null;
        if (!rulers.isEmpty()) {

        }
        return error;
    }
}
