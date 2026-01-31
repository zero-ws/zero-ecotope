package io.zerows.cosmic.plugins.validation;

import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.cortex.metadata.WebRequest;
import io.zerows.cortex.metadata.WebRule;
import io.zerows.cosmic.plugins.validation.exception._60000Exception400Validation;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.web.WebApi;
import io.zerows.management.OCacheStore;
import io.zerows.support.Ut;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.extension.BodyParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ValidatorEntry {

    private static final ConcurrentMap<String, Map<String, List<WebRule>>>
        RULERS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, JsonObject> STORED = OCacheStore.CC_CODEX.get();

    private static final ValidatorInterpolator INTERPOLATOR = new ValidatorInterpolator();
    @SuppressWarnings("all")
    private static final Validator VALIDATOR = Validation.byDefaultProvider()
        .configure()
        .messageInterpolator(INTERPOLATOR)
        .buildValidatorFactory()
        .getValidator();

    /**
     * Validate the method parameters based on javax.validation: Hibernate Validator.
     *
     * @param proxy  The checked object.
     * @param method The checked method.
     * @param args   The checked method's parameters.
     * @param <T>    The object type: Generic types.
     */
    public <T> void verifyMethod(
        final T proxy,
        final Method method,
        final Object[] args) {
        // 1. Get method validator
        final ExecutableValidator validatorParam
            = VALIDATOR.forExecutables();
        // 2. Create new params that wait for validation
        final Set<ConstraintViolation<T>> constraints
            = validatorParam.validateParameters(proxy, method, args);
        // 3. Throw out exception
        if (!constraints.isEmpty()) {
            final ConstraintViolation<T> item = constraints.iterator().next();
            this.replyError(proxy, method, item);
        }
    }

    private <T> void replyError(final T proxy, final Method method,
                                final ConstraintViolation<T> item) {
        if (null != item) {
            String message = item.getMessage();
            if (message.startsWith("${") && message.endsWith("}")) {
                final String key = message.substring(2, message.length() - 1);
                final String resolved = INTERPOLATOR.findMessage(key, Locale.SIMPLIFIED_CHINESE);
                if (Ut.isNotNil(resolved)) {
                    message = resolved;
                }
            }
            throw new _60000Exception400Validation(proxy.getClass(), method, message);
        }
    }

    /**
     * Advanced ruler building for Body content validation based on yml configuration.
     *
     * @param wrapRequest The container to contains event, configuration, ruler.
     * @return The configured rulers.
     */
    public Map<String, List<WebRule>> buildRulers(
        final WebRequest wrapRequest) {
        final Map<String, List<WebRule>> rulers
            = new LinkedHashMap<>();
        final ConcurrentMap<String, Class<? extends Annotation>>
            annotions = wrapRequest.getAnnotations();
        Observable.fromIterable(annotions.keySet())
            .filter(KWeb.ARGS.MIME_DIRECT::equals)
            .map(annotions::get)
            // 1. Check whether contains @BodyParam
            .any(item -> BodyParam.class == item || BeanParam.class == item)
            // 2. Build rulers
            .map(item -> WebApi.nameOf(wrapRequest.getEvent()))
            .map(this::buildRulers)
            .subscribe(rulers::putAll).dispose();
        return rulers;
    }

    private Map<String, List<WebRule>> buildRulers(final String key) {
        if (RULERS.containsKey(key)) {
            return RULERS.get(key);
        }

        final JsonObject rule = STORED.get(key); // ZeroCodex.getCodex(key);
        final Map<String, List<WebRule>> ruler = new LinkedHashMap<>();
        if (null != rule) {
            Ut.itJObject(rule, (value, field) -> {
                // Checked valid rule config
                final List<WebRule> rulers = this.buildRulers(value);
                if (!rulers.isEmpty()) {
                    ruler.put(field, rulers);
                }
            });
            if (!ruler.isEmpty()) {
                RULERS.put(key, ruler);
            }
        }
        return ruler;
    }

    private List<WebRule> buildRulers(final Object config) {
        final List<WebRule> rulers = new ArrayList<>();
        if (config instanceof final JsonArray configData) {
            Ut.itJArray(configData, JsonObject.class, (item, index) -> {
                final WebRule ruler = WebRule.create(item);
                rulers.add(ruler);
            });
        }
        return rulers;
    }
}
