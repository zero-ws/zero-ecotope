package io.zerows.cosmic.plugins.validation;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

@SuppressWarnings("all")
@Slf4j
public class ValidatorBundleLocator implements ResourceBundleLocator {
    private static final boolean RESOURCE_BUNDLE_CONTROL_INSTANTIABLE = determineAvailabilityOfResourceBundleControl();
    private final String bundleName;
    private final ClassLoader classLoader;
    private final boolean aggregate;

    ValidatorBundleLocator(final String bundleName) {
        this(bundleName, (ClassLoader) null);
    }

    private ValidatorBundleLocator(final String bundleName, final ClassLoader classLoader) {
        this(bundleName, classLoader, false);
    }

    ValidatorBundleLocator(final String bundleName, final ClassLoader classLoader, final boolean aggregate) {
        // Contracts.assertNotNull(bundleName, "bundleName");
        if (bundleName == null) {
            throw new IllegalArgumentException("The bundle name cannot be null.");
        }
        this.bundleName = bundleName;
        this.classLoader = classLoader;
        this.aggregate = aggregate && RESOURCE_BUNDLE_CONTROL_INSTANTIABLE;
    }

    private static <T> T run(final PrivilegedAction<T> action) {
        return action.run();
    }

    private static boolean determineAvailabilityOfResourceBundleControl() {
        try {
            final ResourceBundle.Control dummyControl = ValidatorBundleLocator.AggregateResourceBundle.CONTROL;
            if (dummyControl == null) {
                return false;
            } else {
                final Method getModule = Class.class.getMethod("getModule");
                if (getModule == null) {
                    return true;
                } else {
                    final Object module = getModule.invoke(ValidatorBundleLocator.class);
                    final Method isNamedMethod = module.getClass().getMethod("isNamed");
                    final boolean isNamed = (Boolean) isNamedMethod.invoke(module);
                    return !isNamed;
                }
            }
        } catch (final Throwable var5) {
            log.info("[ ZERO ] Validator Warning: Unable to use ResourceBundle aggregation");
            return false;
        }
    }

    @Override
    public ResourceBundle getResourceBundle(final Locale locale) {
        ResourceBundle rb = null;
        if (this.classLoader != null) {
            rb = this.loadBundle(this.classLoader, locale, this.bundleName + " not found by user-provided classloader");
        }

        ClassLoader classLoader;
        if (rb == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                rb = this.loadBundle(classLoader, locale, this.bundleName + " not found by thread configure classloader");
            }
        }

        if (rb == null) {
            classLoader = ValidatorBundleLocator.class.getClassLoader();
            rb = this.loadBundle(classLoader, locale, this.bundleName + " not found by validator classloader");
        }

        if (rb != null) {
            log.info("[ ZERO ] Found {}", this.bundleName);
        } else {
            log.info("[ ZERO ] Not Found {}", this.bundleName);
        }

        return rb;
    }

    private ResourceBundle loadBundle(final ClassLoader classLoader, final Locale locale, final String message) {
        ResourceBundle rb = null;

        try {
            if (this.aggregate) {
                rb = ResourceBundle.getBundle(this.bundleName, locale, classLoader, ValidatorBundleLocator.AggregateResourceBundle.CONTROL);
            } else {
                rb = ResourceBundle.getBundle(this.bundleName, locale, classLoader);
            }
        } catch (final MissingResourceException var6) {
            log.debug(var6.getMessage());
        }
        return rb;
    }

    private static class AggregateResourceBundleControl extends ResourceBundle.Control {
        private AggregateResourceBundleControl() {
        }

        @Override
        public ResourceBundle newBundle(final String baseName, final Locale locale, final String format, final ClassLoader loader, final boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            if (!"java.properties".equals(format)) {
                return super.newBundle(baseName, locale, format, loader, reload);
            } else {
                final String resourceName = this.toBundleName(baseName, locale) + ".properties";
                final Properties properties = this.load(resourceName, loader);
                return properties.size() == 0 ? null : new ValidatorBundleLocator.AggregateResourceBundle(properties);
            }
        }

        private Properties load(final String resourceName, final ClassLoader loader) throws IOException {
            final Properties aggregatedProperties = new Properties();
            final Enumeration<URL> urls = loader.getResources(resourceName);

            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                final Properties properties = new Properties();
                properties.load(url.openStream());
                aggregatedProperties.putAll(properties);
            }
            return aggregatedProperties;
        }
    }

    private static class AggregateResourceBundle extends ResourceBundle {
        protected static final Control CONTROL = new ValidatorBundleLocator.AggregateResourceBundleControl();
        private final Properties properties;

        protected AggregateResourceBundle(final Properties properties) {
            this.properties = properties;
        }

        @Override
        protected Object handleGetObject(final String key) {
            return this.properties.get(key);
        }

        @Override
        public Enumeration<String> getKeys() {
            final Set<String> keySet = new HashSet<>();
            keySet.addAll(this.properties.stringPropertyNames());
            if (this.parent != null) {
                keySet.addAll(Collections.list(this.parent.getKeys()));
            }

            return Collections.enumeration(keySet);
        }
    }
}
