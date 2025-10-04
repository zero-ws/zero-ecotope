package io.zerows.extension.runtime.report.uca.pull;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.OLog;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpFeature;
import io.zerows.platform.metadata.Kv;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

/**
 * 专用于解析 reportConfig 字段中的 input 节点，用于构造输入详细信息，输入方向来源
 * <pre><code>
 *     INPUT - 前端输入
 *     FEATURE - 全局特征
 * </code></pre>
 *
 * @author lang : 2024-11-04
 */
public interface DataInput {

    Cc<String, DataInput> CC_SKELETON = Cc.openThread();

    static DataInput of(final HBundle owner) {
        return DataInputImpl.of(owner, DataInputImpl.class);
    }

    static DataInput of() {
        return of(null);
    }

    default Future<Kv<String, Object>> prepare(final JsonObject params, final JsonObject configureJ) {
        return this.prepare(params, configureJ, null);
    }

    Future<Kv<String, Object>> prepare(JsonObject params, JsonObject configureJ, KpFeature feature);

    default OLog logger() {
        return Ut.Log.data(this.getClass());
    }
}
