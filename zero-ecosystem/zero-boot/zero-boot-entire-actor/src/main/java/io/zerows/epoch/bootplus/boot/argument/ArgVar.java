package io.zerows.epoch.bootplus.boot.argument;

import io.zerows.epoch.metadata.MMVariable;

/**
 * 专用参数变量，直接从 {@link MMVariable} 继承，而 {@link MMVariable} 中已经包含了当前变量的相关信息：
 * <pre><code>
 *     - name:              变量名称
 *     - type:              变量类型
 *     - alias:             变量别名
 *     - get:             变量值
 *     - valueDefault:      变量默认值（扩展内容）
 * </code></pre>
 *
 * @author lang : 2023-06-11
 */
public class ArgVar extends MMVariable {

    private Object valueDefault;

    private ArgVar(final String name) {
        super(name);
    }

    public static ArgVar of(final String name) {
        return new ArgVar(name);
    }

    public ArgVar valueDefault(final Object valueDefault) {
        this.valueDefault = valueDefault;
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public <T> T value() {
        final Object value = super.value();
        return null == value ? (T) this.valueDefault : (T) value;
    }
}
