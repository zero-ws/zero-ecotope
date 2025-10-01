package io.zerows.epoch.corpus.configuration.module;

import org.osgi.framework.Bundle;

import java.io.Serializable;
import java.net.URL;
import java.util.Objects;

/**
 * 从 MDConfiguration 中拆分出来的标识管理，通过标识管理兼容不同环境的标识问题
 * <pre><code>
 *     1. path：     当前目录路径，字符串格式
 *     2. url：      当前目录路径对应的 URL 地址
 *     3. value：    手动指定目录的基础信息
 *     4. owner：    当前目录所属 Bundle 信息（单机环境中不指定）
 * </code></pre>
 * 此处的 owner 在 OSGI 环境中十分重要，用来指代谁发起了配置读取，而在单机环境中，owner 为 null，还有两个特殊属性，path 用来指明相对路径
 * 核心目录，其环境不同生成的 URL 会不一样，不论哪种环境最终可以以 URL 作为 Stream 的开启流程读取到对应的配置文件。
 *
 * @author lang : 2024-05-09
 */
public class MDId implements Serializable {
    private final String value;
    private final String path;
    private final URL url;
    private final Bundle owner;

    // 非 OSGI 环境
    private MDId(final String value) {
        this.value = value;
        this.owner = null;
        // 目录计算
        this.path = "plugins/" + value;
        this.url = Thread.currentThread().getContextClassLoader().getResource(this.path);
    }

    // OSGI 环境
    private MDId(final Bundle owner) {
        this.value = owner.getSymbolicName();
        this.owner = owner;
        // 目录计算
        this.path = "plugins/" + this.value;
        this.url = owner.getResource(this.path);
    }

    public static MDId of(final String id) {
        Objects.requireNonNull(id);
        return new MDId(id);
    }

    public static MDId of(final Bundle owner) {
        Objects.requireNonNull(owner);
        return new MDId(owner);
    }

    public String value() {
        return this.value;
    }

    public String path() {
        return this.path;
    }

    public URL url() {
        return this.url;
    }

    public Bundle owner() {
        return this.owner;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final MDId mdId = (MDId) o;
        return Objects.equals(this.value, mdId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }
}
