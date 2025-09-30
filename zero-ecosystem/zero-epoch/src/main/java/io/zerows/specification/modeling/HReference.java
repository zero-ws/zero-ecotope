package io.zerows.specification.modeling;

import io.zerows.common.reference.RQuery;
import io.zerows.common.reference.RQuote;
import io.zerows.common.reference.RReference;
import io.zerows.common.reference.RResult;

import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface HReference {

    RReference refData(String name);

    ConcurrentMap<String, RQuote> refInput();

    ConcurrentMap<String, RQuery> refQr();

    ConcurrentMap<String, RResult> refOutput();
}
