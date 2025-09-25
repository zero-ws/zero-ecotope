package io.zerows.extension.mbse.basement.uca.jooq;

import io.zerows.extension.mbse.basement.atom.data.DataEvent;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

@SuppressWarnings("all")
class JQAggregate extends AbstractJQQr {
    private final transient JQTerm term;

    JQAggregate(final DSLContext context) {
        super(context);
        this.term = new JQTerm(context);
    }

    DataEvent count(final DataEvent event) {
        return this.aggr(event, (tables, ingest) -> {
            final SelectWhereStep query = this.term.getSelectSample(event, tables, ingest);
            return (long) query.fetch().size();
        });
    }
}
