package io.zerows.epoch.component.serialization;

import io.r2mo.function.Fn;
import io.zerows.epoch.based.exception._60021Exception400FilePathMissing;

import java.io.File;

class SaberFile extends SaberBase {

    @Override
    public Object from(final Class<?> paramType,
                       final String filename) {
        final File file = new File(filename);
        // Throw 400 Error
        Fn.jvmKo(!file.exists() || !file.canRead(), _60021Exception400FilePathMissing.class, filename);
        return file;
    }
}
