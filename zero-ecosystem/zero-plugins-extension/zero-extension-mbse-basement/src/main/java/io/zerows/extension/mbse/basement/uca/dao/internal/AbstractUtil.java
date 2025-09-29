package io.zerows.extension.mbse.basement.uca.dao.internal;

import io.zerows.core.fn.FnZero;
import io.zerows.core.uca.log.Annal;
import io.zerows.core.uca.qr.Criteria;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.atom.data.DataEvent;
import io.zerows.extension.mbse.basement.eon.em.EventType;
import io.zerows.extension.mbse.basement.exception._417DataAtomNullException;
import io.zerows.extension.mbse.basement.uca.jdbc.AoConnection;
import io.zerows.extension.mbse.basement.uca.jooq.JQEngine;
import io.zerows.extension.mbse.basement.uca.metadata.AoSentence;
import io.zerows.specification.modeling.HAtom;

import java.util.function.Function;

/**
 * 抽象工具类
 */
@SuppressWarnings("unchecked")
public abstract class AbstractUtil<T extends AoBinder> implements AoBinder<T> {
    // 子类继承
    protected transient AoConnection connection;
    protected transient AoSentence sentence;
    // 元数据
    protected transient DataAtom atom;
    protected transient JQEngine jooq;

    @Override
    public T on(final AoSentence sentence) {
        this.sentence = sentence;
        this.jooq.bind(sentence);       // 绑定 AoSentence
        return (T) this;
    }

    @Override
    public T on(final AoConnection connection) {
        this.connection = connection;
        this.jooq = JQEngine.create(connection.getDSL());
        /* 绑定DSLContext */
        return (T) this;
    }

    @Override
    public T on(final HAtom atom) {
        /*
         * TODO: 此处有一个强制转换，目前版本中只使用 DataAtom，后期更改
         * */
        this.atom = (DataAtom) atom;
        return (T) this;
    }

    // ---------------------------
    protected DataEvent event() {
        /* 检查 this.io.vertx.up.argument / this.sentence */
        FnZero.outWeb(null == this.atom, _417DataAtomNullException.class, this.getClass());
        return DataEvent.create(this.atom, this.sentence).init(EventType.SINGLE);
    }

    protected DataEvent events() {
        FnZero.outWeb(null == this.atom, _417DataAtomNullException.class, this.getClass());
        return DataEvent.create(this.atom, this.sentence).init(EventType.BATCH);
    }

    protected <ID> DataEvent irIDs(final ID... ids) {
        return this.events().keys(ids);
    }

    protected DataEvent irCond(final Criteria criteria) {
        return this.event().criteria(criteria);
    }

    // Output Record, Record[]
    @SuppressWarnings("all")
    protected <T> T output(final DataEvent event, final Function<DataEvent, DataEvent> executor, final boolean isArray) {
        event.consoleAll();
        final DataEvent response = executor.apply(event);
        if (isArray) {
            return (T) response.dataA();
        } else {
            return (T) response.dataR();
        }
    }


    protected Annal getLogger() {
        return Annal.get(this.getClass());
    }
}
