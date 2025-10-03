package io.zerows.management;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.OLog;
import io.zerows.epoch.metadata.security.KPermit;
import io.zerows.epoch.metadata.security.KSemi;
import io.zerows.platform.annotations.Memory;
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
    Cc<String, OLog> CC_LOG = Cc.open();
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
    /*
     * 「Zero标准」
     * CC_SPI:    内置调用HService形成的通道接口（ServiceLoader规范）
     *            HService优先级
     *            - /META-INF/services/aeon/        Aeon Enabled
     *            - /META-INF/services/             Zero Extension Enabled
     */
    @Memory(Object.class)
    Cc<Class<?>, Object> CC_SPI = Cc.open();
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
