package io.zerows.metadata.app;

import io.zerows.constant.VValue;
import io.zerows.enums.EmDS;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author lang : 2023-06-06
 */
public class KDS<T extends KDatabase> implements Function<KDS<T>, KDS<T>> {

    private final ConcurrentMap<EmDS.Stored, T> database = new ConcurrentHashMap<>();
    private final Set<T> databaseDynamics = new LinkedHashSet<>();

    public T database() {
        return this.database.getOrDefault(EmDS.Stored.PRIMARY, null);
    }

    public T history() {
        return this.database.getOrDefault(EmDS.Stored.HISTORY, null);
    }

    public T workflow() {
        return this.database.getOrDefault(EmDS.Stored.WORKFLOW, null);
    }

    public KDS<T> registry(final EmDS.Stored store, final T database) {
        if (Objects.nonNull(database)) {
            this.database.put(store, database);
            if (EmDS.Stored.DYNAMIC == store) {
                this.databaseDynamics.add(database);
            }
        }
        return this;
    }

    public KDS<T> registry(final Collection<T> databases) {
        this.databaseDynamics.clear();
        this.databaseDynamics.addAll(databases);
        if (VValue.ONE == databases.size()) {
            databases.stream().findFirst()
                .ifPresent(dynamic -> this.database.put(EmDS.Stored.DYNAMIC, dynamic));
        }
        return this;
    }

    public Set<T> dynamicSet() {
        return this.databaseDynamics;
    }

    public T dynamic() {
        return this.database.getOrDefault(EmDS.Stored.DYNAMIC, null);
    }

    @Override
    public KDS<T> apply(final KDS<T> target) {
        if (Objects.nonNull(target)) {
            this.database.putAll(target.database);
            this.databaseDynamics.addAll(target.databaseDynamics);
        }
        return this;
    }
}
