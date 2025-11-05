package io.zerows.extension.crud.uca.input;

import io.zerows.extension.crud.common.Pooled;
import io.zerows.extension.crud.common.em.QrType;

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
            return Pooled.CCT_PRE.pick(PreQrKeyWhole::new, PreQrKeyWhole.class.getName());
        } else if (QrType.BY_UK == type) {
            return Pooled.CCT_PRE.pick(PreQrKeyUnique::new, PreQrKeyUnique.class.getName());
        } else if (QrType.BY_VK == type) {
            return Pooled.CCT_PRE.pick(PreQrKeyView::new, PreQrKeyView.class.getName());
        } else {
            return Pooled.CCT_PRE.pick(PreQrKeyPrimary::new, PreQrKeyPrimary.class.getName());
        }
    }
}
