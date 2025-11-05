package io.zerows.extension.runtime.crud.uca.op.view;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.extension.runtime.crud.bootstrap.IxPin;
import io.zerows.extension.runtime.crud.eon.em.QrType;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.uca.op.Agonic;
import io.zerows.extension.skeleton.spi.UiApeakMy;
import io.zerows.extension.skeleton.spi.ScSeeker;
import io.zerows.program.Ux;

/**
 * 「视图同步」
 * 此类实现用于个人视图的保存，同步视图专用，内置调用 {@link UiApeakMy} 接口来保存当前视图
 * 此处的视图保存包含两部分：
 * <pre><code>
 *     - projection：列定义
 *     - criteria：查询条件
 * </code></pre>
 * 上述两个属性都会追加到存储的内容中，最终同步个人视图缓存。
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class ViewSync implements Agonic {

    @Override
    public Future<JsonObject> runJAsync(final JsonObject input, final IxMod in) {
        final ADB jooq = IxPin.jooq(in);
        return Ux.channel(ScSeeker.class, JsonObject::new, seeker -> seeker.on(jooq).fetchImpact(input))
            /* view has findRunning, ignored, */
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
             * data_key 的计算流程
             */
            .compose(params -> Pre.qr(QrType.BY_VK).inJAsync(params, in))
            .compose(params -> Ux.channel(UiApeakMy.class, JsonObject::new,
                stub -> stub.on(jooq).saveMy(params, params.getJsonObject(KName.DATA))));
    }
}
