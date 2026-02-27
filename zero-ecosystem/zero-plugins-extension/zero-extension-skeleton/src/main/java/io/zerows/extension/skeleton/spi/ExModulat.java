package io.zerows.extension.skeleton.spi;

import cn.hutool.core.util.StrUtil;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

/**
 * 模块化专用的核心配置接口，提取应用配置专用，此处配置主要为扩展配置，即存储在 B_BLOCK 和 B_BAG 中的核心配置，返回数据的配置结构如下
 * <pre><code>
 *     app-01 = data
 *     app-02 = data
 *     app-03 = data
 * </code></pre>
 * 此处的结构会直接挂载到 X_APP 的读取中，此处 extension 的响应数据结构如：
 * <pre><code>
 *     {
 *         "key": "id，应用程序ID",
 *         "mHotel": {
 *             "comment": "模块为 mHotel 的参数集"
 *         },
 *         "bags": [
 *             "子应用清单"
 *         ]
 *     }
 * </code></pre>
 * 上述结构中
 * <pre>
 *     1. mXxx 前缀表示模块对应的参数信息
 *        - 若 uiOpen 中包含了此属性，则会执行公共接口的过滤
 *        - 否则直接提取 mXxx 模块中的所有参数相关内容
 *     2. bags 表示当前应用的子应用清单，需要注意的一点
 *        BBag 中拥有 entry = true，只有 entry = true 的应用才表示拥有入口的应用，其他应用不具有入口特性，若切换入口则直接更改
 *        - entry = true, entryId = 入口菜单
 *     3. BBag 的 UI_CONFIG 负责更新/提取的配置界面，包括界面特殊的表单设置
 *        BBlock 中的 UI_CONFIG / UI_CONTENT 则负责参数的元数据、数据两层
 * </pre>
 * 注意：此接口只提取数据，不提取配置相关信息，核心四个方法的逻辑如下
 * <pre>
 *     1. app 的两种形态 -> {@link JsonObject} / {@link String} （appId）
 *     2. open 模式的启用 -> open = true / open = false
 *     3. 组合数据的场景分析
 *        - 3.1. appJson -> appId ( id / key ) -> open = true
 *          提取应用 App 相关数据，并且只开放 open 模式
 *        - 3.2. appJson -> appId ( id / key ) -> open = false
 *          提取应用 App 相关数据，并且提取全模式的属性信息
 *        - 3.3. 直接使用 appId 进行两种模式的提取
 * </pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface ExModulat {
    /**
     * 输入结构
     * <pre><code>
     *     {
     *         "key": "id"
     *     }
     * </code></pre>
     *
     * @param appJson 应用 X_APP 表结构
     * @return 返回响应结构数据
     */
    default Future<JsonObject> extension(final JsonObject appJson) {
        return this.extension(appJson, false);
    }


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
     * 此处调用了新函数 {@link Ut#vId(JsonObject)} 来提取应用的 id 字段，其中 id 字段有两种形态
     * <pre>
     *     1. 旧版的 id 属性是 `key`，优先级更低
     *     2. 新版的 id 属性是 `id`，优先级更高
     *     3. 先按 id 提取，若 id 不存在则按 key 提取，若两者都不存在则抛出异常
     * </pre>
     *
     * @param appJson 应用结构
     * @param open    是否开启 open 模式
     *                - open = true / 开放模式可加载公开接口相关配置
     *                - open = false / 全模式（必须要求 401 和 403 的认证授权）
     * @return 最终返回应用配置
     */
    default Future<JsonObject> extension(final JsonObject appJson, final boolean open) {
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
            /*
             * 这种逻辑中，要保证一点，就是模块中的配置键不可以相同，如
             * - mSetting
             * - mStore
             * - mHotel
             * 等，若模块中配置键相同或重复，则此处会直接被后加载的模块覆盖导致异常，此处要开发人员自己判断
             */
            original.mergeIn(appJson, true);
            return Ux.future(original);
        });
    }

    default Future<JsonObject> extension(final String appId) {
        return this.extension(appId, false);
    }

    Future<JsonObject> extension(String appId, boolean open);

    default void invalidate(String appId) {}
}
