package io.zerows.epoch.bootplus.extension.uca.modello;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.Kv;
import io.zerows.epoch.constant.KName;
import io.zerows.support.Ut;
import io.zerows.specification.modeling.HRecord;
import io.zerows.specification.modeling.property.OComponent;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class OutCompress implements OComponent {
    @Override
    public Object after(final Kv<String, Object> kv, final HRecord record, final JsonObject combineData) {
        /*
         * {
         *     "name": "当前属性名称",
         *     "formatFail": "当前属性数据类型",
         *     "rule": "PREFIX,...",
         *     "result": "ONE"
         * }
         */
        final JsonObject config = IoHelper.configCompress(combineData);
        final JsonObject data = IoHelper.compressFn(config.getString(KName.RULE)).apply(record, config);
        if (Ut.isNil(data)) {
            /*
             * 无数据
             */
            return null;
        } else {
            return IoHelper.end(data, config);
        }
    }
}
