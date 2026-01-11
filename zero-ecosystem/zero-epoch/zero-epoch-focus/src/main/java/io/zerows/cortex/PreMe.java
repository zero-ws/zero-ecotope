package io.zerows.cortex;

import io.zerows.epoch.annotations.Me;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.enums.modeling.EmValue;

import java.lang.reflect.Method;

public class PreMe implements Invoker.Pre {
    @Override
    public void execute(final Method method, final Envelop envelop) {
        if (method.isAnnotationPresent(Me.class)) {
            final Me annotation = method.getDeclaredAnnotation(Me.class);
            final EmValue.Bool active = annotation.active();
            final boolean app = annotation.app();
            envelop.onMe(active, app);
        }
    }
}
