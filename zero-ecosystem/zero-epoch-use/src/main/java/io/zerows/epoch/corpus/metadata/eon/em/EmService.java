package io.zerows.epoch.corpus.metadata.eon.em;

/**
 * @author lang : 2024-07-01
 */
public final class EmService {
    private EmService() {
    }

    public enum Context {
        PLUGIN,         // 普通插件上下文，zero-equip
        MODULE,         // 扩展插件上下文，zero-extension
        APP,            // 应用插件上下文，入口专用，提取 HSetting
    }
}
