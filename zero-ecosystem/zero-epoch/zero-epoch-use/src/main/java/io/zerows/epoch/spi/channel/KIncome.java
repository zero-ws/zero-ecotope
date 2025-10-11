package io.zerows.epoch.spi.channel;

import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogO;
import io.zerows.support.Ut;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

/*
 * Stack of fluent api here to get parameters
 */
public class KIncome implements Serializable {
    private static final LogO LOGGER = Ut.Log.service(KIncome.class);
    private final transient Vector<Object> queue = new Vector<>();
    private final transient List<String> names = new ArrayList<>();

    private <T> KIncome(final Class<T> key) {
        final KLexeme<T> lexeme = Pocket.get(key);
        if (Objects.nonNull(lexeme)) {
            this.names.addAll(lexeme.params());
        }
    }

    public static <T> KIncome in(final Class<T> key) {
        return new KIncome(key);
    }

    public <T> KIncome in(final T value) {
        // System.err.println(get);
        this.queue.add(value);
        return this;
    }

    public JsonObject arguments() {
        final JsonObject arguments = new JsonObject();
        /*
         * Length here
         */
        final int size = this.names.size();
        for (int idx = 0; idx < size; idx++) {
            /*
             * field name
             */
            final String field = this.names.get(idx);
            if (!this.queue.isEmpty()) {
                final Object value = this.queue.get(idx);
                LOGGER.debug("[ KIncome ] field = {0}, get = {1}", field, value);
                arguments.put(field, value);
            }
        }
        return arguments;
    }
}
