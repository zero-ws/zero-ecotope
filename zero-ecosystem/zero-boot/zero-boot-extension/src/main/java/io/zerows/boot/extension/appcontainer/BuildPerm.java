package io.zerows.boot.extension.appcontainer;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class BuildPerm {

    public static Future<Boolean> run(final Vertx vertx) {
        return Future.succeededFuture(true);
    }
}
