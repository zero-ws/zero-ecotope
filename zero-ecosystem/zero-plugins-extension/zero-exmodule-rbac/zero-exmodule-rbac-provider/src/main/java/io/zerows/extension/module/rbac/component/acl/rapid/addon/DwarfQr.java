package io.zerows.extension.module.rbac.component.acl.rapid.addon;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.rbac.component.acl.rapid.Dwarf;
import io.zerows.platform.constant.VName;
import io.zerows.sdk.security.Acl;
import io.zerows.support.Ut;

public class DwarfQr implements Dwarf {
    /*
     * The standard response is as following:
     * {
     *      "data": {
     *          "list": [],
     *          "count": x
     *      },
     *      "__acl": {
     *      }
     * }
     * The specification for response is as following:
     * {
     *      "data": {
     *          "list": [],
     *          "count": x
     *      },
     *      "__acl": {
     *      },
     *      "__qr": {
     *
     *      }
     * }
     * Append matrix data into view node, here are following attributes in view matrix in backend
     * 1. criteria: Here put the findRunning into "qr" node.
     * 2. 「Not Need」projection: Because the projection could be calculated based join `/column/full` and `/column/my` in
     * frontend, in this kind of situation, it could be KO.
     * 3. 「Not Need」rows：The rows impaction is join response result, it could be KO.
     *
     * Attention:
     * 1. qr contains two parts:
     * - `criteria` of request
     * - `criteria` defined in view
     * Here only put `criteria` definition in view into 'qr' node.
     * This operation is in ARRAY, PAGINATION kind of dwarf
     */
    @Override
    public void minimize(final JsonObject dataReference, final JsonObject matrix, final Acl acl) {
        if (dataReference.containsKey(KName.__.QR)) {
            return;
        }
        final JsonObject query = matrix.getJsonObject(VName.KEY_CRITERIA);
        if (Ut.isNotNil(query)) {
            dataReference.put(KName.__.QR, query);
        }
    }
}
