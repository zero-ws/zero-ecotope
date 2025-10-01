package io.zerows.epoch.common.uca.fs;

import io.r2mo.function.Fn;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.support.UtBase;

import java.io.File;
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

/**
 * @author lang : 2024-06-17
 */
public class LocalDirEmbed extends AbstractLocalDir {
    LocalDirEmbed(final String root) {
        super(root);
    }

    @Override
    public List<String> directories(final String directory) {
        final String root = this.rootAdjust(directory);
        return this.listDirectoriesN(directory, root);
    }

    private List<String> listDirectoriesN(final String folder, final String root) {
        final List<String> folders = new ArrayList<>();
        // root + folder
        final File folderObj = this.ioFileAbsolute(folder, root);
        if (Objects.isNull(folderObj)) {
            return folders;
        }

        folders.add(folder);
        // 此处会检索是否 jar 文件内部
        final boolean isJar = folderObj.getPath().contains("jar!");
        if (isJar) {
            // Else
            final String jarFilename = this.jarExtractFileName(folderObj.getPath());
            folders.addAll(this.jarListDirectoriesN(jarFilename, folder));
            this.logger().debug("Jar Directories found: size = {0}", String.valueOf(folders.size()));
        } else {
            if (folderObj.isDirectory()) {
                // Else
                final String[] folderList = folderObj.list();
                assert folderList != null;
                final String relatedPath = folderObj.getAbsolutePath().replace("\\", "/");
                Arrays.stream(folderList).forEach(folderS -> {
                    final String rootCopy = root.replace("\\", "/");
                    final String pathReplaced = relatedPath.replace(rootCopy, VString.EMPTY);
                    folders.addAll(this.listDirectoriesN(pathReplaced + "/" + folderS, root));
                });
                this.logger().debug("Norm Directories found: size = {0}", String.valueOf(folders.size()));
            }
        }
        return folders;
    }

    private String jarExtractFileName(final String path) {
        // Check if the path contains '!'
        final int index = path.indexOf("!");
        if (index != -1) {
            // Extract the part of the path before '!'
            String jarPart = path.substring(0, index);
            // Remove the "file:" prefix if present
            if (jarPart.startsWith("file:")) {
                jarPart = jarPart.substring(5);
            }
            // Return the JAR file name
            return jarPart;
        }
        // Return null or throw an exception if the path formatFail is invalid
        throw new IllegalArgumentException("Invalid path formatFail: " + path);
    }

    private Set<String> jarListDirectoriesN(final String jarPath, final String input) {
        final Set<String> directories = new TreeSet<>();

        final String internalPath = !input.endsWith("/") ? input + "/" : input;

        Fn.jvmAt(() -> {
            try (final JarFile jarFile = new JarFile(jarPath)) {
                final Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    final JarEntry entry = entries.nextElement();
                    final String entryName = entry.getName();

                    // Check if the entry starts with the internalPath and is deeper than the internalPath
                    if (entryName.startsWith(internalPath) && entryName.length() > internalPath.length()) {
                        // Get the relative path from the internal path
                        final String relativePath = entryName.substring(internalPath.length());
                        // Add directory paths to the set
                        if (entry.isDirectory()) {
                            directories.add(entryName);
                        } else if (relativePath.contains("/")) {
                            // This entry is a file in a subdirectory, add the subdirectory path
                            final String subdirPath = internalPath + relativePath.substring(0, relativePath.lastIndexOf('/') + 1);
                            directories.add(subdirPath);
                        }
                    }
                }
            }
        });
        return directories;
    }

    private String rootAdjust(final String directory) {
        final URL rootUrl = UtBase.ioURL(directory);
        if (Objects.isNull(rootUrl)) {
            return VString.EMPTY;
        } else {
            final String rootPath = rootUrl.getFile();
            return rootPath.replace(directory, VString.EMPTY);
        }
    }
}
