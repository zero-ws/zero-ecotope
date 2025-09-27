package io.zerows.core.web.validation.uca.combine;

import io.zerows.core.exception.WebException;
import io.zerows.core.web.model.atom.Rule;

import java.util.List;
import java.util.Map;

public interface Rigor {

    static Rigor get(final Class<?> clazz) {
        return CACHE.RIGORS.get(clazz);
    }

    WebException verify(final Map<String, List<Rule>> rulers,
                        final Object value);
}
