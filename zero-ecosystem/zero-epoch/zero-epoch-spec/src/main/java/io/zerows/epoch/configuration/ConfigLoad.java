package io.zerows.epoch.configuration;

import io.zerows.epoch.spec.YmConfiguration;
import io.zerows.specification.app.HApp;

/**
 * @author lang : 2025-10-06
 */
public interface ConfigLoad {

    /**
     * 此处的 HApp 中只要包含
     * <pre>
     *     id       - 应用id
     *     tenant   - 租户id
     *     name     - 应用名称
     *     ns       - 应用名空间
     * </pre>
     *
     * @param app 应用信息
     * @return 配置对象
     */
    YmConfiguration configure(HApp app);
}
