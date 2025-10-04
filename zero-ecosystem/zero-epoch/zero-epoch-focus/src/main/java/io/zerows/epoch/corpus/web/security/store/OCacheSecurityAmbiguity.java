package io.zerows.epoch.corpus.web.security.store;

import io.zerows.epoch.metadata.security.Aegis;
import io.zerows.sdk.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-04-22
 */
class OCacheSecurityAmbiguity extends AbstractAmbiguity implements OCacheSecurity {
    private final Set<Aegis> walls = new HashSet<>();
    private final ConcurrentMap<String, Set<Aegis>> wallMap = new ConcurrentHashMap<>();

    OCacheSecurityAmbiguity(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public Set<Aegis> value() {
        return this.walls;
    }

    @Override
    public OCacheSecurity add(final Set<Aegis> walls) {
        this.walls.addAll(walls);
        // 同步更新
        walls.forEach(this::add);
        return this;
    }

    @Override
    public OCacheSecurity remove(final Set<Aegis> walls) {
        this.walls.removeAll(walls);
        // 同步移除
        walls.forEach(this::remove);
        return this;
    }

    @Override
    public OCacheSecurity remove(final Aegis wall) {
        if (!this.wallMap.containsKey(wall.getPath())) {
            return this;
        }
        final Set<Aegis> walls = this.wallMap.get(wall.getPath());
        walls.remove(wall);
        if (walls.isEmpty()) {
            this.wallMap.remove(wall.getPath());
        } else {
            this.wallMap.put(wall.getPath(), walls);
        }
        return this;
    }

    @Override
    public OCacheSecurity add(final Aegis wall) {
        if (!this.wallMap.containsKey(wall.getPath())) {
            this.wallMap.put(wall.getPath(), new HashSet<>());
        }

        /*
         * 1. group by `path`, when you define more than one wall in one path, you can collect
         * all the wall into Set.
         * 2. The order will be re-calculated by each group
         * 3. But you could not define `path + order` duplicated wall
         */
        this.wallMap.get(wall.getPath()).add(wall);
        return this;
    }


    @Override
    public synchronized ConcurrentMap<String, Set<Aegis>> valueWall() {
        return this.wallMap;
    }
}
