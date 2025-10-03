package io.zerows.specification.modeling.property;

import io.vertx.core.json.JsonObject;
import io.zerows.component.shared.program.Kv;
import io.zerows.specification.modeling.HRecord;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface IComponent extends IoSource {

    Object before(Kv<String, Object> kv, HRecord record, JsonObject combineData);
}
