package io.zerows.platform.metadata;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.vital.HOI;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 「默认配置容器」
 *
 * @author lang : 2023-06-06
 */
@Slf4j
public class KArk implements HArk {
    private KDS kds;
    private HOI owner;
    private HApp app;

    private KArk(final String name) {
        this.app = new KApp(name);
        this.owner = new KTenement();
        this.kds = KDS.of(name);
    }

    public static HArk of(final String name) {
        return new KArk(name);
    }

    public static HArk of() {
        return new KArk(null);
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
    public HOI owner() {
        return this.owner;
    }

    @Override
    public HArk apply(final HArk target) {
        if (Objects.nonNull(target) && target instanceof final KArk targetRef) {
            this.app = this.app.apply(targetRef.app);
            this.kds = targetRef.kds;
            this.owner = this.owner.apply(targetRef.owner);
        }
        return this;
    }
}
