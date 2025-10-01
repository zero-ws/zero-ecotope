package io.zerows.extension.commerce.rbac.agent.service.view;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.security.authority.HValve;
import io.zerows.extension.commerce.rbac.atom.ScOwner;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SPacketDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SPathDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SPacket;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SPath;
import io.zerows.extension.commerce.rbac.uca.acl.rapier.Quest;
import io.zerows.extension.commerce.rbac.uca.ruler.AdmitValve;
import io.zerows.extension.commerce.rbac.util.Sc;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class RuleService implements RuleStub {
    private static final Cc<String, HValve> CC_VALVE = Cc.openThread();

    @Override
    public Future<JsonObject> regionAsync(final SPath input) {
        /*
         * Major Path Configuration
         * 1. Not null and `runComponent` is not null
         * 2. `parentId` is null
         * 3. Sort By `uiSort`
         */
        return Sc.cachePath(input, path -> Ux.Jooq.on(SPathDao.class).fetchJAsync(KName.PARENT_ID, path.getKey())
            .compose(children -> {
                /*
                 * Extract `runComponent` to web `HValve` and then run it based on configured
                 * Information here.
                 */
                final Class<?> clazz = Ut.clazz(path.getRunComponent(), AdmitValve.class);
                if (Objects.isNull(clazz)) {
                    return Ux.future();
                }
                final String cacheKey = path.getSigma() + VString.SLASH + path.getCode();
                final HValve value = CC_VALVE.pick(() -> Ut.instance(clazz), cacheKey);
                final JsonObject pathJ = Ux.toJson(path);
                /*
                 * JsonObject Configuration for SPath here
                 */
                Ut.valueToJObject(pathJ,
                    // UI Configuration
                    KName.UI_CONFIG,
                    KName.UI_CONDITION,
                    KName.UI_SURFACE,
                    // DM Configuration
                    KName.DM_CONDITION,
                    KName.DM_CONFIG,
                    // metadata / mapping
                    KName.METADATA,
                    KName.MAPPING
                );
                /*
                 * Build map based on `code` for Area usage
                 * `children` of pathJ
                 */
                if (!children.isEmpty()) {
                    pathJ.put(KName.CHILDREN, children);
                }
                return value.configure(pathJ);
            }));
    }

    @Override
    public Future<JsonObject> regionAsync(final JsonObject pathData, final ScOwner owner) {
        /*
         * 查找合法的被影响资源，此处会有很多种变化
         * - 每个Region影响的资源可能是多个值，也可能是一个值
         * - 由于Region在前端读取的时候已经是执行过 type 维度的条件，所以此处不再考虑 type 参数
         *   type 直接从 region 的 runType 中提取
         * - 此处提取时直接按照 region codes + sigma 二者的值来提取 Pocket 定义
         */
        final SPath path = Ux.fromJson(pathData, SPath.class);
        return this.packetAsync(path)
            .compose(packets -> Quest.syntax().fetchAsync(pathData, packets, owner));
    }

    private Future<List<SPacket>> packetAsync(final SPath input) {
        if (Objects.isNull(input)) {
            return Ux.futureL();
        }
        return Sc.cachePocket(input, path -> Ux.Jooq.on(SPathDao.class).<SPath>fetchAsync(KName.PARENT_ID, path.getKey())
            .compose(children -> {
                // CODE IN (?, ?, ?) AND SIGMA = ?
                final JsonObject condition = Ux.whereAnd()
                    .put(KName.SIGMA, path.getSigma());
                final JsonArray codes = new JsonArray().add(path.getCode());
                children.forEach(child -> codes.add(child.getCode()));
                condition.put(KName.CODE + ",i", codes);
                // SPath -> SPacket
                return Ux.Jooq.on(SPacketDao.class).fetchAsync(condition);
            }));
    }

    @Override
    public Future<JsonObject> regionAsync(final JsonObject condition, final JsonObject viewData) {
        return Ux.Jooq.on(SPathDao.class).<SPath>fetchOneAsync(condition)
            .compose(this::packetAsync)
            .compose(packets -> {
                final Set<String> resources = Ut.elementSet(packets, SPacket::getResource);
                final JsonObject normalized = Ut.elementSubset(viewData, resources);
                return Quest.syntax().syncAsync(normalized);
            });
    }
}
