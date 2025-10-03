package io.zerows.specification.modeling.property;

import io.zerows.metadata.program.Kv;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface OExpression {

    Object after(Kv<String, Object> kv);
}
