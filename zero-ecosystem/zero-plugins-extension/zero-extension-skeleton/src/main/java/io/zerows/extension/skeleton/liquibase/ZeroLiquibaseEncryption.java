package io.zerows.extension.skeleton.liquibase;

import io.r2mo.base.secure.EDCrypto;
import io.r2mo.jce.component.secure.CryptoDatabase;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.ENV;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class ZeroLiquibaseEncryption extends Properties {

    public ZeroLiquibaseEncryption() {
        super.defaults = new Properties();
    }

    private static final Set<String> ENV_SET = Set.of(
        KName.USERNAME, KName.PASSWORD
    );

    @Override
    public synchronized Object put(final Object paramK, final Object paramV) {
        if (!ENV_SET.contains(paramK.toString())) {
            // 直接跳过
            return super.put(paramK, paramV);
        }
        final String wrapValue = paramV.toString();
        final String envValue = ENV.parseVariable(wrapValue);
        final String finalValue;
        if (KName.PASSWORD.equals(paramK)) {
            finalValue = this.decryptPassword(envValue);
        } else {
            finalValue = wrapValue;
        }
        log.info("[ ZERO ] 处理的属性键值对：{} = {}", paramK, finalValue);
        return super.put(paramK, finalValue);
    }

    private String decryptPassword(final String wrapValue) {
        if (128 > wrapValue.length()) {
            // 明文密码，直接返回
            return wrapValue;
        }
        try {
            final EDCrypto crypto = new CryptoDatabase();
            return crypto.decrypt(wrapValue);
        } catch (final Throwable ex) {
            return wrapValue;
        }
    }
}
