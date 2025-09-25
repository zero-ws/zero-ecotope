package io.zerows.extension.mbse.basement.uca.metadata;

import io.zerows.extension.mbse.basement.uca.jdbc.AoConnection;
import io.zerows.extension.mbse.basement.uca.sql.SqlDDLBuilder;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractReflector implements AoReflector {
    protected final transient AoConnection connection;
    private final transient SqlDDLBuilder builder = SqlDDLBuilder.create();

    public AbstractReflector(final AoConnection connection) {
        this.connection = connection;
    }

    @Override
    public long getTotalRows(final String tableName) {
        final String sql = this.builder.buildRowsSQL(tableName);
        return this.connection.count(sql);
    }

    @Override
    public long getNullRows(final String tableName, final String column) {
        final String sql = this.builder.buildNullSQL(tableName, column);
        return this.connection.count(sql);
    }

    @Override
    public String getFieldType(final ConcurrentMap<String, Object> columnDetail) {
        return "";
    }

    @Override
    public ConcurrentMap<String, Object> getColumnDetails(final String column, final List<ConcurrentMap<String, Object>> columnDetailList) {
        return null;
    }

    @Override
    public String getDataTypeWord() {
        return null;
    }

    @Override
    public String getLengthWord() {
        return null;
    }
}
