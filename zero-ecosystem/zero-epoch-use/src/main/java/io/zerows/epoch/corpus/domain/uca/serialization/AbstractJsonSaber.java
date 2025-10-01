package io.zerows.epoch.corpus.domain.uca.serialization;

import io.vertx.core.json.DecodeException;
import io.zerows.epoch.based.exception._60004Exception400ParamFromString;

import java.util.function.Function;

/**
 * InJson
 */
public abstract class AbstractJsonSaber extends AbstractSaber {

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        if (this.isValid(paramType)) {
            try {
                return this.getFun().apply(literal);
            } catch (final DecodeException ex) {
                // Do not do anything
                // getLogger().checked(ex);
                throw new _60004Exception400ParamFromString(paramType, literal);
            }
        }
        return null;
    }

    protected abstract boolean isValid(final Class<?> paramType);

    protected abstract <T> Function<String, T> getFun();
}
