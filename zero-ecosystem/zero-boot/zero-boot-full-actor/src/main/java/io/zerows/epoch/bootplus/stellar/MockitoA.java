package io.zerows.epoch.bootplus.stellar;

import io.r2mo.base.dbe.Database;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VClassPath;
import io.zerows.platform.enums.Environment;
import io.zerows.support.Ut;

/**
 * @author lang : 2023-06-13
 */
public class MockitoA extends AbstractPartyA {
    public MockitoA() {
        super(Environment.Mockito);
    }

    /**
     * Mock环境中的数据库信息可能来自文件部分
     *
     * @return 数据库配置信息
     */
    @Override
    public Database configDatabase() {
        final String path = VClassPath.runtime.environment.ofDatabase(this.environment());
        final JsonObject item = Ut.ioJObject(path);
        final JObject databaseJ = SPI.J(item);
        return Database.createDatabase(databaseJ);
    }

    /**
     * Mock环境中的集成相关信息可能来自文件
     */
    @Override
    public OkB partyB(final String name) {
        final OkB okB = super.partyB(name);
        final String path = VClassPath.runtime.environment.ofIntegration(this.environment());
        final JsonObject item = Ut.ioJObject(path + name + ".json");
        if (Ut.isNotNil(item)) {
            okB.configIntegration().fromJson(item);
            okB.configIntegration().mockOn();
        }
        return okB;
    }
}
