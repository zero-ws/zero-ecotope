package io.zerows.specification.modeling.property;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.Kv;
import io.zerows.specification.modeling.HRecord;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface OComponent extends IoSource {

    Object after(Kv<String, Object> kv, HRecord record, JsonObject combineData);
}
