package io.zerows.extension.module.rbac.boot;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.extension.module.rbac.metadata.logged.ScUser;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.platform.enums.EmSecure;
import io.zerows.program.Ux;
import io.zerows.sdk.security.Acl;
import io.zerows.support.Ut;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class ScAcl {

    static void aclRecord(final JsonObject record, final Acl acl) {
        if (Objects.nonNull(acl)) {
            acl.bind(record);
        }
    }

    static JsonArray aclOn(final JsonArray original, final Acl acl) {
        return aclProjection(original, acl, out -> EmSecure.ActPhase.EAGER == out.phase());
    }

    private static JsonArray aclProjection(final JsonArray original, final Acl acl,
                                           final Predicate<Acl> predicate) {
        final JsonArray projection = Ut.valueJArray(original);
        if (Objects.isNull(acl)) {
            /*
             * No acl, default projection defined in S_VIEW
             */
            return projection;
        } else {
            if (predicate.test(acl)) {
                /*
                 * Acl combine with projection
                 */
                return aclProjection(original, acl);
            } else {
                /*
                 * Keep the same, no happen
                 */
                return projection;
            }
        }
    }

    private static JsonArray aclProjection(final JsonArray original, final Acl acl) {
        final Set<String> aclProjection = acl.aclVisible();
        if (aclProjection.isEmpty()) {
            /*
             * acl is empty, it means no definition found in our system
             * If the acl will be modified / configured, it must contains some default fields
             * such as:
             * - active
             * - key
             * - language
             * - sigma
             */
            return original;
        } else {
            /*
             * Mix calculation
             * 1) Acl Projection is major control
             * 2) Matrix Projection is the secondary here, it means that all aclProjection must contains
             */
            final Set<String> replaced = new HashSet<>(aclProjection);
            Ut.itJArray(original, String.class, (field, index) -> {
                if (aclProjection.contains(field)) {
                    replaced.add(field);
                }
            });
            return Ut.toJArray(replaced);
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
        if (scUser == null) {
            return Ux.futureJ();
        }
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
