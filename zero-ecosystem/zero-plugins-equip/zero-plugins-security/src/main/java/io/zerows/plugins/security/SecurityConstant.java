package io.zerows.plugins.security;

import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._401UnauthorizedException;

/**
 * 注：此处键值应该和配置中的键值做一定的规定
 * <pre>
 *     1. 常量中的内容是大写，yaml 文件中是全小写
 *     2. 所以 {@link SecurityManager} 在提取数据时应该转换成小写
 * </pre>
 *
 * @author lang : 2025-12-30
 */
public interface SecurityConstant {
    String K_PREFIX_SEC = "[ PLUG ] ( Security )";
    String KEY_SESSION = "vertx-web.session-one";
    WebException UNAUTHORIZED = new _401UnauthorizedException("权限认证失败，令牌非法或已过期！");
    // ------------------ 内置 WallExecutor 信息
    String WALL_BASIC = "BASIC";
    String WALL_JWT = "JWT";
    String WALL_LDAP = "LDAP";
}
