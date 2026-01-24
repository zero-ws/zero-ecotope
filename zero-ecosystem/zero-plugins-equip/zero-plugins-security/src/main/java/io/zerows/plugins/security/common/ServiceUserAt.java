package io.zerows.plugins.security.common;

import io.r2mo.jaas.session.UserAt;

/**
 * 用户加载服务接口
 * 
 * 参考 Spring Security 的 UserDetailsService
 * 用于加载用户信息和验证用户凭证
 * 
 * @author lang : 2026-01-14
 */
public interface ServiceUserAt {

    /**
     * 根据用户标识加载用户信息
     * 
     * @param identifier 用户标识（用户名、手机号、邮箱等）
     * @return 用户会话信息，如果用户不存在返回 null
     */
    UserAt loadLogged(String identifier);
    
    /**
     * 验证用户密码
     * 
     * @param identifier 用户标识
     * @param password 密码（明文）
     * @return 如果密码正确返回用户信息，否则返回 null
     */
    default UserAt authenticate(String identifier, String password) {
        final UserAt userAt = this.loadLogged(identifier);
        if (userAt == null || !userAt.isOk()) {
            return null;
        }
        // TODO: 密码验证逻辑应该在实现类中完成
        return userAt;
    }
}
