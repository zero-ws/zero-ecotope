package io.zerows.management;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.annotations.meta.Memory;
import io.zerows.specification.atomic.HCombiner;
import io.zerows.specification.atomic.HCommand;

/**
 * @author lang : 2024-04-17
 */
public interface OCacheStore {
    /*
     * 「验证规则集」
     * 原 ZeroCodex 部分代码
     */
    @Memory(JsonObject.class)
    Cc<String, JsonObject> CC_CODEX = Cc.open();
    /*
     * 「界面级别处理」
     */
    @SuppressWarnings("all")
    @Memory(HCombiner.class)
    Cc<String, HCombiner> CC_COMBINER = Cc.openThread();
    /*
     * 「线程级」
     * CCT_EVENT: Aeon中的所有Event集合
     */
    @SuppressWarnings("all")
    @Memory(HCommand.Async.class)
    Cc<String, HCommand.Async> CCT_EVENT = Cc.openThread();
}
