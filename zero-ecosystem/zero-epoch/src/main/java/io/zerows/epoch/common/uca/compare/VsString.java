package io.zerows.epoch.common.uca.compare;

import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.VString;
import io.zerows.ams.util.UtBase;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
final class VsString extends AbstractSame {
    public VsString() {
        super(String.class);
    }

    @Override
    public boolean isAnd(final Object valueOld, final Object valueNew) {
        if (UtBase.isDate(valueOld)) {
            return Objects.requireNonNull(VsSame.get(LocalDateTime.class)).is(valueOld, valueNew);
        } else if (UtBase.isJArray(valueOld)) {
            return Objects.requireNonNull(VsSame.get(JsonArray.class)).is(valueOld, valueNew);
        } else {
            return super.isAnd(valueOld, valueNew);
        }
    }

    @Override
    public boolean isXor(final Object valueOld, final Object valueNew) {
        final Object valueSelect = Objects.isNull(valueOld) ? valueNew : valueOld;
        if (!(valueSelect instanceof String)) {
            return false;
        }
        final String oldStr = Objects.isNull(valueOld) ? VString.EMPTY : valueOld.toString().trim();
        final String newStr = Objects.isNull(valueNew) ? VString.EMPTY : valueNew.toString().trim();
        return oldStr.equals(newStr);
    }
}
