package io.zerows.epoch.metadata.environment;

import io.r2mo.typed.cc.Cc;
import org.osgi.framework.Bundle;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 服务数据相关信息，结构包括
 * <pre><code>
 *     全局服务数据
 *     局部服务数据
 * </code></pre>
 *
 * @author lang : 2024-07-01
 */
public class ContextOfData {
    private static final Cc<Long, ContextOfData> CC_CONTEXT = Cc.open();
    private static final ConcurrentMap<String, Object> DATA_GLOBAL = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Object> data = new ConcurrentHashMap<>();

    private ContextOfData() {
    }

    public static ContextOfData of(final Bundle owner) {
        return CC_CONTEXT.pick(ContextOfData::new, owner.getBundleId());
    }

    public static void putGlobal(final String field, final Object value) {
        DATA_GLOBAL.put(field, value);
    }

    @SuppressWarnings("all")
    public static <T> T getGlobal(final String field) {
        return (T) DATA_GLOBAL.getOrDefault(field, null);
    }

    public ContextOfData put(final String field, final Object value) {
        this.data.put(field, value);
        return this;
    }

    @SuppressWarnings("all")
    public <T> T get(final String field) {
        return (T) data.getOrDefault(field, null);
    }
}
