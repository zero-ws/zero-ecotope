package io.zerows.epoch.boot;

import io.r2mo.base.io.HStore;
import io.r2mo.spi.SPI;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.configuration.ConfigMod;

import java.io.InputStream;
import java.util.List;

/**
 * @author lang : 2025-12-15
 */
@SPID
public class ConfigModLocal implements ConfigMod {
    private static final HStore STORE = SPI.V_STORE;
    private static final JUtil UT = SPI.V_UTIL;

    @Override
    public JsonObject inYamlJ(final String filename) {
        final JObject contentJ = STORE.inYaml(filename);
        if (UT.isEmpty(contentJ)) {
            return new JsonObject();
        }
        return contentJ.data();
    }

    @Override
    public JsonArray inYamlA(final String filename) {
        final JArray contentA = STORE.inYaml(filename);
        if (UT.isEmpty(contentA)) {
            return new JsonArray();
        }
        return contentA.data();
    }


    @Override
    public JsonObject inJObject(final String filename) {
        final JObject contentJ = STORE.inJson(filename);
        if (UT.isEmpty(contentJ)) {
            return new JsonObject();
        }
        return contentJ.data();
    }

    @Override
    public JsonArray inJArray(final String filename) {
        final JArray contentA = STORE.inJson(filename);
        if (UT.isEmpty(contentA)) {
            return new JsonArray();
        }
        return contentA.data();
    }


    @Override
    public InputStream inStream(final String filename) {
        return STORE.inStream(filename);
    }

    @Override
    public List<String> ioDirectories(final String directory) {
        return STORE.lsDirsN(directory);
    }

    @Override
    public List<String> ioFiles(final String directory, final String suffix) {
        return STORE.lsFilesN(directory, suffix);
    }

    @Override
    public boolean ioExist(final String path) {
        return STORE.isExist(path);
    }
}
