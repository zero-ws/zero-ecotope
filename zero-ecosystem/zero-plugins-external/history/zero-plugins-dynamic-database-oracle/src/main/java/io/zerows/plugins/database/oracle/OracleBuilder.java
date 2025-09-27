package io.zerows.plugins.database.oracle;


import io.zerows.extension.mbse.basement.uca.jdbc.AoConnection;
import io.zerows.extension.mbse.basement.uca.metadata.AbstractBuilder;
import io.zerows.extension.mbse.basement.uca.metadata.AoReflector;
import io.zerows.extension.mbse.basement.uca.metadata.AoSentence;

public class OracleBuilder extends AbstractBuilder {
    /* 隐藏实现，外部不可初始化 */
    OracleBuilder(final AoConnection conn) {
        super(conn);
    }

    @Override
    public AoSentence getSentence() {
        return new OracleSentence(this.conn.getDatabase());
    }

    @Override
    public AoReflector getReflector() {
        return new OracleReflector(this.conn);
    }
}
