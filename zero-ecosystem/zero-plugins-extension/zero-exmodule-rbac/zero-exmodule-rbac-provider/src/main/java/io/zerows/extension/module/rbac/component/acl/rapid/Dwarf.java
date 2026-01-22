package io.zerows.extension.module.rbac.component.acl.rapid;

import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.rbac.common.em.RegionType;
import io.zerows.extension.module.rbac.component.acl.rapid.addon.DwarfAddOn;
import io.zerows.extension.module.rbac.exception._80215Exception500DwarfNull;
import io.zerows.sdk.security.Acl;
import io.zerows.support.Ut;

/*
 * Dwarf
 */
public interface Dwarf {

    static Dwarf create(final RegionType type) {
        if (RegionType.RECORD == type) {
            return Pool.CC_DWARF.pick(DwarfRecord::new, type);
        } else if (RegionType.PAGINATION == type) {
            return Pool.CC_DWARF.pick(DwarfPagination::new, type);
            //return FnZero.po?l(Pool.DWARF_POOL, type, PaginationDwarf::new);
        } else if (RegionType.ARRAY == type) {
            return Pool.CC_DWARF.pick(DwarfArray::new, type);
            //return FnZero.po?l(Pool.DWARF_POOL, type, ArrayDwarf::new);
        } else {
            /*
             * Exception for unsupported type of Dwarf
             */
            throw new _80215Exception500DwarfNull(type.name());
        }
    }

    static Dwarf create() {
        return create(DwarfAddOn.class);
    }

    static Dwarf create(final Class<?> clazz) {
        return Pool.CC_ADDON.pick(() -> Ut.instance(clazz), clazz.getName());
    }

    void minimize(JsonObject dataReference, JsonObject matrix, Acl acl);
}
