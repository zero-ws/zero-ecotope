package io.zerows.sdk.security;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.security.KPermit;
import io.zerows.specification.atomic.HCommand;
import io.zerows.specification.modeling.HAtom;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface HAdmit extends HCommand.Async<KPermit, JsonObject> {

    default HAdmit bind(final HAtom atom) {
        return this;
    }

    default HAdmit bind(final String sigma) {
        return this;
    }

    /*
     * 常用组件备注：
     * 包：io.vertx.mod.rbac.ruler
     * - 维度：
     * -- HSDimNorm：默认值，读取 items（静态模式|前端模式）构造维度专用
     * -- HSDimDao：动态模式，使用配置读取维度信息
     * - 数据：
     * -- HSUiNorm：默认值，内置调用 HAdminCompiler 执行数据组合
     * 数据提取取决于 Compiler 的种类和配置的UI方法，配置时依旧可配置该值
     */
    Future<JsonObject> configure(final KPermit permit, final JsonObject requestJ);
}
