package io.zerows.plugins.security.sms;

import io.vertx.core.Future;
import io.zerows.support.Fx;

import java.util.Set;
import java.util.stream.Collectors;

public interface SmsStub {

    default Future<Boolean> sendCaptcha(final Set<String> toSet) {
        return Fx.combineB(toSet.stream()
            .map(this::sendCaptcha)
            .collect(Collectors.toSet())
        );
    }

    Future<Boolean> sendCaptcha(final String to);
}
