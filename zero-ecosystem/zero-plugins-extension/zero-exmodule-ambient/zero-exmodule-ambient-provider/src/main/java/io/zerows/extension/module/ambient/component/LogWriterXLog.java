package io.zerows.extension.module.ambient.component;

import cn.hutool.core.util.StrUtil;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.domain.tables.daos.XLogDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XLog;
import io.zerows.extension.skeleton.spi.LogWriter;
import io.zerows.extension.skeleton.spi.LogType;
import io.zerows.program.Ux;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class LogWriterXLog implements LogWriter {
    private static final String LEVEL_INFO = "INFO";

    @Override
    public Future<JsonObject> write(final LogType type, final JsonObject data) {
        final JsonObject normalized = Objects.isNull(data) ? new JsonObject() : data.copy();
        final LocalDateTime now = LocalDateTime.now();
        final XLog log = new XLog()
            .setId(UUID.randomUUID().toString())
            .setType(Objects.isNull(type) ? LogType.SYSTEM.name() : type.name())
            .setLevel(normalized.getString("level", LEVEL_INFO))
            .setInfoAt(now)
            .setInfoReadable(normalized.getString("infoReadable"))
            .setInfoSystem(normalized.getString("infoSystem"))
            .setInfoStack(normalized.getString("infoStack"))
            .setLogAgent(normalized.getString("logAgent"))
            .setLogIp(normalized.getString("logIp"))
            .setLogUser(normalized.getString("logUser"))
            .setSigma(normalized.getString(KName.SIGMA))
            .setTenantId(normalized.getString(KName.Tenant.ID))
            .setAppId(normalized.getString(KName.APP_ID))
            .setLanguage(normalized.getString(KName.LANGUAGE))
            .setActive(normalized.getBoolean(KName.ACTIVE, Boolean.TRUE))
            .setMetadata(normalized.getJsonObject(KName.METADATA))
            .setCreatedAt(now)
            .setCreatedBy(normalized.getString(KName.CREATED_BY))
            .setUpdatedAt(now)
            .setUpdatedBy(normalized.getString(KName.UPDATED_BY));
        if (StrUtil.isBlank(log.getInfoReadable())) {
            log.setInfoReadable(log.getInfoSystem());
        }
        return DB.on(XLogDao.class).insertAsync(log).compose(Ux::futureJ);
    }
}
