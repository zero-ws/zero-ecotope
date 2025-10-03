package io.zerows.epoch.bootplus.boot.argument;

import io.zerows.constant.VString;
import io.zerows.epoch.constant.KName;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023-06-12
 */
public class ArgAtom extends ArgIn {
    private final ConcurrentMap<String, ArgVar> stored = new ConcurrentHashMap<>();

    private ArgAtom(final String[] args) {
        {
            // 0 = module
            this.stored.put(KName.MODULE, ArgVar
                .of(KName.MODULE)
                .valueDefault(VString.EMPTY));
            // 1 = path
            this.stored.put(KName.PATH, ArgVar
                .of(KName.PATH));
        }
        this.initialize(args);
    }

    public static ArgAtom of(final String[] args) {
        return new ArgAtom(args);
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
            KName.MODULE,
            KName.PATH
        );
    }

    @Override
    protected ConcurrentMap<String, ArgVar> definition() {
        return this.stored;
    }
}
