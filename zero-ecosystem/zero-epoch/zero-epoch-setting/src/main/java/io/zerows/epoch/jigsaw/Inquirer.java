package io.zerows.epoch.jigsaw;

import java.util.Set;

/**
 * 扫描器，扫描器分两层
 * <pre><code>
 *     1. 类扫描，位于 Zero.Core.Runtime.Assembly 中
 *     2. 组件扫描，位于 Zero.Core.Web.Domain 中
 * </code></pre>
 */
public interface Inquirer<R> {

    R scan(Set<Class<?>> clazzes);
}
