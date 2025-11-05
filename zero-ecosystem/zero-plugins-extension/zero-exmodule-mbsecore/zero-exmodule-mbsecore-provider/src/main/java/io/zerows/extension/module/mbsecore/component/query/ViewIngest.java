package io.zerows.extension.module.mbsecore.component.query;

import io.zerows.component.qr.Criteria;
import io.zerows.extension.module.mbsecore.metadata.element.DataTpl;
import org.jooq.Condition;

class ViewIngest implements Ingest {
    @Override
    public Condition onCondition(final DataTpl tpl,
                                 final Criteria criteria) {
        return null;
    }
}
