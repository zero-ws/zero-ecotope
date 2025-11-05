package io.zerows.extension.crud.uca;

import io.zerows.extension.crud.common.Pooled;

/**
 * @author lang : 2023-08-04
 */
public interface AgonicView {

    static Agonic view(final boolean isMy) {
        if (isMy) {
            return Pooled.CCT_AGONIC.pick(AgonicViewMy::new, AgonicViewMy.class.getName());
        } else {
            return Pooled.CCT_AGONIC.pick(AgonicViewFull::new, AgonicViewFull.class.getName());
        }
    }

    static Agonic view() {
        return Pooled.CCT_AGONIC.pick(AgonicViewSync::new, AgonicViewSync.class.getName());
    }
}
