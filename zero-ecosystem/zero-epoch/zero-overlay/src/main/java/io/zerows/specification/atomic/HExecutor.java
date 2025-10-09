package io.zerows.specification.atomic;

import io.vertx.core.json.JsonObject;
import io.zerows.specification.storage.HPath;

import java.util.Set;

/**
 * 「执行器」
 * <hr/>
 * 高阶执行器组件，包含了组件常用的执行器方法，用于执行器的基础定义。
 * <pre><code>
 *     1. component：定义了执行器类
 *     2. config：定义了执行器配置
 * </code></pre>
 *
 * @author lang : 2023-05-21
 */
public interface HExecutor {

    default Class<?> component() {
        return null;
    }

    default JsonObject config() {
        return new JsonObject();
    }

    /**
     * 「执行器」还原
     *
     * @author lang : 2023-05-21
     */
    interface HRestore extends HExecutor {
        /**
         * 抽象路径，还原的具体源位置
         *
         * @return {@link HPath}
         */
        default Set<HPath> sources() {
            return Set.of();
        }

        /**
         * 快速接口，抽象路径（唯一值，只包含一个的情况）
         *
         * @return {@link HPath}
         */
        default HPath source() {
            final Set<HPath> targets = this.sources();
            return targets.isEmpty() ? null : targets.iterator().next();
        }
    }

    /**
     * 「执行器」备份
     *
     * @author lang : 2023-05-21
     */
    interface HBackup extends HExecutor {
        /**
         * 抽象路径，备份的具体目标位置
         *
         * @return {@link HPath}
         */
        default Set<HPath> targets() {
            return Set.of();
        }

        /**
         * 快速接口，抽象路径（唯一值，只包含一个的情况）
         *
         * @return {@link HPath}
         */
        default HPath target() {
            final Set<HPath> targets = this.targets();
            return targets.isEmpty() ? null : targets.iterator().next();
        }
    }

    /**
     * 验证器，约束中专用
     *
     * @author lang : 2023-05-22
     */
    interface HValidator extends HExecutor {
    }
}
