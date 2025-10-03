package io.zerows.epoch.database.jooq.operation;

interface INFO {
    String JOOQ_BIND = "( Pojo Bind ) Pojo up.god.file = {0} has been bind to dao {1}, Field mode enabled.";
    String JOOQ_FIELD = "( Pojo ) The field \"{0}\" has been hitted ( converted ) to \"{1}\"";
    String JOOQ_MOJO = "( Pojo ) The analyzed result should be : Revert {0}";
    String JOOQ_PARSE = "( Jooq -> Condition ) Parsed result is condition = {0}.";
}
