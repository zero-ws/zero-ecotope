package io.zerows.extension.commerce.rbac.uca.acl.rapid.addon;

import io.vertx.core.json.JsonObject;
import io.zerows.extension.commerce.rbac.uca.acl.rapid.Dwarf;
import io.zerows.epoch.corpus.security.zdk.authority.Acl;

public class DwarfAddOn implements Dwarf {
    @Override
    public void minimize(final JsonObject dataReference, final JsonObject matrix, final Acl acl) {
        // DwarfQr
        Dwarf.create(DwarfQr.class).minimize(dataReference, matrix, acl);
    }
}
