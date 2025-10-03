package io.zerows.epoch.bootplus.extension.migration.modeling;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.program.Ux;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EmptyRevision implements Revision {
    @Override
    public Future<ConcurrentMap<String, JsonObject>> captureAsync(final ConcurrentMap<String, String> keyMap) {
        return Ux.future(new ConcurrentHashMap<>());
    }
}
