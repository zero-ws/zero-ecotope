package io.zerows.extension.mbse.basement.uca.metadata;

import io.zerows.constant.VValue;
import io.zerows.extension.mbse.basement.uca.jdbc.AoConnection;

public class CommonVerifier implements AoVerifier {

    private final transient AoConnection conn;
    private final transient AoSentence sentence;

    public CommonVerifier(final AoConnection conn, final AoSentence sentence) {
        this.conn = conn;
        this.sentence = sentence;
    }

    @Override
    public boolean verifyTable(final String tableName) {
        final String sql = this.sentence.expectTable(tableName);
        final Long counter = this.conn.count(sql);
        return VValue.ZERO < counter;
    }

    @Override
    public boolean verifyColumn(final String tableName, final String columnName) {
        return false;
    }

    @Override
    public boolean verifyColumn(final String tableName, final String columnName, final String expectedType) {
        return false;
    }

    @Override
    public boolean verifyConstraint(final String tableName, final String constraintName) {
        return false;
    }
}
