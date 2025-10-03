package io.zerows.enums;

/**
 * 不同角色的环境信息
 * <pre><code>
 *     1. Production: 部署专用生成环境
 *     2. Development: 开发专用环境
 *     3. Mockito: 集成测试专用环境
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public enum Environment {
    Production,
    Development,
    Mockito,
}
