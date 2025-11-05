package io.zerows.extension.crud.uca.input;

import io.zerows.extension.crud.common.Pooled;

/**
 * @author lang : 2023-08-04
 */
public interface PreId {

    static Pre key(final boolean isNew) {
        if (isNew) {
            return Pooled.CCT_PRE.pick(PreIdUuid::new, PreIdUuid.class.getName());
        } else {
            return Pooled.CCT_PRE.pick(PreIdKey::new, PreIdKey.class.getName());
        }
    }

    static Pre ref() {
        return Pooled.CCT_PRE.pick(PreIdRef::new, PreIdRef.class.getName());
    }
}
