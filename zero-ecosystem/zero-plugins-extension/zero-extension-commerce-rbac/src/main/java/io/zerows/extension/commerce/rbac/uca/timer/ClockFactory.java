package io.zerows.extension.commerce.rbac.uca.timer;

import io.zerows.support.Ut;
import io.zerows.extension.commerce.rbac.atom.ScToken;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author lang : 2024-09-14
 */
public final class ClockFactory {

    private ClockFactory() {
    }

    public static ScClock<ScToken> ofToken(final Class<?> target) {
        return ofToken(FrameworkUtil.getBundle(target));
    }

    public static ScClock<String> ofCode(final Class<?> target) {
        return ofCode(FrameworkUtil.getBundle(target));
    }

    public static ScClock<String> ofSms(final Class<?> target) {
        return ofSms(FrameworkUtil.getBundle(target));
    }

    public static ScClock<String> ofImage(final Class<?> target) {
        return ofImage(FrameworkUtil.getBundle(target));
    }

    @SuppressWarnings("unchecked")
    public static ScClock<ScToken> ofToken(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, ScClockToken.class);
        return (ScClock<ScToken>) ScClock.CC_SKELETON.pick(() -> new ScClockToken(bundle), cacheKey);
    }

    @SuppressWarnings("unchecked")
    public static ScClock<String> ofCode(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, ScClockCode.class);
        return (ScClock<String>) ScClock.CC_SKELETON.pick(() -> new ScClockCode(bundle), cacheKey);
    }

    @SuppressWarnings("unchecked")
    public static ScClock<String> ofSms(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, ScClockSMS.class);
        return (ScClock<String>) ScClock.CC_SKELETON.pick(() -> new ScClockSMS(bundle), cacheKey);
    }

    @SuppressWarnings("unchecked")
    public static ScClock<String> ofImage(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, ScClockImage.class);
        return (ScClock<String>) ScClock.CC_SKELETON.pick(() -> new ScClockImage(bundle), cacheKey);
    }
}
