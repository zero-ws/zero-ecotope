package io.mature.boot.argument;

import io.zerows.ams.constant.VString;
import io.zerows.core.constant.KName;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023-06-11
 */
public class ArgLoad extends ArgIn {
    private final ConcurrentMap<String, ArgVar> stored = new ConcurrentHashMap<>();

    private ArgLoad(final String[] args) {
        {
            // 0 = path
            this.stored.put(KName.PATH, ArgVar
                .of(KName.PATH)
                .valueDefault("init/oob"));
            // 1 = oob
            this.stored.put("oob", ArgVar
                .of("oob")
                .valueDefault(Boolean.TRUE)
                .bind(Boolean.class));
            // 2 = prefix
            this.stored.put(KName.PREFIX, ArgVar
                .of(KName.PREFIX)
                .valueDefault(VString.EMPTY));
        }
        this.initialize(args);
    }

    public static ArgLoad of(final String[] args) {
        return new ArgLoad(args);
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
            KName.PATH,
            "oob",          // 默认 TRUE
            KName.PREFIX    // 默认 ""
        );
    }

    @Override
    protected ConcurrentMap<String, ArgVar> definition() {
        return this.stored;
    }
}
