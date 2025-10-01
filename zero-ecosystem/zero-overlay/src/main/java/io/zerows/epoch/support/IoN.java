package io.zerows.epoch.support;

import io.r2mo.function.Fn;
import io.zerows.epoch.annotations.monitor.ChatGPT;
import io.zerows.epoch.common.log.LogUtil;
import io.zerows.epoch.constant.VValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;

/**
 * 高性能底层：NIO模式
 *
 * @author lang : 2023-05-26
 */
@ChatGPT
class IoN {
    private static final LogUtil LOG = LogUtil.from(IoN.class);

    @SuppressWarnings("all")
    static InputStream stream(final File file) {
        Objects.requireNonNull(file);
        final String parameters = file.getAbsolutePath();
        LOG.io(INFO.IoStream.__FILE_INPUT_STREAM, parameters);
        return Fn.jvmOr(() -> {
            // NIO切换IO
            final FileInputStream in = new FileInputStream(parameters);
            final ReadableByteChannel channel = in.getChannel();
            return Channels.newInputStream(channel);
        });
    }

    static byte[] bytes(final InputStream in) throws IOException {
        final ReadableByteChannel channel = Channels.newChannel(in);
        final ByteArrayOutputStream out = new ByteArrayOutputStream(VValue.DFT.SIZE_BYTE_ARRAY);
        // 使用合适的缓冲区大小
        final ByteBuffer buffer = ByteBuffer.allocate(VValue.DFT.SIZE_BYTE_ARRAY);
        int bytesRead;
        while ((bytesRead = channel.read(buffer)) != -1) {
            buffer.flip();
            out.write(buffer.array(), buffer.arrayOffset() + buffer.position(), bytesRead);
            buffer.clear();
        }
        return out.toByteArray();
    }
}
