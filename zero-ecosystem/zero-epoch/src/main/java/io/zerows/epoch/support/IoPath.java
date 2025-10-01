package io.zerows.epoch.support;

import io.zerows.epoch.constant.VString;
import io.zerows.epoch.enums.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2023/4/28
 */
class IoPath {

    static String resolve(final String path, final Environment environment) {
        if (TIs.isNil(path) || path.startsWith(VString.SLASH)) {
            return path;
        }
        if (Environment.Production == environment) {
            return path;
        } else {
            // TODO: 追加环境变量，默认用了 Maven 的
            return resolve("src/main/resources", path);
        }
    }

    static String resolve(final String folder, final String file) {
        Objects.requireNonNull(file);
        final String valueFolder;
        if (TIs.isNil(folder)) {
            valueFolder = "/";
        } else {
            if (folder.endsWith("/")) {
                // Fix issue of deployment on production environment data loading
                valueFolder = folder.substring(0, folder.lastIndexOf("/"));
            } else {
                valueFolder = folder;
            }
        }
        final String valueFile;
        if (file.startsWith("/")) {
            valueFile = file;
        } else {
            valueFile = "/" + file;
        }
        // Convert `//` to `/`
        return (valueFolder + valueFile).replace("//", "/");
    }


    static List<String> ladder(final String path) {
        if (TIs.isNil(path)) {
            return new ArrayList<>();
        }
        final String[] splitArr = path.split(VString.SLASH);
        final List<String> itemList = new ArrayList<>();
        for (int idx = 0; idx < splitArr.length; idx++) {
            final StringBuilder item = new StringBuilder();
            for (int jdx = 0; jdx < idx; jdx++) {
                item.append(splitArr[jdx]).append(VString.SLASH);
            }
            item.append(splitArr[idx]);
            final String itemStr = item.toString();
            if (!TIs.isNil(itemStr)) {
                itemList.add(itemStr);
            }
        }
        return itemList;
    }

    static String first(final String path, final String separator) {
        final String[] names = path.split(separator);
        String result = null;
        for (final String found : names) {
            if (UtBase.isNotNil(found)) {
                result = found;
                break;
            }
        }
        return result;
    }

    @SuppressWarnings("all")
    static String last(final String path, final String separator) {
        final String[] names = path.split(separator);
        String result = null;
        for (int idx = names.length - 1; idx < names.length; idx--) {
            final String found = names[idx];
            if (UtBase.isNotNil(found)) {
                result = found;
                break;
            }
        }
        return result;
    }
}
