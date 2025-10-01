package io.zerows.epoch.corpus.model.zdk;

import io.vertx.core.http.HttpMethod;

/*
 * API definition here, there are some complex definitions
 * 1）Critical attributes: method, uri
 */
public interface Api extends Commercial {
    /*
     * Http Method
     */
    HttpMethod method();

    /*
     * Api Uri:
     * 1）Secure: `/api/*`
     * 2）Public: `/*`
     * */
    String path();
}
