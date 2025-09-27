package io.zerows.extension.commerce.rbac.uca.acl.rapid;

import io.r2mo.typed.cc.Cc;
import io.zerows.extension.commerce.rbac.eon.em.RegionType;

interface Pool {
    Cc<RegionType, Dwarf> CC_DWARF = Cc.open();

    Cc<String, Dwarf> CC_ADDON = Cc.openThread();
}