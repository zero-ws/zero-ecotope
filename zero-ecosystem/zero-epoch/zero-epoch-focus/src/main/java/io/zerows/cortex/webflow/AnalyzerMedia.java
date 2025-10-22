package io.zerows.cortex.webflow;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebEpsilon;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Ut;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

public class AnalyzerMedia implements Analyzer {

    private static final Cc<String, Income<List<WebEpsilon<Object>>>> CC_EPSILON = Cc.openThread();

    @Override
    public Object[] in(final RoutingContext context,
                       final WebEvent event)
        throws WebException {
        /* Consume mime type matching **/
        final MediaType requestMedia = this.getMedia(context);
        AnalyzerMeta.accept(event, requestMedia);

        /* Extract definition from method **/
        final Income<List<WebEpsilon<Object>>> income = CC_EPSILON.pick(IncomeEpsilon::new); // FnZero.po?lThread(POOL_EPSILON, EpsilonIncome::new);
        final List<WebEpsilon<Object>> epsilons = income.in(context, event);

        /* Extract findRunning list **/
        return epsilons.stream()
            .map(WebEpsilon::getValue).toArray();
    }

    @Override
    public Envelop out(final Envelop envelop,
                       final WebEvent event) throws WebException {
        // TODO: Replier
        return null;
    }

    private MediaType getMedia(final RoutingContext context) {
        final String header = context.request().getHeader(HttpHeaders.CONTENT_TYPE);
        if (Ut.isNil(header)) {
            return MediaType.WILDCARD_TYPE;
        } else {
            return MediaType.valueOf(header);
        }
    }

}
