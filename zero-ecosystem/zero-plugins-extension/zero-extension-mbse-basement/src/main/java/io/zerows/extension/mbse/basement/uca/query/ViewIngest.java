package io.zerows.extension.mbse.basement.uca.query;

import io.zerows.epoch.common.uca.qr.Criteria;
import io.zerows.extension.mbse.basement.atom.element.DataTpl;
import org.jooq.Condition;

class ViewIngest implements Ingest {
    @Override
    public Condition onCondition(final DataTpl tpl,
                                 final Criteria criteria) {
        return null;
    }
}
