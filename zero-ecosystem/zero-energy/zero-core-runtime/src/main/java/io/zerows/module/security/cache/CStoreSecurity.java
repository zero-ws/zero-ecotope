package io.zerows.module.security.cache;

import io.r2mo.typed.cc.Cc;
import io.zerows.ams.annotations.Memory;
import io.zerows.module.metadata.cache.CStore;
import io.zerows.module.security.atom.manage.KPermit;
import io.zerows.module.security.atom.manage.KSemi;

/**
 * @author lang : 2024-04-18
 */
public interface CStoreSecurity extends CStore {
    /*
     * 「环境级别处理」安全管理专用
     * - CC_PERMIT  :  KPermit      权限定义对象
     * - CC_SEMI    :  KSemi        权限执行双维对象（维度+数据）
     */
    @Memory(KPermit.class)
    Cc<String, KPermit> CC_PERMIT = Cc.open();
    @Memory(KSemi.class)
    Cc<String, KSemi> CC_SEMI = Cc.open();
}
