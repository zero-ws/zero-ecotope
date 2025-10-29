package io.zerows.platform.enums;

/**
 * @author lang : 2023-05-31
 */
public final class EmSecure {
    private EmSecure() {
    }

    /**
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    public enum ScDim {
        NONE,       // 无维度定义
        FLAT,       // 列表型维度定义
        TREE,       // 树型维度定义
        FOREST,     // 森林模式（多树）
    }

    /**
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    public enum ScIn {
        NONE,       // 无数据源
        WEB,        // 静态专用
        DAO,        // 动态：静态接口
        ATOM,       // 动态：动态接口
        DEFINE,     // 自定义，组件使用模式
    }

    /**
     * ACL的作用周期
     * - DELAY：延迟执行，处理影响型请求专用
     * - EAGER：及时执行，处理当前请求专用
     * - ERROR：配置错误导致ACL的作用周期失效
     *
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    public enum ActPhase {
        DELAY, // Delay for ACL control
        EAGER, // Eager for ACL control, this control should impact current request
        ERROR, // Error phase
    }


    /**
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    public enum AuthWord {
        AND, // Perm1 + Perm2 + Perm3
        OR,  // Perm1,  Perm2,  Perm3
    }

    public enum CertType {
        JKS,
        PKCS12,
        PEM
    }

    /**
     * @author lang : 2023-05-20
     */
    public enum SecurityLevel {
        // 应用级
        Application(0B0001),
        // 管理级
        Admin(0B0010),
        // 开发级，建模管理，云端部署
        Development(0B0100),
        // 超级账号
        Supervisor(0B1000);

        private final int code;

        SecurityLevel(final int code) {
            this.code = code;
        }

        public int code() {
            return this.code;
        }
    }
}
