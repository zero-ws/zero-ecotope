package io.zerows.extension.crud.uca;

import io.zerows.extension.crud.common.Pooled;
import io.zerows.platform.enums.typed.ChangeFlag;

/**
 * @author lang : 2023-08-04
 */
public interface AgonicAop {
    static Agonic write(final ChangeFlag flag) {
        if (ChangeFlag.ADD == flag) {
            return Pooled.CCT_AGONIC.pick(AgonicADBCreate::new, AgonicADBCreate.class.getName());
        } else if (ChangeFlag.DELETE == flag) {
            return Pooled.CCT_AGONIC.pick(AgonicADBDelete::new, AgonicADBDelete.class.getName());
        } else {
            return Pooled.CCT_AGONIC.pick(AgonicADBUpdate::new, AgonicADBUpdate.class.getName());
        }
    }

    static Agonic write(final IxMod module) {
        return Pooled.CCT_AGONIC.pick(() -> new AgonicADJStandSave(module), AgonicADJStandSave.class.getName());
    }
}
