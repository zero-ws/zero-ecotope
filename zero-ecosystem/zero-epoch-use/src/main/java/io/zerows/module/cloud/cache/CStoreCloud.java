package io.zerows.module.cloud.cache;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotation.Memory;
import io.zerows.module.cloud.atom.AeonConfig;
import io.zerows.module.metadata.cache.CStore;
import io.zerows.specification.development.ncloud.HAeon;
import io.zerows.specification.development.ncloud.HStarter;

/**
 * @author lang : 2023/5/2
 */
public interface CStoreCloud extends CStore {


    /*
     * 「应用配置集」
     * 用于存储 XApp + XSource 等应用程序配置集
     */
    @Memory(JsonObject.class)
    Cc<String, JsonObject> CC_META_APP = Cc.open();

    /*
     * CC_AEON:  Aeon系统启动后的核心配置缓存
     * CC_BOOT:  Aeon系统启动过后的所有使用类清单（组件接口集）
     */
    @Memory(AeonConfig.class)
    Cc<Integer, HAeon> CC_AEON = Cc.open();
    @Memory(HStarter.class)
    Cc<Integer, HStarter> CC_BOOT = Cc.open();
}
