package io.zerows.extension.runtime.crud.uca.op.view;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.cache.Rapid;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.extension.runtime.crud.bootstrap.IxPin;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.uca.op.Agonic;
import io.zerows.extension.skeleton.spi.UiApeakMy;
import io.zerows.extension.skeleton.spi.ScSeeker;
import io.zerows.program.Ux;

/**
 * 「我的列」
 * 此类实现用于读取我的列数据（存储于 zero-rbac中），它的核心点如下：
 * <pre><code>
 *     1. 参数格式如（主要查询 S_RESOURCE / S_ACTION）：
 *        {
 *            "view": [view, position],
 *            "uri": "HTTP对应的URI路径",
 *            "method": "HTTP方法",
 *            "sigma": "统一标识符",
 *            "resourceId": "Seeker 接口专用的资源ID"
 *        }
 *     2. 之后会提取资源和视图数据
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class ViewMy implements Agonic {

    @Override
    public Future<JsonArray> runJAAsync(final JsonObject input, final IxMod in) {
        final ADB jooq = IxPin.jooq(in);
        return this.fetchResources(input, jooq, in)
            /* view has get, ignored, */
            /*
             * url processing
             * {
             *      "requestUri": "xxx",
             *      "method": "method",
             * }
             * */
            .compose(params -> Pre.uri().inJAsync(params, in))
            /*
             * {
             *      "user": "xxx",
             *      "habitus": "xxx"
             * }
             */
            .compose(params -> Pre.user().inJAsync(params, in))
            /*
             * 关键流程
             */
            .compose(params -> this.fetchViews(params, jooq, in));
    }

    /**
     * 资源提取，根据输入数据提取相关资源信息
     * <pre><code>
     *      1. 缓存的 key 值为 {module}:{connect}:{HashCode (input)}
     *         - 基于 {@link IxMod} 的模型信息
     *         - 请求级：输入专用的哈希值
     *      2. 提取资源信息依赖 {@link ScSeeker} 接口，此接口负责提取资源信息
     * </code></pre>
     *
     * @param input 输入数据
     * @param jooq  Jooq接口，可指定绑定的数据源
     * @param in    IxMod接口，专用模型信息
     *
     * @return {@link Future}
     */
    private Future<JsonObject> fetchResources(final JsonObject input, final ADB jooq, final IxMod in) {
        final String key = in.cached() + ":" + input.hashCode();
        return Rapid.<String, JsonObject>object(KWeb.CACHE.RESOURCE, Agonic.EXPIRED).cached(key,
            () -> Ux.channel(ScSeeker.class, JsonObject::new, seeker -> seeker.on(jooq).fetchImpact(input)));
    }

    /**
     * 为了避免不同的模型进行计算，此处也许会引起性能问题，但对用户本身而言，提取个人视图缓存是一个必须步骤
     * 读取个人视图时访问了 {@link UiApeakMy} 接口
     *
     * @param params 参数信息（提取视图的参数信息）
     * @param jooq   Jooq接口，可指定绑定的数据源
     * @param in     IxMod接口，专用模型信息
     *
     * @return {@link Future}
     */
    private Future<JsonArray> fetchViews(final JsonObject params, final ADB jooq, final IxMod in) {
        /*
         * 旧代码：
             final String key = in.cacheKey() + ":" + params.hashCode();
             final User user = in.envelop().user();
             return Rapid.<JsonArray>user(user, CACHE.User.MY_VIEW).cached(key,
                    () -> Ux.channel(ApeakMy.class, JsonArray::new, stub -> stub.join(jooq).fetchMy(params)));
         */
        return Ux.channel(UiApeakMy.class, JsonArray::new, stub -> stub.on(jooq).fetchMy(params));

    }
}
