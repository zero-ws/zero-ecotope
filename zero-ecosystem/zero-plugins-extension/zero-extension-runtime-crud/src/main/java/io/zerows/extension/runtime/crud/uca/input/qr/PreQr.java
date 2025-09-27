package io.zerows.extension.runtime.crud.uca.input.qr;

import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.eon.Pooled;
import io.zerows.extension.runtime.crud.eon.em.QrType;

/**
 * @author lang : 2023-08-04
 */
public interface PreQr {

    /*
     * 1) UniqueKey condition
     * 2) All key condition: sigma = xxx
     * 3) PrimaryKey condition
     * 4) View key
     */
    static Pre qr(final QrType type) {
        if (QrType.ALL == type) {
            return Pooled.CCT_PRE.pick(KeyWholePre::new, KeyWholePre.class.getName());
        } else if (QrType.BY_UK == type) {
            return Pooled.CCT_PRE.pick(KeyUniquePre::new, KeyUniquePre.class.getName());
        } else if (QrType.BY_VK == type) {
            return Pooled.CCT_PRE.pick(KeyViewPre::new, KeyViewPre.class.getName());
        } else {
            return Pooled.CCT_PRE.pick(KeyPrimaryPre::new, KeyPrimaryPre.class.getName());
        }
    }
}
