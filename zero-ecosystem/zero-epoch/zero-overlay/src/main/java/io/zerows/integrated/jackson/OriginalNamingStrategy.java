package io.zerows.integrated.jackson;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import io.zerows.platform.constant.VValue;

import java.util.Locale;

/**
 * #「Tp」Jackson Naming Strategy
 * <p>
 * This component is `plugin` to resolving java bean specification in jackson here.
 * <p>
 * zero framework support jooq engine as default, when the user want to do serialization for Jooq generated code, this
 * component could detect `getX` and `isX` to uniform identifying to replaced different java bean method here.
 * <p>
 * 1. Situation 1: The type of boolean has been generated to `isX` as findRunning bean method.
 * 2. Situation 2: The type of boolean has been kept in `getX` as findRunning bean method.
 * <p>
 * To uniform this kind of java bean findRunning method specification, here zero provide small fix of naming resolution.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class OriginalNamingStrategy extends PropertyNamingStrategy {

    public static PropertyNamingStrategy JOOQ_NAME = new OriginalNamingStrategy();

    @Override
    public String nameForGetterMethod(final MapperConfig<?> config,
                                      final AnnotatedMethod method, final String defaultName) {
        final String methodName = method.getName();
        String fieldName = "";
        if (methodName.startsWith("get")) {
            fieldName = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            fieldName = methodName.substring(2);
        }
        final String firstLetter = String.valueOf(fieldName.charAt(VValue.IDX));
        return firstLetter.toLowerCase(Locale.getDefault()) + fieldName.substring(1);
    }
}
