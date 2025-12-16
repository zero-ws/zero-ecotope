package io.zerows.support.base;

import io.r2mo.function.Fn;
import io.zerows.component.fs.LocalDir;
import io.zerows.platform.constant.VValue;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Slf4j
final class IoDirectory {

    private IoDirectory() {
    }

    static List<String> listFiles(final String folder, final String extension) {
        return list(folder, extension, false);
    }

    static List<String> listDirectories(final String folder) {
        return list(folder, null, true);
    }

    static List<String> listFilesN(final String folder, final String extension, final String prefix) {
        final List<String> folders = LocalDir.of(folder).directories(folder);
        return folders.stream()
            .flatMap(single -> list(single, extension, false).stream()
                .filter(file -> TIs.isNil(prefix) || file.contains(prefix))
                .map(file -> {
                    if (single.endsWith("/")) {
                        return single + file;
                    } else {
                        return single + "/" + file;
                    }
                }))
            .filter(file -> {
                final File fileAbsolute = new File(file);
                if (fileAbsolute.exists() && fileAbsolute.isFile()) {
                    return true;
                }
                // 深度检查
                try {
                    final URL url = fileAbsolute.toURI().toURL();
                    final InputStream in = url.openStream();
                    return Objects.nonNull(in);
                } catch (final IOException e) {
                    final InputStream in = IoStream.readDirect(file);
                    return Objects.nonNull(in);
                }
            })
            .collect(Collectors.toList());
    }

    private static List<String> list(final String folder,
                                     final String extension,
                                     final boolean isDirectory) {
        /*
         * list folder by related file first
         * /folder here
         */
        final File folderObj = new File(folder);
        final Set<String> retSet = new TreeSet<>();
        if (folderObj.exists()) {
            /*
             * Related path here, it means that
             * such as /folder/extra/ etc.
             */
            retSet.addAll(getFiles(folderObj, extension, isDirectory));
        } else {
            URL url = Io.ioURL(folder);
            if (Objects.isNull(url)) {
                /*
                 * Fix jar path issue here.
                 */
                if (folder.contains(VValue.SUFFIX.JAR_DIVIDER)) {
                    url = Fn.jvmOr(() -> new URI(folder).toURL());
                }
            }
            /*
             * Split steps for url extraction
             */
            if (Objects.isNull(url)) {
                log.debug("The url of folder = `{}` is null", folder);
            } else {
                /*
                 * Whether it's jar path or common path.
                 */
                final String protocol = url.getProtocol();
                if (VValue.PROTOCOL.FILE.equals(protocol)) {
                    /*
                     * Common file
                     */
                    retSet.addAll(getFiles(url, extension, isDirectory));
                } else if (VValue.PROTOCOL.JAR.equals(protocol)) {
                    /*
                     * Jar File
                     */
                    retSet.addAll(getJars(url, extension, isDirectory));
                } else {
                    log.error("[ ZERO ] protocol error! protocol = {}, url = {}", protocol, url);
                }
            }
        }
        return new ArrayList<>(retSet);
    }

    static List<String> getJars(final URL url, final String extension, final boolean isDirectory) {
        return Fn.jvmOr(() -> {
            final JarURLConnection jarCon = (JarURLConnection) url.openConnection();
            final JarFile jarFile = jarCon.getJarFile();
            /*
             * JarEntry iterator
             */
            final String[] specifics = url.getPath().split("!/");
            final String folder = specifics[1];     // Jar Specification
            if (isDirectory) {
                /*
                 * Get folder entry
                 */
                return getJarDirectories(jarFile, folder);
            } else {
                /*
                 * Get file entry
                 */
                return getJarFiles(jarFile, folder, extension);
            }
        });
    }

    private static List<String> getJarDirectories(final JarFile jarFile, final String folder) {
        final List<String> retList = new ArrayList<>();
        final Enumeration<JarEntry> entities = jarFile.entries();
        while (entities.hasMoreElements()) {
            final JarEntry entry = entities.nextElement();
            /*
             * First, we must filter the folder
             */
            final String entryName = entry.getName();
            final String filtered = folder + "/";
            /*
             * Here must startsWith "filtered" to avoid the folder such as
             * - model
             * - model.hybird ( Should not be valid here )
             * The filtered must be correct path instead of folder name
             */
            if (entryName.startsWith(filtered) && entry.isDirectory()
                && !filtered.equals(entryName)  // This condition is for folder only
            ) {
                /*
                 * The condition is ok to pickup folder only
                 * Because folder is end with '/', it means that you must replace the last folder
                 */
                final String replaced = entryName.substring(0, entryName.lastIndexOf('/'));
                final String found = replaced.substring(replaced.lastIndexOf('/') + 1);
                if (!TIs.isNil(found)) {
                    retList.add(found);
                }
            }
        }
        return retList;
    }

    private static List<String> getJarFiles(final JarFile jarFile, final String folder, final String extension) {
        final List<String> retList = new ArrayList<>();
        final Enumeration<JarEntry> entities = jarFile.entries();
        while (entities.hasMoreElements()) {
            final JarEntry entry = entities.nextElement();
            /*
             * First, we must filter the folder
             */
            final String entryName = entry.getName();
            if (entryName.startsWith(folder) && !entry.isDirectory()) {
                if (Objects.isNull(extension)) {
                    /*
                     * No Extension
                     */
                    final String foundFile = entryName.substring(entryName.lastIndexOf('/') + 1);
                    if (!TIs.isNil(foundFile)) {
                        retList.add(foundFile);
                    }
                } else {
                    if (entryName.endsWith(extension)) {
                        /*
                         * Extension enabled
                         */
                        final String foundFile = entryName.substring(entryName.lastIndexOf('/') + 1);
                        if (!TIs.isNil(foundFile)) {
                            retList.add(foundFile);
                        }
                    }
                }
            }
        }
        return retList;
    }

    private static List<String> getFiles(final File directory, final String extension, final boolean isDirectory) {
        final List<String> retList = new ArrayList<>();

        // 1. 先优先检查是否存在
        if (!directory.exists()) {
            log.error("The path does not exist, file = `{}`", directory.getAbsolutePath());
            return retList;
        }

        // 2. 只有存在时，才判断是否为目录
        if (directory.isDirectory()) {
            final File[] files = (isDirectory) ?
                directory.listFiles(File::isDirectory) :
                (null == extension ?
                    directory.listFiles(File::isFile) :
                    directory.listFiles((item) -> item.isFile() && item.getName().endsWith(extension)));
            if (null != files) {
                retList.addAll(Arrays.stream(files)
                    .map(File::getName)
                    .toList()); // 注意：Java 8使用 collect, Java 16+ 可用 toList()
            }
        } else {
            // 3. 既存在，又不是目录（说明传入的是一个普通文件）
            // 在 IoDirectory 的语境下，列出文件的子文件没有意义，
            // 但这里不应该报错说"不存在"，可以选择 debug 记录一下或者直接忽略
            log.debug("The path is a file, not a directory, skip listing: `{}`", directory.getAbsolutePath());

            // 备选逻辑：如果你希望传入单个文件时，如果满足 extension 也把它自己返回，可以在这里处理
            /*
            if (!isDirectory && (extension == null || directory.getName().endsWith(extension))) {
                retList.add(directory.getName());
            }
            */
        }
        return retList;
    }

    private static List<String> getFiles(final URL url, final String extension, final boolean isDirectory) {
        final File directory = new File(url.getFile());
        return getFiles(directory, extension, isDirectory);
    }
}
