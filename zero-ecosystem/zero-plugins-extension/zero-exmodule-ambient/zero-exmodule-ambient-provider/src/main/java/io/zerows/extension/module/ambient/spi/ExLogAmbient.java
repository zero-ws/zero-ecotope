package io.zerows.extension.module.ambient.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.ambient.component.LogWriterXLog;
import io.zerows.extension.skeleton.spi.ExLog;
import io.zerows.extension.skeleton.spi.LogWriter;
import io.zerows.extension.skeleton.spi.LogType;
import io.zerows.spi.HPI;

import java.util.Objects;

public class ExLogAmbient implements ExLog {
    private final transient LogWriter writer = this.writer();

    @Override
    public Future<JsonObject> record(final LogType type, final JsonObject data) {
        return this.writer.write(type, data);
    }

    private LogWriter writer() {
        final LogWriter configured = HPI.findOverwrite(LogWriter.class);
        return Objects.isNull(configured) ? new LogWriterXLog() : configured;
    }
}
