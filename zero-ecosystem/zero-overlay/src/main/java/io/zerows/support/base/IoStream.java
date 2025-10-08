package io.zerows.support.base;

import io.r2mo.function.Fn;
import io.vertx.core.buffer.Buffer;
import io.zerows.component.fs.LocalDir;
import io.zerows.component.log.LogUtil;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.exception._11002Exception500EmptyIo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Stream read class.
 */
final class IoStream {
    /**
     * 「DEAD-LOCK」LoggerFactory.getLogger
     * Do not use `Annal` logger because of deadlock.
     */
    //    private static final Logger LOGGER = LoggerFactory.getLogger(IoStream.class);
    private static final LogUtil LOG = LogUtil.from(IoStream.class);

    private IoStream() {
    }

    static Buffer ioBuffer(final String filename) {
        final InputStream in = read(filename);
        return ioBuffer(in);
    }

    static Buffer ioBuffer(final URL url) {
        if (Objects.isNull(url)) {
            return Buffer.buffer();
        }
        return Fn.jvmAs(
            url::openStream, IoStream::ioBuffer,
            () -> new _11002Exception500EmptyIo("URL/Buffer: " + url.getPath())
        );
    }

    @SuppressWarnings("all")
    static Buffer ioBuffer(final InputStream in) {
        return Fn.jvmOr(() -> {
            final byte[] bytes = new byte[in.available()];
            in.read(bytes);
            in.close();
            return Buffer.buffer(bytes);
        });
    }

    /**
     * Codec usage
     *
     * @param message The java object that will be converted from.
     * @param <T>     Target java object that will be converted to.
     *
     * @return Target java object ( Generic Type )
     */
    static <T> byte[] to(final T message) {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        return Fn.jvmOr(() -> {
            final ObjectOutputStream out = new ObjectOutputStream(bytes);
            out.writeObject(message);
            out.close();
            return bytes.toByteArray();
        });
    }

    /**
     * Codec usage
     *
     * @param pos    The position of reading
     * @param buffer The buffer to hold the data from reading.
     * @param <T>    The converted java object type, Generic Type
     *
     * @return Return to converted java object.
     */
    @SuppressWarnings("unchecked")
    static <T> T from(final int pos, final Buffer buffer) {
        final ByteArrayInputStream stream = new ByteArrayInputStream(buffer.getBytes());
        return Fn.jvmOr(() -> {
            final ObjectInputStream in = new ObjectInputStream(stream);
            return (T) in.readObject();
        });
    }

    /**
     * @param filename The filename to describe source path
     *
     * @return Return the InputStream object mount to source path.
     */
    static InputStream read(final String filename) {
        return read(filename, null);
    }


    static byte[] readBytes(final String filename) {
        final InputStream in = read(filename);
        return Fn.jvmOr(() -> IoN.bytes(in));
    }


    /**
     * Ensure read from path
     * 1. Read from current folder
     * 2. clazz == null: Read from class loader
     * 3. clazz != null: Read from clazz's class loader
     *
     * @param filename The filename to describe source path
     * @param clazz    The class loader related class
     *
     * @return Return the InputStream object mount to source path.
     */
    static InputStream read(final String filename,
                            final Class<?> clazz) {
        final String root = LocalDir.root();
        LOG.io(INFO.IoStream.__FILE_ROOT, root, filename);
        /*
         * 0. new File(filename)
         *    new FileInputStream(File)
         */
        // 切换NIO
        final File file = new File(filename);
        if (file.exists()) {
            LOG.io(INFO.IoStream.INF_CUR, file.exists());
            return readSupplier(() -> readDirect(file), filename);
        } else {
            /*
             *  filename re-calculate with root directory extract from LocalDir.root()
             */
            final String refile = IoPath.resolve(root, filename);
            final File fileResolved = new File(refile);
            if (fileResolved.exists()) {
                return readSupplier(() -> readDirect(fileResolved), refile);
            }
        }

        InputStream in;
        if (Objects.isNull(clazz)) {


            /*
             * 1. Thread.currentThread().getContextClassLoader()
             *    loader.getResourceAsStream(filename)
             */
            in = readSupplier(() -> readDirect(filename), filename);
        } else {


            /*
             * 2. clazz.getResourceAsStream(filename)
             */
            in = readSupplier(() -> readDirect(filename, clazz), filename);
            if (Objects.isNull(in)) {


                /*
                 * Switch to 1.
                 */
                in = readSupplier(() -> readDirect(filename), filename);
            }
        }

        // Stream.class get
        if (Objects.isNull(in)) {


            /*
             * 3. Stream.class.getResourceAsStream(filename)
             */
            LOG.io(INFO.IoStream.__CLASS_LOADER_STREAM, filename);
            in = readSupplier(() -> IoStream.class.getResourceAsStream(filename), filename);
        }
        // System.Class Loader
        if (Objects.isNull(in)) {


            /*
             * 4. ClassLoader.getSystemResourceAsStream(filename)
             */
            LOG.io(INFO.IoStream.__CLASS_LOADER_SYSTEM, filename);
            in = readSupplier(() -> ClassLoader.getSystemResourceAsStream(filename), filename);
        }
        /*
         * Jar reading
         * Firstly, check whether it contains jar flag
         */
        if (Objects.isNull(in) && filename.contains(VValue.SUFFIX.JAR_DIVIDER)) {


            /*
             * 5. readJar(filename)
             */
            LOG.io(INFO.IoStream.__JAR_RESOURCE, filename);
            in = readJar(filename);
        }
        if (null == in) {
            throw new _11002Exception500EmptyIo(filename);
        }
        return in;
    }

    // 直接读取文件
    static InputStream readDirect(final File file) {
        if (Objects.isNull(file)) {
            // 文件本身为空，跳过
            return null;
        }
        if (!file.exists() || !file.isFile()) {
            // 文件不存在，跳过
            return null;
        }
        // NIO切换
        return IoN.stream(file);
    }

    static InputStream readDirect(final String filename) {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        LOG.io(INFO.IoStream.__CLASS_LOADER, filename);
        return Fn.jvmOr(() -> loader.getResourceAsStream(filename));
    }

    private static InputStream readDirect(final String filename, final Class<?> clazz) {
        LOG.io(INFO.IoStream.__RESOURCE_AS_STREAM, clazz, filename);
        return Fn.jvmOr(() -> clazz.getResourceAsStream(filename));
    }

    // ----------------- 私有方法

    private static InputStream readJar(final String filename) {
        return readSupplier(() -> {
            try {
                final URL url = new URI(filename).toURL();
                final String protocol = url.getProtocol();
                if (VValue.PROTOCOL.JAR.equals(protocol)) {
                    final JarURLConnection jarCon = (JarURLConnection) url.openConnection();
                    return jarCon.getInputStream();
                } else {
                    return null; // Jar Error
                }
            } catch (final Throwable e) {
                // TODO: Log.fatal(Stream.class, e);
                e.printStackTrace();
                return null;
            }
        }, filename);
    }

    private static InputStream readSupplier(final Supplier<InputStream> supplier,
                                            final String filename) {
        final InputStream in = supplier.get();
        if (null != in) {
            LOG.io(INFO.IoStream.INF_PATH, filename, in);
        }
        return in;
    }
}
