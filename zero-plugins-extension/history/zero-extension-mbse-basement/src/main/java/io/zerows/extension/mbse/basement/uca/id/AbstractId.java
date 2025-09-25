package io.zerows.extension.mbse.basement.uca.id;

import io.zerows.extension.mbse.basement.atom.element.DataMatrix;
import io.zerows.extension.mbse.basement.domain.tables.pojos.MJoin;
import io.zerows.extension.mbse.basement.util.Ao;
import io.zerows.specification.modeling.HRecord;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

abstract class AbstractId implements AoId {

    @Override
    public void connect(final HRecord record,
                        final ConcurrentMap<String, DataMatrix> keys,
                        final ConcurrentMap<String, DataMatrix> matrix,
                        final Set<MJoin> joins) {
        /* 设置主键 */
        Ao.connect(record, keys, matrix, joins.stream()
            .map(MJoin::getEntityKey).collect(Collectors.toSet()));
    }
}
