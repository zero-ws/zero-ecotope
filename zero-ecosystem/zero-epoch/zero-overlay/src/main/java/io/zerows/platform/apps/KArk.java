package io.zerows.platform.apps;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.app.HLot;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

/**
 * 「默认配置容器」
 *
 * @author lang : 2023-06-06
 */
@Slf4j
public class KArk implements HArk {
    private final HLot owner;
    private final HApp app;
    private KDS kds;

    private KArk(final HApp app) {
        this.app = app;
        this.owner = KTenant.getOrCreate(app.tenant());

        final KDS kds = KDS.of(app.id());
        this.kds = Objects.isNull(kds) ? KDS.of(app.name()) : kds;
    }

    public static HArk of(final HApp app) {
        return new KArk(app);
    }

    @Override
    public HApp app() {
        return this.app;
    }

    @Override
    public KDS datasource() {
        return this.kds;
    }

    @Override
    public Database database() {
        final DBS dbs = this.kds.findRunning(this.app.name());
        if (Objects.isNull(dbs)) {
            log.warn("[ ZERO ] 无法找到数据源信息，应用名称：{}", this.app.name());
            return null;
        }
        return dbs.getDatabase();
    }

    @Override
    public HLot owner() {
        return this.owner;
    }

    @Override
    public HArk apply(final HArk target) {
        if (Objects.nonNull(target) && target instanceof final KArk targetRef) {
            this.apply(targetRef.kds);
            this.apply(targetRef.app);
            this.apply(targetRef.owner);
        }
        return this;
    }

    @Override
    public HArk apply(final KDS kds) {
        this.kds = kds;
        return this;
    }

    @Override
    public HArk apply(final HApp app) {
        Optional.ofNullable(app).ifPresent(this.app::apply);
        return this;
    }

    @Override
    public HArk apply(final HLot lot) {
        Optional.ofNullable(lot).ifPresent(this.owner::apply);
        return this;
    }
}
