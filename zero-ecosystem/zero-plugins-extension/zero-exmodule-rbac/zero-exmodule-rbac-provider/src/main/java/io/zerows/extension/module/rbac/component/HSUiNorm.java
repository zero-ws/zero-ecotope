package io.zerows.extension.module.rbac.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.security.KPermit;
import io.zerows.sdk.security.AbstractAdmit;
import io.zerows.support.Ut;

import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class HSUiNorm extends AbstractAdmit {

    @Override
    public Future<JsonObject> configure(final KPermit permit, final JsonObject requestJ) {
        return super.configure(permit, requestJ, KPermit::uiJ);
    }

    @Override
    @SuppressWarnings("all")
    public Future<JsonObject> compile(final KPermit permit, final JsonObject request) {
        /* 提取类型和参数 */
        final HAdmit compiler = HAdmit.create(permit, getClass());

        final JsonObject config = Ut.valueJObject(request, KName.Rbac.UI);
        final JsonObject qr = Ut.valueJObject(config, KName.Rbac.QR);

        /*
         * output 定义
         * {
         *     "group": "type"
         * }
         * 1）如果定义了 group 则按字段对 data 执行分组
         * 2）否则直接处理 data 节点的数据
         */
        final JsonObject output = Ut.valueJObject(config, KName.OUTPUT);
        return compiler.ingest(qr, config).compose(data -> compileAsync(output, data));
    }

    private Future<JsonObject> compileAsync(final JsonObject output, final JsonArray data) {
        final String group = Ut.valueString(output, KName.GROUP);
        final JsonObject response = new JsonObject();
        if (Ut.isNil(group)) {
            response.put(KName.DATA, data);
        } else {
            final ConcurrentMap<String, JsonArray> grouped = Ut.elementGroup(data, group);
            response.put(KName.DATA, Ut.toJObject(grouped));
        }
        return Future.succeededFuture(response);
    }
}
