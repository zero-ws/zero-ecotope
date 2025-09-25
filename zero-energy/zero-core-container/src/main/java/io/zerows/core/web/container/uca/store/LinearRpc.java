package io.zerows.core.web.container.uca.store;

import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-05-03
 */
class LinearRpc extends LinearAgent implements StubLinear {
    LinearRpc(final Bundle bundle) {
        super(bundle);
    }

    /*
     * 此处不重写任何 Class<?>，因为输入的 Class<?> 已经发生了变化，但发布的内容和 Agent 是一致的
     * RPC 的 Agent 和普通 Agent 都是相同的 @Agent 注解类
     */
}
