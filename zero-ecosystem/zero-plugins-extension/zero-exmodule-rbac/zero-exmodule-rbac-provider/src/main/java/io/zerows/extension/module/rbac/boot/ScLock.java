package io.zerows.extension.module.rbac.boot;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.extension.module.rbac.exception._80221Exception401MaximumTimes;
import io.zerows.extension.module.rbac.metadata.ScConfig;
import io.zerows.plugins.cache.HMM;
import io.zerows.program.Ux;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author lang : 2024-07-11
 */
class ScLock {
    private static final ScConfig CONFIG = MDRBACManager.of().config();

    /*
     * Login limitation for times in system, If failed the counter of current username should
     * increase by 1, the max limitation handler time is ScConfig.getVerifyLimitation
     * - lockVerify
     * - lockOn
     * - lockOff
     */
    static Future<JsonObject> lockVerify(final String username, final Supplier<Future<JsonObject>> executor) {
        final Integer limitation = CONFIG.getVerifyLimitation();
        if (Objects.isNull(limitation)) {
            // Verify Limitation is null, skip limitation code
            return executor.get();
        } else {
            return HMM.<String, Integer>of(ScConstant.POOL_LIMITATION).find(username).compose(counter -> {
                if (Objects.isNull(counter)) {
                    // Passed
                    return executor.get();
                } else {
                    // Compared
                    if (counter < limitation) {
                        // Passed
                        return executor.get();
                    } else {
                        // Failure
                        final Integer verifyDuration = CONFIG.getVerifyDuration();
                        return FnVertx.failOut(_80221Exception401MaximumTimes.class, limitation, verifyDuration);
                    }
                }
            });
        }
    }

    static Future<Integer> lockOn(final String username) {
        final Integer limitation = CONFIG.getVerifyLimitation();
        if (Objects.isNull(limitation)) {
            // Verify Limitation is null, skip limitation code
            return Ux.future();
        } else {
            final Integer verifyDuration = CONFIG.getVerifyDuration();
            final HMM<String, Integer> rapid = HMM.of(ScConstant.POOL_LIMITATION);
            return rapid.find(username).compose(counter -> {
                if (Objects.isNull(counter)) {
                    // First, the limitation counter is 1.
                    return rapid.put(username, 1, verifyDuration);
                } else {
                    return rapid.put(username, counter + 1, verifyDuration);
                }
            });
        }
    }

    static <V> Future<V> lockOff(final String username) {
        final Integer limitation = CONFIG.getVerifyLimitation();
        if (Objects.isNull(limitation)) {
            // Verify Limitation is null, skip limitation code
            return Ux.future();
        } else {
            return HMM.<String, V>of(ScConstant.POOL_LIMITATION).remove(username);
        }
    }
}
