package io.zerows.extension.runtime.report.eon.em;

import io.zerows.extension.runtime.report.uca.pull.DataSet;

/**
 * @author lang : 2024-10-29
 */
public final class EmReport {
    private EmReport() {
    }

    /**
     * 数据源类型，用于 {@link DataSet} 分流操作，构造不同实现类
     * <pre><code>
     *     {
     *         "sourceType": "???",
     *         "ds.table": "???",
     *         "ds.view": "???",
     *         "ds.extension": "???",
     *
     *         "ds.active": "???",
     *         "ds.standby": "???"
     *     }
     * </pre></code>
     *
     * @author lang : 2024-10-12
     */
    public enum SourceType {
        TABLE,
        VIEW,
        EXTENSION,
        JOIN_2
    }

    public enum UcaStatus {
        ACTIVE,
        WAITING,
        DISABLED,
        ERROR
    }

    public enum FeatureType {
        // DIMENSION 和 AGGR 同为维度属性
        // ---- 统计类
        DIMENSION,              // 主维度：来自维度处理
        AGGR,                   // 基于维度的计算：聚合处理
        // ---- 明细类
        DATA,                   // 明细部分：数据直接处理
        LAZY,                   // 懒加载
        // ---- 以下为特殊类型 ----
        GLOBAL,                 // 全局
        NONE;

        public static FeatureType[] valueOk() {
            return new FeatureType[]{DIMENSION, AGGR, DATA, LAZY};
        }
    }

    public enum InputSource {
        INPUT,
        FEATURE
    }
}
