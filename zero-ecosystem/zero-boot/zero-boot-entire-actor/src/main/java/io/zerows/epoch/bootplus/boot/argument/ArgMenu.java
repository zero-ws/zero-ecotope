package io.zerows.epoch.bootplus.boot.argument;

import io.zerows.epoch.based.constant.KName;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023-06-12
 */
public class ArgMenu extends ArgIn {
    private final ConcurrentMap<String, ArgVar> stored = new ConcurrentHashMap<>();

    private ArgMenu(final String[] args) {
        {
            // 0 = path
            this.stored.put(KName.PATH, ArgVar
                .of(KName.PATH)
                .valueDefault("init/map/menu.yml"));
        }
        this.initialize(args);
    }

    public static ArgMenu of(final String[] args) {
        return new ArgMenu(args);
    }

    @Override
    public <T> T value(final String name) {
        final ArgVar var = this.stored.getOrDefault(name, null);
        if (Objects.isNull(var)) {
            return null;
        }
        return var.value();
    }

    @Override
    protected List<String> names() {
        return List.of(
            KName.PATH
        );
    }

    @Override
    protected ConcurrentMap<String, ArgVar> definition() {
        return this.stored;
    }
}
