package io.zerows.extension.mbse.basement.uca.sql;

/**
 * Sql引擎输入时的工具类
 */
public final class SqlInput {

    private SqlInput() {
    }
    /*
    public static PreparedStatementCreator prepareStmt(final String sql, final List<Object> params) {
        return connection -> {
            final PreparedStatement statement = connection.prepareStatement(sql);
            Ut.itList(params, (get, index) -> Fn.safeJvm(() -> OxFiller.set(statement, index, get)));
            return statement;
        };
    }*/
}
