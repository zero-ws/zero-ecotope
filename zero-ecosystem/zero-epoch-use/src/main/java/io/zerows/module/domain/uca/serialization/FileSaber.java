package io.zerows.module.domain.uca.serialization;

import io.r2mo.function.Fn;
import io.zerows.epoch.runtime.exception._60021Exception400FilePathMissing;

import java.io.File;

class FileSaber extends AbstractSaber {

    @Override
    public Object from(final Class<?> paramType,
                       final String filename) {
        final File file = new File(filename);
        // Throw 400 Error
        Fn.jvmKo(!file.exists() || !file.canRead(), _60021Exception400FilePathMissing.class, filename);
        return file;
    }
}
