package io.zerows.extension.crud.uca.input.view;

import io.zerows.extension.crud.eon.Pooled;
import io.zerows.extension.crud.uca.input.Pre;

/**
 * @author lang : 2023-08-04
 */
public interface PreView {

    static Pre apeak(final boolean isMy) {
        if (isMy) {
            return Pooled.CCT_PRE.pick(ApeakMyPre::new, ApeakMyPre.class.getName());
        } else {
            return Pooled.CCT_PRE.pick(ApeakPre::new, ApeakPre.class.getName());
        }
    }
}
