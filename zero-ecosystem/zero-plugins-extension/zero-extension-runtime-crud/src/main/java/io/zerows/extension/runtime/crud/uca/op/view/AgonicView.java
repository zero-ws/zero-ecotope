package io.zerows.extension.runtime.crud.uca.op.view;

import io.zerows.extension.runtime.crud.uca.op.Agonic;
import io.zerows.extension.runtime.crud.eon.Pooled;

/**
 * @author lang : 2023-08-04
 */
public interface AgonicView {

    static Agonic view(final boolean isMy) {
        if (isMy) {
            return Pooled.CCT_AGONIC.pick(ViewMy::new, ViewMy.class.getName());
        } else {
            return Pooled.CCT_AGONIC.pick(ViewFull::new, ViewFull.class.getName());
        }
    }

    static Agonic view() {
        return Pooled.CCT_AGONIC.pick(ViewSync::new, ViewSync.class.getName());
    }
}
