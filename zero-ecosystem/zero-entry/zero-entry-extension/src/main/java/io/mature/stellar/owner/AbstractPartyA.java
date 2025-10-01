package io.mature.stellar.owner;

import io.mature.stellar.ArgoStore;
import io.mature.stellar.vendor.OkB;
import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.enums.Environment;
import io.zerows.common.app.KDS;
import io.zerows.common.app.KGlobal;
import io.zerows.common.app.KIntegration;
import io.zerows.core.database.atom.Database;
import io.zerows.core.exception.boot._40103Exception500ConnectAmbient;
import io.zerows.core.running.boot.KPivot;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.runtime.skeleton.refine.Ke.LOG;

/**
 * @author lang : 2023-06-13
 */
public abstract class AbstractPartyA implements OkA {

    private final Environment environment;

    private final KGlobal global;
    private final HArk ark;

    private final ConcurrentMap<String, OkB> vendors = new ConcurrentHashMap<>();

    protected AbstractPartyA(final Environment environment) {
        this.environment = environment;
        // 直接从存储中提取
        final JsonObject globalJ = ArgoStore.stellar();
        final KGlobal globalRef = Ut.deserialize(globalJ, KGlobal.class);
        this.global = globalRef;
        LOG.Ok.info(this.getClass(), "Global environment has been initialized!! = {0}", globalRef);
        // name = OkB
        globalRef.vendors().forEach(name -> {
            LOG.Ok.info(this.getClass(), "Vendor {0} has been created!", name);
            final KIntegration integration = this.global.integration(name);
            this.vendors.put(name, OkB.of(this, integration));
        });
        {
            // 检查环境是否启动完成
            final HAmbient ambient = KPivot.running();
            Fn.jvmKo(Objects.isNull(ambient), _40103Exception500ConnectAmbient.class);
            // 启动完成则可以直接提取应用信息
            final String appId = globalRef.appId();
            final String sigma = globalRef.sigma();
            if (Ut.isNil(appId)) {
                this.ark = Ke.ark(sigma);
            } else {
                this.ark = Ke.ark(appId);
            }
            Fn.jvmKo(Objects.isNull(this.ark), _40103Exception500ConnectAmbient.class);
            final HApp app = this.ark.app();
            LOG.Ok.info(this.getClass(), "HAmbient Environment has been initialized! = {0}", app.name());
        }
    }

    // --------------- 接口中的特殊API ---------------
    @Override
    public KGlobal partyA() {
        return this.global;
    }

    @Override
    public OkB partyB(final String name) {
        return this.vendors.get(name);
    }

    @Override
    public HArk configArk() {
        return this.ark;
    }

    @Override
    public Database configDatabase() {
        final KDS<Database> kds = this.ark.database();
        return kds.dynamic();
    }

    @Override
    public Environment environment() {
        return this.environment;
    }
}
