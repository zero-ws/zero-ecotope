package io.zerows.specification.modeling;

import io.zerows.epoch.common.shared.reference.RQuery;
import io.zerows.epoch.common.shared.reference.RQuote;
import io.zerows.epoch.common.shared.reference.RReference;
import io.zerows.epoch.common.shared.reference.RResult;

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
