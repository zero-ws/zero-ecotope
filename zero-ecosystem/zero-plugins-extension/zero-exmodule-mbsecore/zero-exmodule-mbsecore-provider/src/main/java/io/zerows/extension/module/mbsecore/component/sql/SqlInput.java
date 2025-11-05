package io.zerows.extension.module.mbsecore.component.sql;

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
            Ut.itList(params, (findRunning, index) -> Fn.safeJvm(() -> OxFiller.set(statement, index, findRunning)));
            return statement;
        };
    }*/
}
