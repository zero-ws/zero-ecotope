package io.zerows.cortex.webflow;

import io.vertx.core.file.FileSystem;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import io.zerows.weaver.ZeroType;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 「Co」JSR311 for .@FormParam
 *
 * This `Filler` is for form attributes extracting
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class FillerForm implements Filler {

    @Override
    public Object apply(final String name,
                        final Class<?> paramType,
                        final RoutingContext context) {
        /*
         * Set<FileUploads>
         */
        final Set<FileUpload> uploadSet = new HashSet<>(context.fileUploads());
        if (uploadSet.isEmpty()) {
            /*
             * Not file parameters ( Without uploading )
             */
            final String value = context.request().getFormAttribute(name);
            return ZeroType.value(paramType, value);
        } else {
            final ConcurrentMap<String, Set<FileUpload>> compressed
                = ResolverUtil.toFile(uploadSet);
            if (compressed.containsKey(name)) {
                /*
                 * With uploading multi here
                 */
                final Set<FileUpload> uploadParam = compressed.get(name);
                final FileSystem fileSystem = context.vertx().fileSystem();
                return ResolverUtil.toFile(uploadParam, paramType, fileSystem::readFileBlocking);
            } else {
                /*
                 * Not file parameters ( With uploading )
                 */
                final String value = context.request().getFormAttribute(name);
                return ZeroType.value(paramType, value);
            }
        }
    }
}
