package io.zerows.cortex;

import io.vertx.core.json.JsonObject;
import io.zerows.cortex.metadata.ParameterBuilder;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.constant.VValue;
import io.zerows.weaver.ZeroType;

import java.lang.reflect.Method;
import java.util.Objects;

class CallUtil {

    static Object[] parseArguments(final Method method, final Envelop envelop, final Object... extension) {
        /*
         * One type dynamic here
         */
        final Object reference = envelop.data();
        /*
         * Non Direct
         */
        final Object[] arguments = new Object[method.getParameterCount()];
        final JsonObject json = (JsonObject) reference;
        final Class<?>[] types = method.getParameterTypes();
        /*
         * Adjust argument index
         */
        int adjust = 0;
        for (int idx = 0; idx < types.length; idx++) {
            /*
             * Multi calling for Session type
             */
            final Class<?> type = types[idx];
            /*
             * Found typed here
             * Adjust idx  - 1 to move argument index to
             * left.
             * {
             *    "0": "key",
             *    "1": "type",
             * }
             * (String,<Tool>,String) -> (idx, current), (0, 0), (1, ?), (2, 1)
             *                                               adjust = 1
             *
             * (<Tool>, String, String) -> (idx, current), (0, ?), (1, 0), (2, 1)
             *                                          adjust = 1
             *
             * (String, String,<Tool>) -> (idx, current), (0, 0), (1, 1), (2, ?)
             *                                                          adjust = 1
             */
            // Old: TypedArgument.analyzeWorker
            final ParameterBuilder<Envelop> builder = ParameterBuilder.ofWorker();
            final Object analyzed = builder.build(envelop, type, extension);
            if (Objects.isNull(analyzed)) {
                final int current = idx - adjust;
                final Object value = json.getValue(String.valueOf(current));
                if (Objects.isNull(value)) {
                    /*
                     * Input is null when type is not match, if type is JsonObject
                     * The result should be json instead of `null`
                     */
                    if (JsonObject.class == type && VValue.IDX == idx) {
                        /*
                         * Here are often the method as
                         * method(JsonObject, ...) formatFail
                         */
                        arguments[idx] = json.copy();
                    } else {
                        arguments[idx] = null;
                    }
                } else {
                    /*
                     * Serialization
                     */
                    arguments[idx] = ZeroType.value(type, value.toString());
                }
            } else {
                /*
                 * EmType successfully
                 */
                arguments[idx] = analyzed;
                adjust += 1;
            }
        }
        return arguments;
    }
}
