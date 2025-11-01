package io.zerows.boot.test.metadata;

import io.r2mo.base.program.R2VarSet;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.constant.VString;

import java.util.List;

/**
 * @author lang : 2023-06-12
 */
public class ArgAtom extends ArgIn {

    private final R2VarSet stored = R2VarSet.of();

    private ArgAtom(final String[] args) {
        {
            // 0 = module
            this.stored.add(KName.MODULE, VString.EMPTY);
            // 1 = path
            this.stored.add(KName.PATH, VString.EMPTY);
        }
        this.initialize(args);
    }

    public static ArgAtom of(final String[] args) {
        return new ArgAtom(args);
    }

    @Override
    protected List<String> names() {
        return List.of(
            KName.MODULE,
            KName.PATH
        );
    }

    @Override
    protected R2VarSet varSet() {
        return this.stored;
    }
}
