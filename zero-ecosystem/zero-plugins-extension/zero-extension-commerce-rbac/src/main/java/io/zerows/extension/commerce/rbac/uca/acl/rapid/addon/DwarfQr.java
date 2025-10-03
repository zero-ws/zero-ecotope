package io.zerows.extension.commerce.rbac.uca.acl.rapid.addon;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.component.qr.syntax.Ir;
import io.zerows.support.Ut;
import io.zerows.sdk.security.Acl;
import io.zerows.extension.commerce.rbac.uca.acl.rapid.Dwarf;

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
     * 1. criteria: Here put the value into "qr" node.
     * 2. 「Not Need」projection: Because the projection could be calculated based on `/column/full` and `/column/my` in
     * frontend, in this kind of situation, it could be KO.
     * 3. 「Not Need」rows：The rows impaction is on response result, it could be KO.
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
        final JsonObject query = matrix.getJsonObject(Ir.KEY_CRITERIA);
        if (Ut.isNotNil(query)) {
            dataReference.put(KName.__.QR, query);
        }
    }
}
