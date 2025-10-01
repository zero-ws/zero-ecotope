package io.zerows.extension.runtime.skeleton.secure;

import io.zerows.common.program.KPair;
import io.zerows.core.constant.KName;
import io.zerows.epoch.spi.cloud.HED;
import io.zerows.core.util.Ut;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.zerows.extension.runtime.skeleton.refine.Ke.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ZeroLiquibaseEncryption extends Properties {
    private static final HED ZERO_HED = new HEDExtension();

    public ZeroLiquibaseEncryption() {
        super.defaults = new Properties();
    }

    @Override
    public synchronized Object put(final Object paramK, final Object paramV) {
        if (KName.PASSWORD.equals(paramK)) {
            final KPair pair = ZERO_HED.loadRSA();
            final String wrapValue = paramV.toString();
            final String decryptPassword = this.decryptPassword(wrapValue, pair);
            if (Ut.isNil(decryptPassword)) {
                LOG.Ke.warn(this.getClass(), "Decrypt Error = {0}", wrapValue);
                return null;
            }
            LOG.Ke.info(this.getClass(), "Decrypt Password = {0}, ", decryptPassword);
            return super.defaults.put(paramK, decryptPassword);
        } else {
            return super.defaults.put(paramK, paramV);
        }
    }

    private String decryptPassword(final String wrapValue, final KPair pair) {
        // 定义匹配的正则表达式
        final String regex = "\\$\\{(.+)\\}";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(wrapValue);
        if (matcher.find()) {
            // 使用了环境变量
            final String envKey = matcher.group(1);
            final String password = Ut.envWith(envKey, null);
            if (Ut.isNil(password)) {
                return null;
            }
            return Ut.decryptRSAV(password, pair.getPrivateKey());
        } else {
            // 未使用环境变量
            return Ut.decryptRSAV(wrapValue, pair.getPrivateKey());
        }
    }
}
