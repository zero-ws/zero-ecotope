package io.zerows.epoch.application;

import io.zerows.platform.constant.VName;

/**
 * @author lang : 2023-05-29
 */
@Deprecated
public interface YmlOption {
    /**
     * <pre><code>
     *     category:
     *     hostname:
     *     port:
     *     instance:
     *     username:
     *     password:
     *     driverClassName:
     *     jdbcUrl:
     *     options:
     * </code></pre>
     */
    interface database extends domain {
        String CATEGORY = VName.CATEGORY;
        String INSTANCE = VName.INSTANCE;
        String OPTIONS = VName.OPTIONS;
        String DRIVER_CLASS_NAME = "driverClassName";
        String JDBC_URL = "jdbcUrl";
    }

    /**
     * <pre><code>
     *     hostname:
     *     port:
     *     password:
     *     username:
     * </code></pre>
     */
    interface domain {
        String HOST = VName.HOST;
        String HOSTNAME = VName.HOSTNAME;
        String PASSWORD = VName.PASSWORD;
        String PORT = VName.PORT;
        String USERNAME = VName.USERNAME;
    }

    /**
     * <pre><code>
     *     component:
     *     config:
     * </code></pre>
     */
    interface component {
        String COMPONENT = VName.COMPONENT;
        String CONFIG = VName.CONFIG;
    }
}
