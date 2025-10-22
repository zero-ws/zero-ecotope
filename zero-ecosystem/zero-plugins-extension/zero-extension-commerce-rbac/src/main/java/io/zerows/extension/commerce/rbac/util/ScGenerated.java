package io.zerows.extension.commerce.rbac.util;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.extension.commerce.rbac.atom.ScConfig;
import io.zerows.extension.commerce.rbac.bootstrap.ScPin;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.OUser;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SResource;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.skeleton.exception._80219Exception403TokenGeneration;
import io.zerows.extension.skeleton.spi.ScCredential;
import io.zerows.platform.constant.VValue;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author lang : 2024-07-11
 */
class ScGenerated {
    private static final ScConfig CONFIG = ScPin.getConfig();

    static String valuePassword() {
        return CONFIG.getInitializePassword();
    }

    static Future<OUser> valueAuth(final SUser user, final JsonObject inputJ) {
        final String language = inputJ.getString(KName.LANGUAGE, KWeb.ARGS.V_LANGUAGE);
        final JsonObject initializeJ = CONFIG.getInitialize();
        final OUser oUser = Ux.fromJson(initializeJ, OUser.class);
        oUser.setClientId(user.getKey())
            .setClientSecret(Ut.randomString(64))
            .setLanguage(language)
            .setActive(Boolean.TRUE)
            .setKey(UUID.randomUUID().toString());
        return Ux.future(oUser);
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

    static Future<List<OUser>> valueAuth(final List<SUser> users) {
        if (users.isEmpty()) {
            /* Now inserted */
            return Ux.futureL();
        }

        // sigma 值聚集
        final Set<String> sigmaSet = Ut.valueSetString(users, SUser::getSigma);
        if (VValue.ONE != sigmaSet.size()) {
            return FnVertx.failOut(_80219Exception403TokenGeneration.class, sigmaSet.size());
        }
        /*
         * Credential 通道读取，主要读取 KCredential 对象，属性如：
         * - id
         * - sigma
         * - language
         * - realm
         * - grantType
         * 此处主要信息为 realm 和 grantType 两个属性
         */
        final String sigma = sigmaSet.iterator().next();
        return Ux.channelAsync(ScCredential.class, Ux::futureL, stub -> stub.fetchAsync(sigma).compose(credential -> {
            // OUser processing ( Batch Mode )
            final List<OUser> ousers = new ArrayList<>();
            users.stream().map(user -> new OUser()
                    .setActive(Boolean.TRUE)
                    .setKey(UUID.randomUUID().toString())
                    .setClientId(user.getKey())
                    .setClientSecret(Ut.randomString(64))
                    .setScope(credential.getRealm())
                    .setLanguage(credential.getLanguage())
                    .setGrantType(credential.getGrantType()))
                .forEach(ousers::add);
            return Ux.future(ousers);
        }));
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
