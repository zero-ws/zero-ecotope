package io.zerows.extension.commerce.rbac.uca.timer;

import io.zerows.extension.commerce.rbac.atom.ScToken;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;

/**
 * @author lang : 2024-09-14
 */
public final class ClockFactory {

    private ClockFactory() {
    }

    public static ScClock<ScToken> ofToken(final Class<?> target) {
        final HBundle bundle = HPI.findBundle(target);
        return ofToken(bundle);
    }

    public static ScClock<String> ofCode(final Class<?> target) {
        final HBundle bundle = HPI.findBundle(target);
        return ofCode(bundle);
    }

    public static ScClock<String> ofSms(final Class<?> target) {
        final HBundle bundle = HPI.findBundle(target);
        return ofSms(bundle);
    }

    public static ScClock<String> ofImage(final Class<?> target) {
        final HBundle bundle = HPI.findBundle(target);
        return ofImage(bundle);
    }

    @SuppressWarnings("unchecked")
    public static ScClock<ScToken> ofToken(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, ScClockToken.class);
        return (ScClock<ScToken>) ScClock.CC_SKELETON.pick(() -> new ScClockToken(bundle), cacheKey);
    }

    @SuppressWarnings("unchecked")
    public static ScClock<String> ofCode(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, ScClockCode.class);
        return (ScClock<String>) ScClock.CC_SKELETON.pick(() -> new ScClockCode(bundle), cacheKey);
    }

    @SuppressWarnings("unchecked")
    public static ScClock<String> ofSms(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, ScClockSMS.class);
        return (ScClock<String>) ScClock.CC_SKELETON.pick(() -> new ScClockSMS(bundle), cacheKey);
    }

    @SuppressWarnings("unchecked")
    public static ScClock<String> ofImage(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, ScClockImage.class);
        return (ScClock<String>) ScClock.CC_SKELETON.pick(() -> new ScClockImage(bundle), cacheKey);
    }
}
