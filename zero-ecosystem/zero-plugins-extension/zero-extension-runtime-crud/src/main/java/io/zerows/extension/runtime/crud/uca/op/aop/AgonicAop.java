package io.zerows.extension.runtime.crud.uca.op.aop;

import io.zerows.epoch.enums.typed.ChangeFlag;
import io.zerows.extension.runtime.crud.eon.Pooled;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.op.Agonic;

/**
 * @author lang : 2023-08-04
 */
public interface AgonicAop {
    static Agonic write(final ChangeFlag flag) {
        if (ChangeFlag.ADD == flag) {
            return Pooled.CCT_AGONIC.pick(AgonicCreate::new, AgonicCreate.class.getName());
        } else if (ChangeFlag.DELETE == flag) {
            return Pooled.CCT_AGONIC.pick(AgonicDelete::new, AgonicDelete.class.getName());
        } else {
            return Pooled.CCT_AGONIC.pick(AgonicUpdate::new, AgonicUpdate.class.getName());
        }
    }

    static Agonic write(final IxMod module) {
        return Pooled.CCT_AGONIC.pick(() -> new StandBySave(module), StandBySave.class.getName());
    }
}
