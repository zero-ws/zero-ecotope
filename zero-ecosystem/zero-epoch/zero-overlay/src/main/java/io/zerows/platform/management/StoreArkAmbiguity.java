package io.zerows.platform.management;

import io.r2mo.typed.common.MultiKeyMap;
import io.zerows.platform.constant.VName;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.app.HLot;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.base.UtBase;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-07-08
 */
@Slf4j
class StoreArkAmbiguity extends AbstractAmbiguity implements StoreArk {

    private static final MultiKeyMap<HArk> STORED = new MultiKeyMap<>();

    StoreArkAmbiguity(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public Set<String> keys() {
        return STORED.keySet();
    }

    @Override
    public HArk valueGet(final String key) {
        return STORED.getOr(key);
    }

    @Override
    public StoreArk add(final HArk ark) {
        if (Objects.isNull(ark) || Objects.isNull(ark.app())) {
            log.warn("[ ZERO ] ( App ) 输入的 HArk 对象无效，无法注册！");
            return this;
        }
        // 0. 主 key
        final String keyOfCache = UtBase.keyApp(ark);
        final HApp app = ark.app();


        // 1. 基础规范：name / ns
        final String name = app.name();
        final String ns = app.ns();


        // 2. 扩展规范 code / appKey / id
        final String code = app.option(VName.CODE);
        final String appKey = app.option(VName.APP_KEY);
        final String id = app.option(VName.APP_ID);


        // 3. 选择规范 sigma / tenantId
        final String sigma = app.option(VName.SIGMA);
        final HLot lot = ark.owner();
        final String tenantId = Optional.ofNullable(lot).map(HLot::owner).orElse(null);


        // 填充存储
        STORED.put(keyOfCache, ark,
            name, ns,
            code, appKey, id,
            sigma, tenantId
        );
        return this;
    }

    @Override
    public ConcurrentMap<String, HArk> valueMap() {
        return STORED.asMap();
    }

    @Override
    public StoreArk remove(final HArk ark) {
        if (Objects.isNull(ark)) {
            return this;
        }
        final String keyOfCache = UtBase.keyApp(ark);
        STORED.remove(keyOfCache);
        return this;
    }
}
