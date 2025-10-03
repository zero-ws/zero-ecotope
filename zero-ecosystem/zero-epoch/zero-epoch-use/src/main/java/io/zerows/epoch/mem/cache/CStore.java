package io.zerows.epoch.mem.cache;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.annotations.Memory;

/**
 * @author lang : 2024-04-17
 */
public interface CStore extends
    CStoreObject,           // 语言级对象
    CStoreComponent,        // 高阶接口对象
    CStoreTrack             // 跟踪专用对象：日志、监控等
{
    /*
     * 「验证规则集」
     * 原 ZeroCodex 部分代码
     */
    @Memory(JsonObject.class)
    Cc<String, JsonObject> CC_CODEX = Cc.open();
}
