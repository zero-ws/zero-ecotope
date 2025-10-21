package io.zerows.extension.skeleton.spi;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;

/*
 * Interface for authorization resource key processing
 * 1) Request Uri: /api/group/search
 * 2) Pattern Uri:  /api/:actor/search
 * 3) Resolution for request key:
 *
 */
public interface ScOrbit {

    String ARG0 = KName.URI;
    String ARG1 = KName.URI_REQUEST;

    /*
     * Calculation method here, stack should be
     * parameter stack
     */
    String analyze(JsonObject arguments);
}
