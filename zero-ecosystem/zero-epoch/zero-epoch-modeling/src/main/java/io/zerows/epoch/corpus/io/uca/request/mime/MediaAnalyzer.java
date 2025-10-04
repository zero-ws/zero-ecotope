package io.zerows.epoch.corpus.io.uca.request.mime;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.basicore.ActorEvent;
import io.zerows.epoch.corpus.io.uca.request.mime.parse.EpsilonIncome;
import io.zerows.epoch.corpus.io.uca.request.mime.parse.Income;
import io.zerows.epoch.corpus.model.Epsilon;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Ut;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

public class MediaAnalyzer implements Analyzer {

    private static final Cc<String, Income<List<Epsilon<Object>>>> CC_EPSILON = Cc.openThread();

    @Override
    public Object[] in(final RoutingContext context,
                       final ActorEvent event)
        throws WebException {
        /* Consume mime type matching **/
        final MediaType requestMedia = this.getMedia(context);
        MediaAtom.accept(event, requestMedia);

        /* Extract definition from method **/
        final Income<List<Epsilon<Object>>> income = CC_EPSILON.pick(EpsilonIncome::new); // FnZero.po?lThread(POOL_EPSILON, EpsilonIncome::new);
        final List<Epsilon<Object>> epsilons = income.in(context, event);

        /* Extract value list **/
        return epsilons.stream()
            .map(Epsilon::getValue).toArray();
    }

    @Override
    public Envelop out(final Envelop envelop,
                       final ActorEvent event) throws WebException {
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
