package io.zerows.epoch.support;

import io.zerows.epoch.constant.VEnv;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.enums.app.OsType;
import io.zerows.epoch.common.shared.program.Kv;

import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023/4/28
 */
class _Env extends _End {
    protected _Env() {
    }

    public static OsType envOs() {
        // os.name
        return OsType.from(System.getProperty(VEnv.PROP.OS_NAME));
    }

    /**
     * 默认读取 name 对应环境变量，如果不存在返回空字符串
     *
     * @param name 环境变量名
     *
     * @return 环境变量值
     */
    public static String env(final String name) {
        return UEnv.readEnv(name, VString.EMPTY);
    }

    /**
     * 默认读取 name 对应环境变量，转换成 Class<T> 对应类型
     *
     * @param name  环境变量名
     * @param clazz 环境变量值类型
     * @param <T>   环境变量值类型
     *
     * @return 环境变量值
     */
    public static <T> T env(final String name, final Class<T> clazz) {
        return UEnv.readEnv(name, clazz);
    }

    /**
     * 默认读取 name 对应环境变量，如果不存在返回 defaultValue
     *
     * @param name         环境变量名
     * @param defaultValue 默认值
     *
     * @return 环境变量值
     */
    public static String envWith(final String name, final String defaultValue) {
        return UEnv.readEnv(name, defaultValue);
    }

    /**
     * 默认读取 name 对应环境变量，如果不存在返回 defaultValue
     *
     * @param name         环境变量名
     * @param defaultValue 默认值
     * @param clazz        环境变量值类型
     * @param <T>          环境变量值类型
     *
     * @return 环境变量值
     */
    public static <T> T envWith(final String name, final T defaultValue, final Class<T> clazz) {
        return UEnv.readEnv(name, defaultValue, clazz);
    }

    /**
     * 将属性文件中的内容写入到环境变量中
     *
     * @param properties 属性文件
     *
     * @return 写入后的环境变量
     */
    public static ConcurrentMap<String, String> envOut(final Properties properties) {
        return UEnv.writeEnv(properties);
    }

    /**
     * 将属性文件中的内容写入到环境变量中
     *
     * @param name  环境变量名
     * @param value 环境变量值
     *
     * @return 写入后的环境变量
     */
    public static Kv<String, String> envOut(final String name, final String value) {
        return UEnv.writeEnv(name, value);
    }

    /**
     * 打印环境变量专用方法，可直接将环境变量提取并打印
     *
     * @param envs 环境变量
     *
     * @return 环境变量字符串
     */
    public static String envString(final ConcurrentMap<String, String> envs) {
        return UEnv.reportEnv(envs);
    }
}
