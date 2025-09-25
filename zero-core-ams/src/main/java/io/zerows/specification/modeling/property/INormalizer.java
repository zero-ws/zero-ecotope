package io.zerows.specification.modeling.property;

import io.zerows.common.program.Kv;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface INormalizer {

    Object before(Kv<String, Object> kv);
}
