package io.zerows.support;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.metadata.program.KHugeFile;
import io.zerows.constant.VValue;
import io.zerows.enums.typed.CompressLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;

/*
 *
 */
final class IoOut {

    private IoOut() {
    }

    @SuppressWarnings("all")
    static void write(final String path, final String data) {
        Fn.jvmAt(() -> {
            final File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            final FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
        });
    }

    static void write(final String path, final JsonObject data) {
        final String target = null == data ? "{}" : data.encodePrettily();
        write(path, target);
    }

    static void write(final String path, final JsonArray data) {
        final String target = null == data ? "[]" : data.encodePrettily();
        write(path, target);
    }

    static boolean make(final String path) {
        final File file = new File(path);
        boolean created = false;
        if (!file.exists()) {
            created = file.mkdirs();
        }
        return created;
    }

    static void writeCompress(final String path, final JsonArray data) {
        final String target = null == data ? "[]" : data.encode();
        writeCompress(path, target);
    }

    static void writeCompress(final String path, final JsonObject data) {
        final String target = null == data ? "{}" : data.encode();
        writeCompress(path, target);
    }

    static void writeCompress(final String path, final String data) {
        final byte[] dataBytes = data.getBytes(VValue.DFT.CHARSET);
        final byte[] output = IoZip.compress(dataBytes, CompressLevel.BEST_COMPRESSION);
        Fn.jvmAt(() -> {
            final FileOutputStream fos = new FileOutputStream(path);
            fos.write(output);
            fos.close();
        });
    }

    /*
     * OutputStream
     */
    static void writeBig(final String filename, final OutputStream output) {
        final KHugeFile file = new KHugeFile(filename);
        Fn.jvmAt(() -> {
            while (file.read() != VValue.RANGE) {
                final byte[] bytes = file.getCurrentBytes();
                output.write(bytes);
                output.flush();
            }
            output.close();
        });
    }
}
