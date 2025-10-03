package io.zerows.extension.mbse.basement.uca.change;

import io.zerows.spi.VsExtension;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AtomVsExtension implements VsExtension {
    @Override
    public boolean is(final Object valueOld, final Object valueNew, final Class<?> type) {
        final Adjuster adjuster = Adjuster.get(type);
        if (Objects.isNull(adjuster)) {
            /*
             * 两个值不同
             */
            return Boolean.FALSE;
        } else {
            return adjuster.isSame(valueOld, valueNew);
        }
    }
}
