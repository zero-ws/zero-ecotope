package io.zerows.platform.metadata;

import io.zerows.platform.constant.VValue;
import io.zerows.platform.enums.EmDS;

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
@Deprecated
public class OldKDS<T extends KDatabase> implements Function<OldKDS<T>, OldKDS<T>> {

    private final ConcurrentMap<EmDS.DB, T> database = new ConcurrentHashMap<>();
    private final Set<T> databaseDynamics = new LinkedHashSet<>();

    public T database() {
        return this.database.getOrDefault(EmDS.DB.PRIMARY, null);
    }

    public T history() {
        return this.database.getOrDefault(EmDS.DB.HISTORY, null);
    }

    public T workflow() {
        return this.database.getOrDefault(EmDS.DB.WORKFLOW, null);
    }

    public OldKDS<T> registry(final EmDS.DB store, final T database) {
        if (Objects.nonNull(database)) {
            this.database.put(store, database);
            if (EmDS.DB.DYNAMIC == store) {
                this.databaseDynamics.add(database);
            }
        }
        return this;
    }

    public OldKDS<T> registry(final Collection<T> databases) {
        this.databaseDynamics.clear();
        this.databaseDynamics.addAll(databases);
        if (VValue.ONE == databases.size()) {
            databases.stream().findFirst()
                .ifPresent(dynamic -> this.database.put(EmDS.DB.DYNAMIC, dynamic));
        }
        return this;
    }

    public Set<T> dynamicSet() {
        return this.databaseDynamics;
    }

    public T dynamic() {
        return this.database.getOrDefault(EmDS.DB.DYNAMIC, null);
    }

    @Override
    public OldKDS<T> apply(final OldKDS<T> target) {
        if (Objects.nonNull(target)) {
            this.database.putAll(target.database);
            this.databaseDynamics.addAll(target.databaseDynamics);
        }
        return this;
    }
}
