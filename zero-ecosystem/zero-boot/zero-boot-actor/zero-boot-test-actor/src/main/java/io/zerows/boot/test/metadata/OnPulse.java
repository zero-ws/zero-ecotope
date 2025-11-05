package io.zerows.boot.test.metadata;

import io.r2mo.typed.cc.Cc;
import io.zerows.platform.constant.VString;
import io.zerows.platform.metadata.KPathAtom;
import io.zerows.support.Ut;

/**
 * 统一入口，防止新版的单独处理，直接在主函数中调用对应信息启动
 *
 * @author lang : 2023-06-12
 */
public class OnPulse {
    private static final Cc<String, QModeller> CC_MODELLER = Cc.openThread();

    // --------------------- 菜单相关
    public static void menuReview(final Class<?> mainClass) {
        OnMenu.review(mainClass);
    }

    public static void menuWrite(final Class<?> mainClass) {
        // 提取角色相关信息
        OnMenu.write(mainClass);
    }

    // --------------------- 建模相关

    public static void atomInit(final Class<?> mainClass, final KPathAtom pathAtom) {
        final QModeller modeller = atomModeller(pathAtom);
        modeller.initialize();
    }

    public static void atomPre(final Class<?> mainClass, final KPathAtom pathAtom) {
        final QModeller modeller = atomModeller(pathAtom);
        modeller.preprocess();
    }

    private static QModeller atomModeller(final KPathAtom pathAtom) {
        final String input = pathAtom.input();
        final String output = pathAtom.output();
        final String hashKey = Ut.encryptMD5(input + VString.COLON + output);
        return CC_MODELLER.pick(() -> QModeller.of(input, output), hashKey);
    }
}
