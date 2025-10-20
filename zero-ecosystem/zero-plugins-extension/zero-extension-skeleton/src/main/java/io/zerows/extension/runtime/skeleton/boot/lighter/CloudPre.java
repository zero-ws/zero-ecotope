package io.zerows.extension.runtime.skeleton.boot.lighter;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * 新容器启动流程，启动之前的云端环境的基础环境准备流程，前置条件
 * <pre><code>
 *     1. mvn 已编译好 inst 指令程序
 *     2. 环境变量已加载完成
 *        开发环境：.env.deployment
 *        生产环境：run-env.sh
 * </code></pre>
 * 上述步骤完成之后就是此流程的执行
 * <pre><code>
 *     1. 检查 .env.lock 文件，文件存在跳过
 *        1.1. 数据库检查，DDL 初始化
 *        1.2. 执行 run-configure.sh 脚本（Ansible一键部署）
 *     2. 启动容器环境（本组件不做，跳过）
 * </code></pre>
 *
 * @author lang : 2024-08-14
 */
@Deprecated
public class CloudPre extends ZeroPre {

    @Override
    public Boolean beforeStart(final Vertx vertx, final JsonObject options) {
        // 标准插件初始化 Infix
        //        final Boolean started = super.beforeStart(vertx, options);
        //        if (!started) {
        //            return Boolean.FALSE;
        //        }

        return Boolean.TRUE;
    }
}
