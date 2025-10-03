package io.zerows.epoch.mem.cache;

import io.r2mo.typed.cc.Cc;
import io.zerows.annotations.monitor.Memory;
import io.zerows.epoch.corpus.security.manage.KPermit;
import io.zerows.epoch.corpus.security.manage.KSemi;

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
