package io.zerows.extension.commerce.rbac.util;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.component.environment.DevEnv;
import io.zerows.constant.VString;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.web.cache.Rapid;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SPath;
import io.zerows.extension.commerce.rbac.eon.ScConstant;
import io.zerows.extension.commerce.rbac.uca.logged.ScUser;
import io.zerows.extension.runtime.skeleton.refine.Ke;

import java.util.Objects;
import java.util.function.Function;

class ScCache {

    static <R> Future<R> admitPath(final SPath path, final Function<SPath, Future<R>> executor, final String suffix) {
        if (DevEnv.cacheAdmit()) {
            // Cache Enabled for Default
            final String admitPool = ScConstant.POOL_ADMIN;
            // Each sigma has been mapped to single pool
            final String poolName = admitPool + VString.SLASH + path.getSigma() + VString.SLASH + suffix;
            final Rapid<String, R> rapid = Rapid.object(poolName, 3600);
            return rapid.cached(path.getKey(), () -> executor.apply(path));
        } else {
            return executor.apply(path);
        }
    }

    static Future<JsonObject> view(final RoutingContext context, final String habitus) {
        // habitus 为 Zero Framework 的专用 Session（内置业务会话标识）
        if (Ut.isNil(habitus)) {
            /*
             * Empty bound in current interface instead of other
             * -- Maybe the user has not been logged
             * */
            return Ux.futureJ();
        }

        final String viewKey = Ke.keyView(context);
        final ScUser scUser = ScUser.login(habitus);
        /*
         * 此处需要针对缓存中的 matrix 执行拷贝，后续流程中会直接执行如下流程
         * cache matrix -> Before + Visitant -> 影响 matrix
         *              -> After  + Visitant -> 影响 matrix
         * DataRegion中消费的 matrix 在新版本中会直接被 Cosmo 组件变更，而造成最终的影响
         * 所以读取出来的视图矩阵在此处执行拷贝
         */
        return scUser.view(viewKey)
            .compose(matrix -> Ux.future(Objects.isNull(matrix) ? null : matrix.copy()));
    }
}
