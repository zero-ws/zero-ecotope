package io.zerows.module.domain.uca.serialization;

import io.zerows.core.fn.Fx;
import io.zerows.module.domain.exception._400FilePathMissingException;

import java.io.File;

class FileSaber extends AbstractSaber {

    @Override
    public Object from(final Class<?> paramType,
                       final String filename) {
        final File file = new File(filename);
        // Throw 400 Error
        Fx.outWeb(!file.exists() || !file.canRead(), this.logger(),
            _400FilePathMissingException.class, this.getClass(), filename);
        return file;
    }
}
