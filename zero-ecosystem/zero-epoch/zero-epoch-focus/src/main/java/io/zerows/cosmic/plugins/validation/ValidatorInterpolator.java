package io.zerows.cosmic.plugins.validation;

import io.zerows.component.log.LogO;
import io.zerows.support.Ut;
import jakarta.el.ELManager;
import jakarta.el.ExpressionFactory;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import java.util.Locale;
import java.util.Map;

@SuppressWarnings("all")
public class ValidatorInterpolator extends ValidatorMessager {
    private static final LogO LOGGER = Ut.Log.uca(ValidatorInterpolator.class);

    private final ExpressionFactory expressionFactory;

    public ValidatorInterpolator() {
        this(null);
    }

    public ValidatorInterpolator(final ResourceBundleLocator userResourceBundleLocator) {
        this(userResourceBundleLocator, buildExpressionFactory(), true);
    }

    private ValidatorInterpolator(final ResourceBundleLocator userResourceBundleLocator, final ExpressionFactory factory, boolean internal) {
        super(userResourceBundleLocator);
        this.expressionFactory = factory;
    }

    private static ExpressionFactory buildExpressionFactory() {
        if (canLoadExpressionFactory()) {
            LOGGER.debug("Loaded expression factory via original TCCL");
            return ELManager.getExpressionFactory();
        } else {
            final ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(ResourceBundleMessageInterpolator.class.getClassLoader());
                if (canLoadExpressionFactory()) {
                    LOGGER.debug("Loaded expression factory via HV classloader");
                    return ELManager.getExpressionFactory();
                }

                Thread.currentThread().setContextClassLoader(ELManager.class.getClassLoader());
                if (canLoadExpressionFactory()) {
                    LOGGER.debug("Loaded expression factory via EL classloader");
                    return ELManager.getExpressionFactory();
                }
            } catch (final Throwable var6) {
                LOGGER.fatal(var6);
                throw var6;
            } finally {
                Thread.currentThread().setContextClassLoader(originalContextClassLoader);
            }
            throw new RuntimeException("Expression Factory error: Could not initialize EL ExpressionFactory.");
        }
    }

    private static boolean canLoadExpressionFactory() {
        try {
            ExpressionFactory.newInstance();
            return true;
        } catch (final Throwable var1) {
            return false;
        }
    }

    @Override
    public String interpolate(final Context context, final Locale locale, final String term) {
        if (term.startsWith("${") && term.endsWith("}")) {
            final String key = term.substring(2, term.length() - 1);
            final String value = this.findMessage(key, locale);
            if (value != null) {
                return value;
            }
        }
        if (term.startsWith("{") && term.endsWith("}")) {
            final String key = term.substring(1, term.length() - 1);
            if (context != null && context.getConstraintDescriptor() != null) {
                final Map<String, Object> attributes = context.getConstraintDescriptor().getAttributes();
                if (attributes != null && attributes.containsKey(key)) {
                    final Object value = attributes.get(key);
                    return value == null ? "null" : value.toString();
                }
            }
        }
        return term;
    }
}

