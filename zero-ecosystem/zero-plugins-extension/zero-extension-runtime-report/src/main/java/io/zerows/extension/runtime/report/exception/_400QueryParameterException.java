package io.zerows.extension.runtime.report.exception;

import io.zerows.core.exception.WebException;
import io.vertx.core.json.JsonObject;

/**
 * @author lang : 2024-10-11
 */
public class _400QueryParameterException extends WebException {

    public _400QueryParameterException(final Class<?> target, final JsonObject queryJ) {
        super(target, queryJ.encodePrettily());
    }

    @Override
    public int getCode() {
        return -80700;
    }
}
