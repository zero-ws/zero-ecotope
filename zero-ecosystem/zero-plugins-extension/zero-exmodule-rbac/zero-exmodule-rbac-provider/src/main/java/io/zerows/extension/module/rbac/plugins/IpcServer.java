package io.zerows.extension.module.rbac.plugins;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Ipc;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.skeleton.common.KeIpc;

/*
 * Ipc Server for current
 */
public class IpcServer {
    /*
     * This is specific because of @Inject may of duplicated
     * implementation class in zero system.
     */

    @Ipc(KeIpc.Sc.IPC_TOKEN_VERIFY)
    public Future<JsonObject> verify(final Envelop envelop) {
        return null;
    }

    @Ipc(KeIpc.Sc.IPC_TOKEN_ACCESS)
    public Future<JsonObject> access(final Envelop envelop) {
        return null;
    }
}
