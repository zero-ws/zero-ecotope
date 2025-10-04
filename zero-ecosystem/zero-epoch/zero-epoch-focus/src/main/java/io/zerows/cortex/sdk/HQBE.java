package io.zerows.cortex.sdk;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.web.Envelop;

/**
 * QBE专用流程，解析 QBE 参数
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface HQBE {

    Future<Envelop> before(JsonObject qbeJ, Envelop envelop);
}
