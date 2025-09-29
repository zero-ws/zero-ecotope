package io.zerows.extension.runtime.integration.agent.api;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Me;
import io.zerows.core.annotations.Queue;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.integration.agent.service.DirStub;
import io.zerows.extension.runtime.integration.eon.Addr;
import io.zerows.extension.runtime.skeleton.exception._81002Exception400FilenameInvalid;
import jakarta.inject.Inject;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Queue
public class DirActor {

    @Inject
    private transient DirStub stub;

    @Me
    @Address(Addr.Directory.ADD)
    public Future<JsonObject> create(final JsonObject data) {
        // isFileName Checking
        final String name = data.getString(KName.NAME);
        if (!Ut.isFileName(name)) {
            return FnVertx.failOut(_81002Exception400FilenameInvalid.class);
        }
        return this.stub.create(data);
    }

    @Me
    @Address(Addr.Directory.UPDATE)
    public Future<JsonObject> update(final String key, final JsonObject data) {
        // isFileName Checking
        final String name = data.getString(KName.NAME);
        if (!Ut.isFileName(name)) {
            return FnVertx.failOut(_81002Exception400FilenameInvalid.class);
        }
        return this.stub.update(key, data);
    }

    /*
     * Hard Delete for directory.
     * 1. Delete `I_DIRECTORY` records
     * 2. Remove folder from `manager`
     */
    @Address(Addr.Directory.DELETE)
    public Future<Boolean> remove(final String key) {
        return this.stub.remove(key);
    }
}
