package io.zerows.epoch.corpus.web.validation.uca.combine;

import io.r2mo.typed.exception.WebException;
import io.reactivex.rxjava3.core.Observable;
import io.zerows.epoch.corpus.model.Rule;
import io.zerows.epoch.corpus.web.validation.uca.rules.Ruler;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Verify special workflow
 */
public class FileRigor implements Rigor {

    @Override
    public WebException verify(final Map<String, List<Rule>> rulers,
                               final Object body) {
        WebException error = null;
        if (!rulers.isEmpty()) {
            // Merge rulers here.
            final Set<Rule> rules = new HashSet<>();
            Observable.fromIterable(rulers.keySet())
                .map(rulers::get)
                .flatMap(Observable::fromIterable)
                .subscribe(rules::add)
                .dispose();
            // Rules here.
            error = Ruler.verify(rules, "BODY", body);
        }
        return error;
    }
}
