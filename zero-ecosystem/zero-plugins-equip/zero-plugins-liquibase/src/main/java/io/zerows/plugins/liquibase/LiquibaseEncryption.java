package io.zerows.plugins.liquibase;

import io.zerows.epoch.constant.KName;
import io.zerows.platform.ENV;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;
import java.util.Set;

/**
 * <pre>
 *     处理环境变量
 * </pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class LiquibaseEncryption extends Properties {

    public LiquibaseEncryption() {
        super.defaults = new Properties();
    }

    private static final Set<String> ENV_SET = Set.of(
        KName.USER, KName.USERNAME, KName.PASSWORD
    );

    @Override
    public synchronized Object put(final Object paramK, final Object paramV) {
        if (!ENV_SET.contains(paramK.toString())) {
            // 直接跳过
            return super.put(paramK, paramV);
        }
        final String wrapValue = paramV.toString();
        final String envValue = ENV.parseVariable(wrapValue);
        log.info("[ ZERO ] 处理的属性键值对：{} = {}", paramK, envValue);
        return super.put(paramK, envValue);
    }
}
