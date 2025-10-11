package io.zerows.cortex.webflow;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.FileUpload;
import io.zerows.component.log.LogOf;
import io.zerows.weaver.ZeroType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 内部专用，只在 Request 请求处理模块中使用的工具类
 *
 * @author lang : 2024-04-21
 */
@SuppressWarnings("all")
class ResolverUtil {
    private static final LogOf LOGGER = LogOf.get(ResolverUtil.class);

    public static <T> T toFile(final Set<FileUpload> fileUploads, final Class<?> expected, final Function<String, Buffer> consumer) {
        /*
         * Size = 0
         * Size = 1
         * Size > 1
         */
        if (Objects.isNull(fileUploads) || fileUploads.isEmpty()) {
            /*
             * Size = 0 or null
             */
            LOGGER.warn("The fileUploads set size is 0.");
            if (Collection.class.isAssignableFrom(expected)) {
                if (List.class.isAssignableFrom(expected)) {
                    /*
                     * List<Tool>
                     */
                    return (T) Collections.emptyList();
                } else if (Set.class.isAssignableFrom(expected)) {
                    /*
                     * Set<Tool>
                     */
                    return (T) Collections.emptySet();
                } else {
                    LOGGER.warn("The type {0} is not supported.", expected.getName());
                    return null;
                }
            } else {
                return null;
            }
        } else {
            /*
             * Collection checking
             * 1. List<Tool>
             * 2. Set<Tool>
             */
            if (Collection.class.isAssignableFrom(expected)) {
                /*
                 * For collection, because of generic type declare and Java Reflection limitation
                 * It could support `FileUpload` type only, it is the source of file upload and
                 * it could be used in different income requirement.
                 *
                 * It means that list only support Set<FileUpload> and List<FileUpload>
                 */
                final Stream stream = fileUploads.stream()
                    .map(fileUpload -> toFile(fileUpload, FileUpload.class, consumer));
                if (List.class.isAssignableFrom(expected)) {
                    /*
                     * List<Tool>
                     */
                    return (T) stream.collect(Collectors.toList());
                } else if (Set.class.isAssignableFrom(expected)) {
                    /*
                     * Set<Tool>
                     */
                    return (T) stream.collect(Collectors.toSet());
                } else {
                    LOGGER.warn("The type {0} is not supported.", expected.getName());
                    return null;
                }
            } else {
                /*
                 * Size > 1 ( Because declared type is not collection )
                 */
                if (!isByteArray(expected) && expected.isArray()) {
                    /*
                     * expected != byte[]/Byte[]
                     */
                    if (expected.isArray()) {
                        /*
                         * expected == Tool[]
                         */
                        final Class<?> componentCls = expected.getComponentType();
                        final List<FileUpload> fileList = new ArrayList<>(fileUploads);

                        if (File.class == componentCls) {
                            /*
                             * File[]
                             */
                            final File[] files = new File[fileList.size()];
                            for (int idx = 0; idx < fileList.size(); idx++) {
                                files[idx] = toFile(fileList.get(idx), File.class, consumer);
                            }
                            return (T) files;
                        } else if (FileUpload.class.isAssignableFrom(componentCls)) {
                            /*
                             * FileUpload[]
                             */
                            final FileUpload[] files = new FileUpload[fileList.size()];
                            for (int idx = 0; idx < fileList.size(); idx++) {
                                files[idx] = fileList.get(idx);
                            }
                            return (T) files;
                        }
                        return (T) fileUploads.stream()
                            .map(item -> toFile(item, componentCls, consumer))
                            .toArray();
                    } else {
                        /*
                         * expected = Single
                         */
                        return toFile(fileUploads, expected, consumer);
                    }
                } else {
                    /*
                     * byte[] or single type
                     */
                    final FileUpload fileUpload = fileUploads.iterator().next();
                    return toFile(fileUpload, expected, consumer);
                }
            }
        }
    }

    /**
     * Split `Set<FileUpload>` by fieldname
     *
     * @param fileUploads FileUpload Set
     *
     * @return Map of `field = Set<FileUpload>`
     */
    public static ConcurrentMap<String, Set<FileUpload>> toFile(final Set<FileUpload> fileUploads) {
        final ConcurrentMap<String, Set<FileUpload>> fileMap = new ConcurrentHashMap<>();
        fileUploads.stream().forEach(fileUpload -> {
            final String field = fileUpload.name();
            /* Process */
            if (!fileMap.containsKey(field)) {
                final Set<FileUpload> set = new HashSet<>();
                fileMap.put(field, set);
            }
            final Set<FileUpload> set = fileMap.get(field);
            set.add(fileUpload);
        });
        return fileMap;
    }

    private static boolean isByteArray(final Class<?> expected) {
        if (expected.isArray()) {
            final Class<?> componentCls = expected.getComponentType();
            return (byte.class == componentCls || Byte.class == componentCls);
        } else return false;
    }

    public static <T> T toFile(final FileUpload fileUpload, final Class<?> expected, final Function<String, Buffer> consumer) {
        final String filename = fileUpload.uploadedFileName();
        if (FileUpload.class.isAssignableFrom(expected)) {
            /*
             * FileUpload ( interface )
             */
            return (T) fileUpload;
        } else if (File.class == expected) {
            /*
             * File ( class )
             */
            return (T) ZeroType.value(expected, filename);
        } else if (expected.isArray()) {
            /*
             * Tool[]
             */
            final Class<?> componentCls = expected.getComponentType();
            if (isByteArray(expected)) {
                final Buffer buffer = consumer.apply(filename);
                final byte[] bytes = buffer.getBytes();
                if (byte.class == componentCls) {
                    /*
                     * byte[]
                     */
                    return (T) bytes;
                } else {
                    /*
                     * Byte[]
                     */
                    final Byte[] byteWrapper = new Byte[bytes.length];
                    for (int idx = 0; idx < bytes.length; idx++) {
                        byteWrapper[idx] = bytes[idx];
                    }
                    return (T) byteWrapper;
                }
            } else {
                LOGGER.warn("The array type support byte[]/Byte[] only in current version, current = {0}",
                    componentCls.getName());
                return null;
            }
        } else if (Buffer.class.isAssignableFrom(expected)) {
            /*
             * Buffer
             */
            return (T) consumer.apply(filename);
        } else {
            LOGGER.warn("The expected type {0} is not supported.", expected.getName());
            return null;
        }
    }
}
