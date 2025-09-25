package io.zerows.core.database.jooq.util;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
interface INFO {
    String INQUIRY_MESSAGE = "( Qr ) Processed metadata = {0}.";
    String JOOQ_BIND = "( Pojo Bind ) Pojo up.god.file = {0} has been bind to dao {1}, Field mode enabled.";
    String JOOQ_FIELD = "( Pojo ) The field \"{0}\" has been hitted ( converted ) to \"{1}\"";
    String JOOQ_MOJO = "( Pojo ) The analyzed result should be : Revert {0}";
}
