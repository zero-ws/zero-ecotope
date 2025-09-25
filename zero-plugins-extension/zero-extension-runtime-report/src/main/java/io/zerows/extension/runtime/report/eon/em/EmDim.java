package io.zerows.extension.runtime.report.eon.em;

/**
 * @author lang : 2024-10-30
 */
public final class EmDim {
    private EmDim() {
    }

    public enum Type {
        TREE,
        LINE,
        EXTENSION;

        public enum Tree {
            LEAF,       // 叶节点
            ROOT,       // 根节点
            ALL,        // 所有节点
        }
    }

    public enum Aggregator {
        SUM,        // 求和
        AVG,        // 平均
        COUNT,      // 计数
        MAX,        // 最大
        MIN,        // 最小
    }
}
