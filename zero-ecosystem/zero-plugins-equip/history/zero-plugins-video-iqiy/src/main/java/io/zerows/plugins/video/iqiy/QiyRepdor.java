package io.zerows.plugins.video.iqiy;

import io.zerows.core.uca.log.Annal;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.fn.Fn;
import io.zerows.plugins.video.iqiy.exception._401QiyAuthorizedException;
import io.zerows.plugins.video.iqiy.exception._401QiyExecuteException;

class QiyRepdor {
    private static final String CODE = "code";
    private static final String DATA = "data";
    private static final String MSG = "msg";
    private static final Annal LOGGER = Annal.get(QiyRepdor.class);

    static Future<JsonObject> handle(final JsonObject response) {
        LOGGER.info(Info.FEIGN_RESPONSE, response);
        Fn.outWeb(!QiyCodes.SUCCESS.equals(response.getString(CODE)), LOGGER,
            _401QiyAuthorizedException.class, QiyRepdor.class,
            response.getString(CODE),
            response.getString(MSG));
        return Future.succeededFuture(response.getJsonObject(DATA));
    }

    static Future<JsonObject> complete(final JsonObject response) {
        LOGGER.info(Info.FEIGN_RESPONSE, response);
        Fn.outWeb(!QiyCodes.SUCCESS.equals(response.getString(CODE)), LOGGER,
            _401QiyExecuteException.class, QiyRepdor.class,
            response.getString(CODE),
            response.getString(MSG));
        return Future.succeededFuture(response.getJsonObject(DATA));
    }
}
