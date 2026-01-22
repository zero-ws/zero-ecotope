package io.zerows.extension.module.rbac.boot;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KWeb;
import io.zerows.extension.module.rbac.domain.tables.pojos.SResource;
import io.zerows.extension.module.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.module.rbac.metadata.ScConfig;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2024-07-11
 */
class ScGenerated {
    private static final ScConfig CONFIG = MDRBACManager.of().config();

    static String valuePassword() {
        return CONFIG.getInitializePassword();
    }

    static Future<List<SUser>> valueAuth(final JsonArray userA, final String sigma) {
        final List<SUser> users = Ux.fromJson(userA, SUser.class);
        users.forEach(user -> {
            user.setKey(UUID.randomUUID().toString());
            user.setActive(Boolean.TRUE);
            /* 12345678 */
            final String initPwd = valuePassword();
            user.setPassword(initPwd);
            user.setSigma(sigma);
            if (Objects.isNull(user.getLanguage())) {
                user.setLanguage(KWeb.ARGS.V_LANGUAGE);
            }
        });
        return Ux.future(users);
    }

    static String valueProfile(final SResource resource) {
        /*
         * Get Role/Group/Tree modes
         */
        final String modeRole = resource.getModeRole();
        final String modeGroup = resource.getModeGroup();
        if (Ut.isNil(modeGroup)) {
            /*
             * User Mode
             *
             * USER_UNION
             * USER_INTERSECT
             * USER_EAGER
             * USER_LAZY
             */
            return "USER_" + modeRole.toUpperCase(Locale.getDefault());
        } else {
            final String modeTree = resource.getModeTree();
            final String group = modeGroup.toUpperCase(Locale.getDefault()) +
                "_" + modeRole.toUpperCase(Locale.getDefault());
            if (Ut.isNil(modeTree)) {
                /*
                 * Group Mode
                 * HORIZON_XXX
                 * CRITICAL_XXX
                 * OVERLOOK_XXX
                 */
                return group;
            } else {
                /*
                 * Inherit / Child / Parent/ Extend
                 * EXTEND_XXX
                 * PARENT_XXX
                 * CHILD_XXX
                 * INHERIT_XXX
                 */
                return modeTree.toUpperCase(Locale.getDefault()) +
                    "_" + group;
            }
        }
    }
}
