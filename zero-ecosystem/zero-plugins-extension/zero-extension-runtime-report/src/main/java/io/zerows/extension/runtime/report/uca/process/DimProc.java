package io.zerows.extension.runtime.report.uca.process;

import io.r2mo.typed.cc.Cc;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.report.atom.RDimension;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpDimension;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.specification.development.compiled.HBundle;

import java.util.List;

/**
 * 维度处理器，用于设置分组维度，按照某种维度分组之后，可设置不同的处理流程
 * <pre><code>
 *     1. 先把明细分组（递归）
 *     2. 然后根据组信息，执行聚合
 * </code></pre>
 * 维度处理器
 * <pre><code>
 *     1. 维度数据源提取
 *     2. 维度类型处理成特征可识别量，维度名称必须和特征维持一致不重复
 *     3. 维度数据源和特征数据源合并
 * </code></pre>
 *
 * @author lang : 2024-10-21
 */
public interface DimProc {

    Cc<String, DimProc> CC_SKELETON = Cc.openThread();

    static DimProc of(final HBundle bundle) {
        return AbstractDimProc.of(bundle, DimProcImpl.class);
    }

    static DimProc of() {
        return of(null);
    }

    /**
     * 分组维度专用（外层调用）
     *
     * @param params    参数
     * @param dimension 维度定义
     *
     * @return 处理之后的维度信息
     */
    default Future<List<RDimension>> dimAsync(final JsonObject params, final List<KpDimension> dimension) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    default Future<RDimension> dimAsync(final JsonObject params, final JsonArray source, final KpDimension dimension) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }
}
