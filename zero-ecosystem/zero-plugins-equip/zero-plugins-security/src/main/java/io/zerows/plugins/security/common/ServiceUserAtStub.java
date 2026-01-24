package io.zerows.plugins.security.common;

import io.r2mo.jaas.session.UserAt;
import lombok.extern.slf4j.Slf4j;

/**
 * ServiceUserAt 的默认实现（Stub）
 * 
 * 用于测试和演示，实际项目中应该实现自己的用户加载逻辑
 * 
 * @author lang : 2026-01-14
 */
@Slf4j
public class ServiceUserAtStub implements ServiceUserAt {

    @Override
    public UserAt loadLogged(final String identifier) {
        log.warn("[ PLUG ] 使用 Stub 实现加载用户：identifier = {}", identifier);
        log.warn("[ PLUG ] 请实现自己的 ServiceUserAt 并注册为 Bean");
        
        // TODO: 实际项目中应该从数据库加载用户
        // 这里返回 null，表示用户不存在
        return null;
    }

    @Override
    public UserAt authenticate(final String identifier, final String password) {
        log.warn("[ PLUG ] 使用 Stub 实现认证用户：identifier = {}", identifier);
        
        // 加载用户
        final UserAt userAt = this.loadLogged(identifier);
        if (userAt == null || !userAt.isOk()) {
            return null;
        }
        
        // TODO: 实际项目中应该验证密码
        // 这里简化实现，直接返回用户
        log.warn("[ PLUG ] Stub 实现跳过密码验证");
        return userAt;
    }
}
