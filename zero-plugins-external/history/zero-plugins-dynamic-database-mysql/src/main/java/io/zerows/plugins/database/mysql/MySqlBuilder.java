package io.zerows.plugins.database.mysql;

import io.zerows.extension.mbse.basement.uca.jdbc.AoConnection;
import io.zerows.extension.mbse.basement.uca.metadata.AbstractBuilder;
import io.zerows.extension.mbse.basement.uca.metadata.AoReflector;
import io.zerows.extension.mbse.basement.uca.metadata.AoSentence;

public class MySqlBuilder extends AbstractBuilder {
    /* 隐藏实现，外部不可初始化 */
    MySqlBuilder(final AoConnection conn) {
        super(conn);
    }

    @Override
    public AoSentence getSentence() {
        return new MySqlSentence(this.conn.getDatabase());
    }

    @Override
    public AoReflector getReflector() {
        return new MySqlReflector(this.conn);
    }
}
