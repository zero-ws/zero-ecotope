package io.zerows.extension.module.rbac.component;

import io.zerows.extension.module.rbac.common.em.PackType;
import io.zerows.platform.enums.EmSecure;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
interface __H1H {
    // ScIn = Supplier :: HAdmitCompiler
    ConcurrentMap<EmSecure.ScIn, Supplier<HAdmit>> ADMIN_COMPILER = new ConcurrentHashMap<>() {
        {
            this.put(EmSecure.ScIn.DAO, HAdmitUiDao::new);
            this.put(EmSecure.ScIn.WEB, HAdmitUiWeb::new);
        }
    };
    // HType = Supplier :: HEyelet
    // VType = Supplier :: HEyelet
    // QType = Supplier :: HEyelet
    ConcurrentMap<Enum, Supplier<HEyelet>> EYELET = new ConcurrentHashMap<>() {
        {
            put(PackType.HType.IN, HEyeletRow::new);
        }
    };
}
