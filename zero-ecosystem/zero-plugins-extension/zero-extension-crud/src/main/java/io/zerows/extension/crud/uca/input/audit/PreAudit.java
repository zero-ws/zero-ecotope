package io.zerows.extension.crud.uca.input.audit;

import io.zerows.extension.crud.eon.Pooled;
import io.zerows.extension.crud.uca.input.Pre;

/**
 * @author lang : 2023-08-04
 */
public interface PreAudit {

    static Pre audit(final boolean created) {
        if (created) {
            return Pooled.CCT_PRE.pick(AuditCreatePre::new, AuditCreatePre.class.getName());
        } else {
            return Pooled.CCT_PRE.pick(AuditUpdatePre::new, AuditUpdatePre.class.getName());
        }
    }

    static Pre audit() {
        return Pooled.CCT_PRE.pick(AuditDeletePre::new, AuditDeletePre.class.getName());
    }
}
