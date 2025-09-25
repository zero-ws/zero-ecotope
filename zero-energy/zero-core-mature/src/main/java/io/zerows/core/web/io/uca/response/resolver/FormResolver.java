package io.zerows.core.web.io.uca.response.resolver;

import io.vertx.core.file.FileSystem;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.web.io.util.UtilUpload;
import io.zerows.core.web.io.zdk.mime.Resolver;
import io.zerows.core.web.model.atom.Epsilon;

import java.util.HashSet;
import java.util.Set;

public class FormResolver<T> implements Resolver<T> {

    @Override
    public Epsilon<T> resolve(final RoutingContext context,
                              final Epsilon<T> income) {
        final Set<FileUpload> fileUploads = new HashSet<>(context.fileUploads());
        /*
         * Not needed to group `Set<FileUpload>`
         */
        final Class<?> argType = income.getArgType();
        final FileSystem fileSystem = context.vertx().fileSystem();
        final T result = UtilUpload.toFile(fileUploads, argType, fileSystem::readFileBlocking);
        income.setValue(result);
        return income;
    }
}
