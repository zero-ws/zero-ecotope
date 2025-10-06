package io.zerows.support.base;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.annotations.HighOrder;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.modeling.HAtom;
import io.zerows.specification.modeling.HRecord;

/**
 * @author lang : 2023/4/28
 */
class _Modeler extends _It {
    protected _Modeler() {
    }

    /**
     * 建模专用转换，将记录转换成JsonObject
     *
     * @param records 记录
     *
     * @return JsonObject
     */
    @HighOrder(_App.class)
    public static JsonArray toJArray(final HRecord[] records) {
        return _App.toJArray(records);
    }

    /**
     * 返回模型专用的缓存键，该缓存键的构造基于两个核心维度：
     * <pre><code>
     *     1. 模型标识符 identifier
     *     2. 配置项，可变的 JsonObject 详细信息
     * </code></pre>
     *
     * @param atom    模型原子
     * @param options 配置项
     *
     * @return 缓存键
     */
    @HighOrder(_App.class)
    public static String keyAtom(final HAtom atom, final JsonObject options) {
        return _App.keyAtom(atom, options);
    }

    /**
     * 构造应用专用的缓存键，
     *
     * @param ark 应用配置容器 {@link HArk}
     *
     * @return 缓存键
     */
    @HighOrder(_App.class)
    public static String keyApp(final HArk ark) {
        return _App.keyApp(ark);
    }

    /**
     * 返回租户专用的ID值，检索优先级
     * <pre><code>
     *     1. Z_TENANT 环境变量
     *     2. 传入的 id
     *     3. 两个都为 null 则 DEFAULT
     * </code></pre>
     *
     * @param id 租户ID
     *
     * @return 租户ID
     */
    @HighOrder(_App.class)
    public static String keyOwner(final String id) {
        return _App.keyOwner(id);
    }

    /**
     * 单租户环境下返回以 id / appKey 为核心的查询条件，条件格式：
     * <pre><code>
     *     {
     *         "language": "xxx",
     *         "sigma": "xxx",
     *         "id": "xxx",
     *         "appKey": "xxx"
     *     }
     * </code></pre>
     *
     * @param ark 应用配置容器 {@link HArk}
     *
     * @return 缓存键
     */
    @HighOrder(_App.class)
    public static JsonObject qrApp(final HArk ark) {
        return _App.qrApp(ark, null);
    }

    /**
     * 多租户环境下返回以 id / appKey / tenant 为核心的查询条件，条件格式：
     * <pre><code>
     *     {
     *         "language": "xxx",
     *         "sigma": "xxx",
     *         "id": "xxx",
     *         "appKey": "xxx",
     *         "tenantId": "xxx"
     *     }
     * </code></pre>
     *
     * @param ark  应用配置容器 {@link HArk}
     * @param mode 模式
     *
     * @return 缓存键
     */
    @HighOrder(_App.class)
    public static JsonObject qrApp(final HArk ark, final EmApp.Mode mode) {
        return _App.qrApp(ark, mode);
    }


    /**
     * 单租户环境下返回以 namespace / name  为核心的查询条件，条件格式：
     * <pre><code>
     *     {
     *         "language": "xxx",
     *         "sigma": "xxx",
     *         "name": "xxx",
     *         "namespace": "xxx",
     *     }
     * </code></pre>
     *
     * @param ark 应用配置容器 {@link HArk}
     *
     * @return 缓存键
     */
    @HighOrder(_App.class)
    public static JsonObject qrService(final HArk ark) {
        return _App.qrService(ark, null);
    }

    /**
     * 单租户环境下返回以 namespace / name / tenantId 为核心的查询条件，条件格式：
     * <pre><code>
     *     {
     *         "language": "xxx",
     *         "sigma": "xxx",
     *         "name": "xxx",
     *         "namespace": "xxx",
     *         "tenantId": "xxx"
     *     }
     * </code></pre>
     *
     * @param ark  应用配置容器 {@link HArk}
     * @param mode 模式
     *
     * @return 缓存键
     */
    @HighOrder(_App.class)
    public static JsonObject qrService(final HArk ark, final EmApp.Mode mode) {
        return _App.qrService(ark, mode);
    }
}
