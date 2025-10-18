package io.zerows.extension.mbse.basement.uca.jdbc;

import io.r2mo.base.dbe.Database;
import io.r2mo.function.Fn;
import io.zerows.epoch.store.DBSActor;
import io.zerows.extension.mbse.basement.exception._80502Exception500EmptySQL;
import io.zerows.extension.mbse.basement.uca.sql.SqlOutput;
import io.zerows.platform.constant.VValue;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("all")
@Slf4j
public class DataConnection implements AoConnection {
    private final Database database;

    /* 一旦调用connect方法证明连接切换到对应的数据库中 */
    @SuppressWarnings("all")
    public DataConnection(final Database database) {
        synchronized (getClass()) {
            this.database = database;
        }
    }

    @Override
    public int execute(final String sql) {
        Fn.jvmKo(Ut.isNil(sql), _80502Exception500EmptySQL.class);
        log.debug("[ ExZERO ] 执行 SQL 语句：{}", sql);
        final DSLContext context = this.getDSL();
        final Query query = context.query(sql);
        final int ret = query.execute();
        return VValue.ZERO <= ret ? ret : VValue.RC_FAILURE;
    }

    @Override
    public Database getDatabase() {
        return this.database;
    }

    @Override
    public Connection getConnection() {
        return DBSActor.ofDBS(this.database).getConnection();
    }

    @Override
    public DSLContext getDSL() {
        return DBSActor.ofDSL(this.database);
    }

    @Override
    public List<ConcurrentMap<String, Object>> select(final String sql,
                                                      final String[] columns) {
        final Result queries = this.fetch(sql);
        return SqlOutput.toMatrix(queries, columns);
    }

    @Override
    public <T> List<T> select(final String sql,
                              final String column) {
        final Result queries = this.fetch(sql);
        return SqlOutput.toList(queries, column);
    }

    private Result fetch(final String sql) {
        Fn.jvmKo(Ut.isNil(sql), _80502Exception500EmptySQL.class);
        log.debug("[ ExZERO ] 执行 SQL Select 语句：{}", sql);
        final DSLContext context = this.getDSL();
        final ResultQuery<Record> query = context.resultQuery(sql);
        return query.fetch();
    }

    @Override
    public Long count(final String sql) {
        Fn.jvmKo(Ut.isNil(sql), _80502Exception500EmptySQL.class);
        log.debug("[ ExZERO ] 执行 SQL Count 语句：{}", sql);
        final DSLContext context = this.getDSL();
        final ResultQuery<Record> query = context.resultQuery(sql);
        final Record record = query.fetchOne();
        return record.getValue(0, Long.class);
    }
}
