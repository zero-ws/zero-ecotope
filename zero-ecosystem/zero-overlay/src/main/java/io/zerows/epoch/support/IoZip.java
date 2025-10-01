package io.zerows.epoch.support;

import io.r2mo.function.Fn;
import io.vertx.core.buffer.Buffer;
import io.zerows.epoch.annotations.monitor.ChatGPT;
import io.zerows.epoch.constant.VValue;
import io.zerows.epoch.enums.typed.CompressLevel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Set;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class IoZip {

    static Buffer ioZip(final Set<String> fileSet) {
        // Create Tpl zip file path
        return Fn.jvmOr(() -> {
            final ByteArrayOutputStream fos = new ByteArrayOutputStream();
            try (final ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))) {
                final byte[] buffers = new byte[VValue.DFT.SIZE_BYTE_ARRAY];
                fileSet.forEach(filename -> ioZip(zos, buffers, filename));
            }
            return Buffer.buffer(fos.toByteArray());
        });
    }

    @ChatGPT
    private static void ioZip(final ZipOutputStream zos, final byte[] buffers, final String filename) {
        Fn.jvmAt(() -> {
            final File file = new File(filename);
            final ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);
            try (final FileInputStream fis = new FileInputStream(file);
                 final BufferedInputStream bis = new BufferedInputStream(fis, VValue.DFT.SIZE_BYTE_ARRAY)) {
                int read;
                while ((read = bis.read(buffers)) != -1) {
                    zos.write(buffers, 0, read);
                }
            }
        });
    }

    static String ioCompress(final String file) {
        final byte[] bytes = IoStream.readBytes(file);
        final byte[] compressed = decompress(bytes);
        return new String(compressed, VValue.DFT.CHARSET);
    }

    static byte[] compress(final byte[] data, final CompressLevel level) {
        return Fn.jvmOr(() -> {
            final Deflater deflater = new Deflater();
            deflater.setLevel(level.getLevel());
            deflater.setInput(data);

            try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
                deflater.finish();
                final byte[] buffer = new byte[VValue.DFT.SIZE_BYTE_ARRAY];
                while (!deflater.finished()) {
                    final int count = deflater.deflate(buffer);
                    outputStream.write(buffer, 0, count);
                }
                return outputStream.toByteArray();
            }
        });
    }

    private static byte[] decompress(final byte[] data) {
        return Fn.jvmOr(() -> {
            final Inflater inflater = new Inflater();
            inflater.setInput(data);

            try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
                data.length)) {
                final byte[] buffer = new byte[VValue.DFT.SIZE_BYTE_ARRAY];
                while (!inflater.finished()) {
                    final int count = inflater.inflate(buffer);
                    outputStream.write(buffer, 0, count);
                }
                return outputStream.toByteArray();
            }
        });
    }
}
