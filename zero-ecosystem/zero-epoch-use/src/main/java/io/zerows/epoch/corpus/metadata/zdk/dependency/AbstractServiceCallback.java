package io.zerows.epoch.corpus.metadata.zdk.dependency;

import io.zerows.epoch.corpus.metadata.zdk.service.ServiceContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lang : 2024-05-02
 */
public abstract class AbstractServiceCallback {

    protected final ServiceContext context;

    private final List<OOnce> okRefs = new ArrayList<>();

    protected AbstractServiceCallback(final ServiceContext context,
                                      final OOnce... okRefs) {
        this.context = context;
        this.okRefs.addAll(Arrays.asList(okRefs));
    }

    public void start(final Object reference) {
        this.okRefs.forEach(ok -> ok.bind(reference));

        if (this.isReady()) {
            this.okRefs.stream()
                .filter(okFor -> okFor instanceof OOnce.LifeCycle<?>)
                .map(okFor -> (OOnce.LifeCycle<?>) okFor)
                .forEach(ok -> ok.start(this.context));
        }
    }

    public void stop(final Object reference) {
        this.okRefs.stream()
            .filter(okFor -> okFor instanceof OOnce.LifeCycle<?>)
            .map(okFor -> (OOnce.LifeCycle<?>) okFor)
            .forEach(ok -> ok.stop(this.context));
    }

    public boolean isReady() {
        return this.okRefs.stream().allMatch(OOnce::isReady);
    }
}
