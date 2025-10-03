package io.zerows.epoch.corpus.container.osgi.command;

import io.vertx.core.Vertx;
import io.zerows.epoch.corpus.container.store.under.StoreVertx;
import io.zerows.epoch.sdk.osgi.OCommand;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.Set;


/**
 * @author lang : 2024-05-02
 */
public class CommandRunningVertx implements OCommand {

    @Override
    public void execute(final Bundle caller) {
        final StoreVertx storedVertx = StoreVertx.of(caller);
        final Set<String> names = storedVertx.keys();
        System.out.println("Running Vertx Instance: ");
        names.forEach((name) -> {
            final Vertx vertx = storedVertx.vertx(name);
            Objects.requireNonNull(vertx);
            System.out.println("name: " + name + ", vertx: " + vertx.hashCode());
        });
    }
}
