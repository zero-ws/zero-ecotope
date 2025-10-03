package io.zerows.epoch.program;

import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.constant.VPath;
import io.zerows.constant.VString;
import io.zerows.support.UtBase;
import org.osgi.framework.Bundle;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author lang : 2024-04-17
 */
final class BundleIo {
    // key = filename + bundleId
    private static final Cc<String, JsonObject> CCD_FILE_DEFAULT = Cc.open();
    // key = filename
    private static final Cc<String, JsonObject> CCD_FILE_STORE = Cc.open();

    static JsonObject ioDefault(final String filename, final Bundle bundle) {
        final String filepath = "bundle/" + filename;
        final String cachedKey = Objects.isNull(bundle) ?
            filepath : filepath + VString.SLASH + bundle.getBundleId();
        final JsonObject storeDefault = CCD_FILE_DEFAULT.pick(() -> ioSmart(filepath, bundle), cachedKey);
        return UtBase.valueJObject(storeDefault);
    }

    static JsonObject ioCombine(final String filename, final Bundle bundle) {
        final JsonObject defaultJ = ioDefault(filename, bundle);
        final JsonObject configureJ = ioSmart(filename, bundle);
        final JsonObject resultJ = new JsonObject();
        // default <-- configured
        resultJ.mergeIn(defaultJ, true);
        if (Ut.isNotNil(configureJ)) {
            resultJ.mergeIn(configureJ, true);
        }
        return resultJ;
    }

    static JsonObject ioPriority(final String filename, final Bundle bundle) {
        // 提供配置
        final JsonObject configureJ = ioConfigure(filename, bundle);
        if (UtBase.isNotNil(configureJ)) {
            return configureJ;
        }

        // 默认配置
        final JsonObject defaultJ = ioDefault(filename, bundle);
        return UtBase.valueJObject(defaultJ);
    }

    static JsonObject ioConfigure(final String filename, final Bundle bundle) {
        JsonObject storedConfigured = CCD_FILE_STORE.pick(() -> ioSmart(filename, bundle), filename);
        if (Ut.isNil(storedConfigured)) {
            // 解决 classpath 未指定成功的情况
            final String fileConf = "conf/" + filename;
            if (Ut.ioExist(fileConf)) {
                storedConfigured = CCD_FILE_STORE.pick(() -> ioSmart(fileConf, bundle), fileConf);
            }
        }
        return Ut.valueJObject(storedConfigured);
    }

    private static JsonObject ioSmart(final String filename, final Bundle bundle) {
        if (UtBase.isNil(filename)) {
            return new JsonObject();
        }
        return filename.endsWith(VString.DOT + VPath.SUFFIX.JSON)
            ? ioJObject(filename, bundle) : ioYamlJ(filename, bundle);
    }

    static JsonObject ioJObject(final String filename, final Bundle bundle) {
        return ioSafe(filename, bundle, JsonObject::new, UtBase::ioJObject);
    }

    static JsonObject ioYamlJ(final String filename, final Bundle bundle) {
        return ioSafe(filename, bundle, JsonObject::new, UtBase::ioYaml);
    }

    static JsonArray ioYamlA(final String filename, final Bundle bundle) {
        return ioSafe(filename, bundle, JsonArray::new, UtBase::ioYaml);
    }

    static JsonArray ioJArray(final String filename, final Bundle bundle) {
        return ioSafe(filename, bundle, JsonArray::new, UtBase::ioJArray);
    }

    static URL ioURL(final String filename, final Bundle bundle) {
        URL url = null;
        // 直接构造文件（当前目录）
        File file = new File(filename);
        if (!file.exists()) {
            file = new File("conf/" + filename);
        }
        if (file.exists()) {
            try {
                url = file.toURI().toURL();
            } catch (final MalformedURLException ignored) {
            }
        }

        // 区分 OSGI 和非 OSGI 环境
        if (Objects.isNull(bundle)) {
            if (Objects.isNull(url)) {
                // 深度检索
                url = Thread.currentThread().getContextClassLoader().getResource(filename);
                if (Objects.isNull(url)) {
                    url = BundleIo.class.getResource(filename);
                }
            }
        } else {
            url = bundle.getResource(filename);
        }
        return url;
    }

    static List<String> ioDirectory(final String directory, final Bundle bundle, final boolean recursive) {
        // 获取 Bundle 中制定目录下的所有子目录（不包括文件）
        return ioEntries(directory, bundle, recursive, subDirectory -> subDirectory.endsWith("/"));
    }

    static List<String> ioFile(final String directory, final Bundle bundle,
                               final boolean recursive, final String extension) {
        // 获取 Bundle 中制定目录下的所有子文件夹（不包括目录）
        final List<String> directories = ioDirectory(directory, bundle, recursive);
        // 由于是读取文件，所以追加当前文件夹
        directories.add(directory);

        final Set<String> treeFiles = new TreeSet<>();
        directories.forEach(subDirectory -> {
            // 此处不可以使用递归，递归会导致空指针错
            final List<String> files = ioEntries(subDirectory, bundle, false, subFile -> {
                if (subFile.endsWith("/")) {
                    return false;
                }
                if (Ut.isNil(extension)) {
                    return true;
                } else {
                    return subFile.endsWith(VString.DOT + extension);
                }
            });
            treeFiles.addAll(files);
        });
        return new ArrayList<>(treeFiles);
    }

    private static <T> T ioSafe(final String filename, final Bundle bundle,
                                final Supplier<T> supplier,
                                final Function<InputStream, T> executor) {
        final URL finalUrl = ioURL(filename, bundle);
        if (Objects.isNull(finalUrl)) {
            return supplier.get();
        }
        return executor.apply(Fn.jvmOr(finalUrl::openStream));
    }

    private static List<String> ioEntries(final String directory, final Bundle bundle,
                                          final boolean recursive, final Predicate<String> filterFn) {
        final Enumeration<String> directories = bundle.getEntryPaths(directory);
        if (Objects.isNull(directories)) {
            // 预防空指针错
            return new ArrayList<>();
        }

        final List<String> urls = new ArrayList<>();
        while (directories.hasMoreElements()) {
            final String subDirectory = directories.nextElement();
            if (filterFn.test(subDirectory)) {
                urls.add(subDirectory);
                // 子目录递归处理
                if (recursive) {
                    urls.addAll(ioDirectory(subDirectory, bundle, true));
                }
            }
        }
        return urls;
    }
}
