package io.zerows.extension.crud.uca.input;

import io.zerows.extension.crud.common.Pooled;

/**
 * @author lang : 2023-08-04
 */
public interface PreView {

    static Pre apeak(final boolean isMy) {
        if (isMy) {
            return Pooled.CCT_PRE.pick(PreViewApeakMy::new, PreViewApeakMy.class.getName());
        } else {
            return Pooled.CCT_PRE.pick(PreViewApeak::new, PreViewApeak.class.getName());
        }
    }
}
