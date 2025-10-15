package io.zerows.epoch.bootplus.boot;

import io.zerows.epoch.bootplus.exploit.QModeller;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.support.Ut;

/**
 * @author lang : 2023-06-12
 */
public class EngrossAtom {

    public static void run(final Class<?> clazz, final String[] args) {
        /*
         * 不做任何输入限制，都带有默认值处理
         */
        final ArgAtom input = ArgAtom.of(args);
        final String path = input.value(KName.PATH);
        final String module = input.value(KName.MODULE);
        Ke.LOG.Ke.info(clazz, """
                信息说明
                \t环境：{0}
                \t模型根路径：{1}
                \t模型：{2}
                """,
            input.environment(), path, module
        );

        final String modDir;
        if (Ut.isNil(path)) {
            modDir = "app@running/@atom/" + module;
        } else {
            modDir = path + "/app@running/@atom/" + module;
        }
        final QModeller modeller = QModeller.of(modDir, modDir);
        modeller.preprocess(() -> {
            Ke.LOG.Ke.info(clazz, "「Pre」建模预处理完成！");
            modeller.initialize(() -> {
                Ke.LOG.Ke.info(clazz, "「Init」模型初始化完成！");
                System.exit(0);
            });
        });
    }
}
