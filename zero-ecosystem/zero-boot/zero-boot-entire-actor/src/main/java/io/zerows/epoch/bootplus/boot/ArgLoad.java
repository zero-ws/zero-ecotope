package io.zerows.epoch.bootplus.boot;

import io.r2mo.base.program.R2VarSet;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.constant.VString;

import java.util.List;

/**
 * @author lang : 2023-06-11
 */
public class ArgLoad extends ArgIn {
    private final R2VarSet stored = R2VarSet.of();

    private ArgLoad(final String[] args) {
        {
            // 0 = path
            this.stored.add(KName.PATH, "init/oob");
            // 1 = oob
            this.stored.add("oob", Boolean.TRUE, Boolean.class);
            // 2 = prefix
            this.stored.add(KName.PREFIX, VString.EMPTY);
        }
        this.initialize(args);
    }

    public static ArgLoad of(final String[] args) {
        return new ArgLoad(args);
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
    protected R2VarSet varSet() {
        return this.stored;
    }
}
