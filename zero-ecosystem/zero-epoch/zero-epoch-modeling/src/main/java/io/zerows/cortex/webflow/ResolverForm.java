package io.zerows.cortex.webflow;

import io.vertx.core.file.FileSystem;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebEpsilon;

import java.util.HashSet;
import java.util.Set;

public class ResolverForm<T> implements Resolver<T> {

    @Override
    public WebEpsilon<T> resolve(final RoutingContext context,
                                 final WebEpsilon<T> income) {
        final Set<FileUpload> fileUploads = new HashSet<>(context.fileUploads());
        /*
         * Not needed to group `Set<FileUpload>`
         */
        final Class<?> argType = income.getArgType();
        final FileSystem fileSystem = context.vertx().fileSystem();
        final T result = ResolverUtil.toFile(fileUploads, argType, fileSystem::readFileBlocking);
        income.setValue(result);
        return income;
    }
}
