package io.zerows.boot.test.metadata;

import io.r2mo.base.program.R2VarSet;
import io.zerows.epoch.constant.KName;

import java.util.List;

/**
 * @author lang : 2023-06-12
 */
public class ArgMenu extends ArgIn {
    private final R2VarSet stored = R2VarSet.of();

    private ArgMenu(final String[] args) {
        {
            // 0 = path
            this.stored.add(KName.PATH, "init/map/menu.yml");
        }
        this.initialize(args);
    }

    public static ArgMenu of(final String[] args) {
        return new ArgMenu(args);
    }

    @Override
    protected List<String> names() {
        return List.of(
            KName.PATH
        );
    }

    @Override
    protected R2VarSet varSet() {
        return this.stored;
    }
}
