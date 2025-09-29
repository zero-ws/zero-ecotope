package io.zerows.core.web.io.uca.request.argument;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.fn.FnZero;
import io.zerows.core.util.Ut;
import io.zerows.core.web.io.exception._415PointDefineException;
import io.zerows.module.domain.atom.commune.Vis;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class PointFiller implements Filler {
    @Override
    public Object apply(final String name, final Class<?> paramType, final RoutingContext datum) {
        /*
         * First check the `paramType`, it must be following
         * 1. JsonArray
         * 2. List<Tool>
         */
        final boolean valid = Objects.nonNull(paramType)        // 1) Type is not null
            && (paramType.isAssignableFrom(List.class)          // 2) Type is implements List interface
            || JsonArray.class == paramType                     // 3) Type is JsonArray data structure
            || Vis.class == paramType                     // 4) View structure defined
        );
        FnZero.out(!valid, _415PointDefineException.class, this.getClass(), paramType);

        final String literal = datum.request().getParam(name);
        if (Objects.isNull(literal)) {
            // No input for this parameters
            return null;
        } else {
            final String normalized = Ut.aiJArray(literal);
            // Convert to correct type
            return this.resolve(paramType, normalized);
        }
    }

    private Object resolve(final Class<?> paramType, final String input) {
        final JsonArray value = Ut.toJArray(input);
        final Object reference;
        if (JsonArray.class == paramType) {
            reference = value;
        } else if (paramType.isAssignableFrom(List.class)) {
            reference = value.getList();
        } else {
            reference = Vis.create(value);
        }
        return reference;
    }
}
