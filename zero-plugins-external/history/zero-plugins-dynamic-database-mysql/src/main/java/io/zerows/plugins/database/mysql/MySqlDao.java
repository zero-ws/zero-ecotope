package io.zerows.plugins.database.mysql;

import io.zerows.extension.mbse.basement.uca.dao.AbstractDao;
import io.zerows.extension.mbse.basement.uca.jdbc.AoConnection;
import io.zerows.extension.mbse.basement.uca.metadata.AoSentence;

public class MySqlDao extends AbstractDao {

    MySqlDao(final AoConnection connection) {
        super(connection);
    }

    @Override
    public AoSentence sentence() {
        return new MySqlSentence(this.conn.getDatabase());
    }
}
