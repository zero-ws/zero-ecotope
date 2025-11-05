package io.zerows.extension.crud.uca.input;

import io.zerows.extension.crud.common.Pooled;

/**
 * @author lang : 2023-08-04
 */
public interface PreAudit {

    static Pre audit(final boolean created) {
        if (created) {
            return Pooled.CCT_PRE.pick(PreAuditCreatePre::new, PreAuditCreatePre.class.getName());
        } else {
            return Pooled.CCT_PRE.pick(PreAuditUpdatePre::new, PreAuditUpdatePre.class.getName());
        }
    }

    static Pre audit() {
        return Pooled.CCT_PRE.pick(PreAuditDeletePre::new, PreAuditDeletePre.class.getName());
    }
}
