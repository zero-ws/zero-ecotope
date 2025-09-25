package io.zerows.extension.commerce.documentation.eon.em;

/**
 * @author lang : 2023-09-25
 */
public final class EmRefer {
    private EmRefer() {

    }

    /**
     * Refer 对应的值，新版全部使用大写，可作
     * 参数对待
     *
     * @author lang : 2023-09-25
     */
    public enum Entity {
        DOC,        // 文档
        SEGMENT,    // 段落
        CLAUSE,     // 条款
        PAPER,      // 考卷、底稿
    }
}
