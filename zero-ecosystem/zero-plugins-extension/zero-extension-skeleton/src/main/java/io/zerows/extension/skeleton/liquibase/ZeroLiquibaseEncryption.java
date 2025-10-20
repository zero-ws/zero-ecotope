package io.zerows.extension.skeleton.liquibase;

import io.r2mo.base.secure.EDCrypto;
import io.r2mo.spi.SPI;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.ENV;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class ZeroLiquibaseEncryption extends Properties {

    private static final EDCrypto CRYPTO = SPI.findOne(EDCrypto.class, EDCrypto.FOR_DATABASE);

    public ZeroLiquibaseEncryption() {
        super.defaults = new Properties();
    }

    @Override
    public synchronized Object put(final Object paramK, final Object paramV) {
        if (KName.PASSWORD.equals(paramK)) {
            final String wrapValue = paramV.toString();
            final String decryptPassword = this.decryptPassword(wrapValue);
            if (Ut.isNil(decryptPassword)) {
                log.warn("[ ZERO ] 解密密码出错：{}", wrapValue);
                return null;
            }
            log.warn("[ ZERO ] 解密数据库密码成功，解密后密码：{}", decryptPassword);
            return super.defaults.put(paramK, decryptPassword);
        } else {
            return super.defaults.put(paramK, paramV);
        }
    }

    private String decryptPassword(final String wrapValue) {
        // 定义匹配的正则表达式
        final String regex = "\\$\\{(.+)\\}";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(wrapValue);
        if (matcher.find()) {
            // 使用了环境变量
            final String envKey = matcher.group(1);
            final String password = ENV.of().get(envKey, (String) null);
            if (Ut.isNil(password)) {
                return null;
            }
            return CRYPTO.decrypt(password);
        } else {
            // 未使用环境变量
            return CRYPTO.decrypt(wrapValue);
        }
    }
}
