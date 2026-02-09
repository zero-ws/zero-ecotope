package io.zerows.extension.module.modulat.component;

import cn.hutool.core.util.StrUtil;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.skeleton.spi.ExModulat;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * 扩展模块核心配置，用于加载模块化之后的配置信息
 * <pre>
 *
 * </pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ExModulatCommon implements ExModulat {
    /**
     * 📘[JSON] --> appJson 结构：
     * <pre><code>
     * {
     *     "key": "X_APP 数据表中的主键",
     *     "name": "X_APP 表中的 name 字段",
     *     "code": "应用编码",
     *     "title": "应用标题",
     *     "domain": "域名",
     *     "port": "应用端口",
     *     "context": "（前端）应用 Context",
     *     "urlLogin": "（前端）登录页 /login/index",
     *     "urlAdmin": "（前端）管理主页 /main/index",
     *     "endpoint": "（后端）应用 EndPoint /htl",
     *     "entry": "入口专用 BAG，对应 B_BAG 中的 code",
     *     "sigma": "",
     *     "language": "cn",
     *     "active": true,
     *     "createdBy": "auditor-active",
     *     "appId": "",
     *     "tenantId": ""
     * }
     * </code></pre>
     *
     * @param appJson 应用结构
     * @param open    是否开启 open 模式
     *                - open = true / 开放模式不屏蔽敏感数据
     *                - open = false / 关闭模式屏蔽敏感数据（必须要求认证）
     * @return 最终返回应用配置
     */
    @Override
    public Future<JsonObject> extension(final JsonObject appJson, final boolean open) {
        final String key = Ut.vId(appJson);
        if (StrUtil.isEmpty(key)) {
            /*
            启动流程中的执行异常 /
             java.lang.NullPointerException
                at java.base/java.util.Objects.requireNonNull(Objects.java:233)
                at io.zerows.extension.module.modulat.component.ExModulatCommon.extension(ExModulatCommon.java:70)
                at io.zerows.extension.module.modulat.component.ExModulatCommon.extension(ExModulatCommon.java:55)
                at io.zerows.extension.skeleton.spi.ExModulat.extension(ExModulat.java:41)
                at io.zerows.extension.module.modulat.boot.MDModulatActor.startAsync(MDModulatActor.java:38)
                at io.zerows.extension.skeleton.metadata.MDModuleActor.lambda$startAsync$1(MDModuleActor.java:192)
                at java.base/java.util.concurrent.ConcurrentHashMap.forEach(ConcurrentHashMap.java:1603)
                at io.zerows.extension.skeleton.metadata.MDModuleActor.startAsync(MDModuleActor.java:192)
             */
            return Ux.futureJ();
        }
        return this.extension(key, open).compose(moduleJ -> {
            final JsonObject original = moduleJ.copy();
            original.mergeIn(appJson, true);
            return Ux.future(original);
        });
    }

    /*
     * {
     *     "configKey1": {},
     *     "configKey2": {}
     * }
     */
    @Override
    public Future<JsonObject> extension(final String appId, final boolean open) {
        Objects.requireNonNull(appId);
        final JsonObject appJ = Ut.vId(appId);
        return Ark.ofConfigure().modularize(appId, open).compose(moduleJ -> {
            appJ.mergeIn((JsonObject) moduleJ, true);
            if (open) {
                // open = true 可启用“登录参数”
                return Ux.future(appJ);
            } else {
                // open = false 的时候才读取 bags 节点的数据，否则不读取
                return Ark.ofBag().modularize(appId, false).compose(bagJ -> {
                    final JsonArray bags = (JsonArray) bagJ;
                    appJ.put(KName.App.BAGS, bags);
                    return Ux.future(appJ);
                });
            }
        });
    }
}
