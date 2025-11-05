package io.zerows.extension.module.rbac.component.acl.rapid;

import io.r2mo.typed.cc.Cc;
import io.zerows.extension.module.rbac.common.em.RegionType;

interface Pool {
    Cc<RegionType, Dwarf> CC_DWARF = Cc.open();

    Cc<String, Dwarf> CC_ADDON = Cc.openThread();
}