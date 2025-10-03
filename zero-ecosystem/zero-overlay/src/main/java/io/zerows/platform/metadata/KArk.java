package io.zerows.platform.metadata;

import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.vital.HOI;

import java.util.Objects;

/**
 * 「默认配置容器」
 *
 * @author lang : 2023-06-06
 */
public class KArk implements HArk {
    private KDS<KDatabase> ds;
    private HOI owner;
    private HApp app;

    private KArk(final String name) {
        this.app = new KApp(name);
        this.owner = new KTenement();
        this.ds = new KDS<>();
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
    @SuppressWarnings("unchecked")
    public <T extends KDatabase> KDS<T> database() {
        return (KDS<T>) this.ds;
    }

    @Override
    public HOI owner() {
        return this.owner;
    }

    @Override
    public HArk apply(final HArk target) {
        if (Objects.nonNull(target) && target instanceof final KArk targetRef) {
            this.app = this.app.apply(targetRef.app);
            this.ds = this.ds.apply(targetRef.ds);
            this.owner = this.owner.apply(targetRef.owner);
        }
        return this;
    }
}
