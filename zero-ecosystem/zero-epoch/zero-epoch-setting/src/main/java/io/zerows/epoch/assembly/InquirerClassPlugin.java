package io.zerows.epoch.assembly;

import io.reactivex.rxjava3.core.Observable;
import io.zerows.epoch.annotations.Infusion;
import io.zerows.sdk.environment.Inquirer;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class InquirerClassPlugin implements Inquirer<Set<Class<?>>> {

    public static final String PLUGIN = "Plugins {} has been scanned with @Infusion. ";

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> allClasses) {
        final Set<Class<?>> plugins = new HashSet<>();
        // Filter Client
        Observable.fromIterable(allClasses)
            .filter(this::isPlugin)
            .subscribe(plugins::add)
            .dispose();
        // Ensure Tp Client
        if (!plugins.isEmpty()) {
            this.logger().info(PLUGIN, plugins.size());
        }
        return plugins;
    }

    private boolean isPlugin(final Class<?> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        final Long counter = Observable.fromArray(fields)
            .filter(field -> field.isAnnotationPresent(Infusion.class))
            .count().blockingGet();
        return 0 < counter;
    }
}
