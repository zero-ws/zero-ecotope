package io.zerows.specification.modeling;

import io.zerows.platform.metadata.RQuery;
import io.zerows.platform.metadata.RQuote;
import io.zerows.platform.metadata.RReference;
import io.zerows.platform.metadata.RResult;

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
