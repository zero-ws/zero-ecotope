package io.zerows.epoch.corpus.web.validation.uca.combine;

import io.r2mo.typed.exception.WebException;
import io.zerows.epoch.corpus.model.atom.Rule;

import java.util.List;
import java.util.Map;

public interface Rigor {

    static Rigor get(final Class<?> clazz) {
        return CACHE.RIGORS.get(clazz);
    }

    WebException verify(final Map<String, List<Rule>> rulers,
                        final Object value);
}
