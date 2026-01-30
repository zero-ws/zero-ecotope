package io.zerows.extension.skeleton.boot;

import io.zerows.cortex.sdk.HQBE;
import io.zerows.epoch.configuration.ConfigMod;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.epoch.spi.Dictionary;
import io.zerows.extension.skeleton.spi.ExActivity;
import io.zerows.extension.skeleton.spi.ExApp;
import io.zerows.extension.skeleton.spi.ExArbor;
import io.zerows.extension.skeleton.spi.ExAtom;
import io.zerows.extension.skeleton.spi.ExAttachment;
import io.zerows.extension.skeleton.spi.ExIo;
import io.zerows.extension.skeleton.spi.ExLinkage;
import io.zerows.extension.skeleton.spi.ExModulat;
import io.zerows.extension.skeleton.spi.ExOwner;
import io.zerows.extension.skeleton.spi.ExSetting;
import io.zerows.extension.skeleton.spi.ExTransit;
import io.zerows.extension.skeleton.spi.ExUser;
import io.zerows.extension.skeleton.spi.ScCredential;
import io.zerows.extension.skeleton.spi.ScOrbit;
import io.zerows.extension.skeleton.spi.ScPermit;
import io.zerows.extension.skeleton.spi.ScRoutine;
import io.zerows.extension.skeleton.spi.ScSeeker;
import io.zerows.extension.skeleton.spi.UiApeak;
import io.zerows.extension.skeleton.spi.UiApeakMy;
import io.zerows.extension.skeleton.spi.UiForm;
import io.zerows.extension.skeleton.spi.UiValve;
import io.zerows.platform.constant.VString;
import io.zerows.specification.development.HMaven;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-10-31
 */
@Slf4j
class ExBoot {

    private static final List<Class<?>> SPI_SET = new ArrayList<>() {
        {
            this.add(ConfigMod.class);
            this.add(HQBE.class);
            this.add(Dictionary.class);
            // -- 扩展接口处理
            this.add(ExActivity.class);           // 🚶 活动跟踪器接口，用于追踪系统中的各种活动
            this.add(ExApp.class);                // 📱 应用扩展接口，定义应用级别的扩展点
            this.add(ExArbor.class);              // 🌳 树形结构处理接口，用于处理树状数据结构
            this.add(ExAtom.class);               // ⚛️ 原子操作接口，定义系统中的原子级操作
            this.add(ExAttachment.class);         // 📎 附件处理接口，管理系统中的附件功能
            this.add(ExIo.class);                 // 💾 IO扩展接口，处理输入输出相关扩展
            this.add(ExLinkage.class);            // 🔗 联动处理接口，处理字段或模块间的联动关系
            this.add(ExModulat.class);            // 🧩 模块化处理接口，支持模块化的功能扩展
            this.add(ExOwner.class);              // 👤 所有者接口，处理资源所有权相关逻辑
            this.add(ExSetting.class);            // ⚙️ 设置接口，管理系统和用户的各种设置
            this.add(ExTransit.class);            // 📬 中转接口，处理消息和数据的中转
            this.add(ExUser.class);               // 👥 用户扩展接口，处理用户相关功能扩展
            this.add(ScCredential.class);         // 🪪 凭证接口，处理认证和授权凭证
            this.add(ScOrbit.class);              // 🛰️ 轨道接口，定义数据流转的标准轨道
            this.add(ScPermit.class);             // 🔑 许可接口，处理权限许可相关功能
            this.add(ScRoutine.class);            // 🔄 例程接口，定义系统标准处理流程
            this.add(ScSeeker.class);             // 🔍 查找器接口，用于查找和发现系统资源
            // -- UI 处理
            this.add(UiForm.class);               // 📝 表单接口，处理前端表单相关功能
            this.add(UiApeak.class);              // 📊 界面顶峰接口，处理UI界面的顶栏显示
            this.add(UiApeakMy.class);            // 👤 个人界面顶峰接口，处理个人化UI顶栏
            this.add(UiValve.class);              // 🚪 界面阀门接口，控制UI组件的显示和隐藏
        }
    };

    static void vLog() {
        log.info("[ XMOD ] 扩展模块 SPI 监控详情：");
        for (final Class<?> spiClass : SPI_SET) {
            final List<?> implementations = HPI.findMany(spiClass);
            final String implNames = implementations.isEmpty()
                ? VString.EMPTY
                : implementations.stream()
                .map(impl -> impl.getClass().getName())
                .distinct()
                .collect(Collectors.joining(", "));
            log.info("[ XMOD ]    \uD83E\uDD4F {} = [{}]", spiClass.getName(), implNames);
        }
    }

    static void vModule() {
        log.info("[ XMOD ]  加载模块 ID 集合：");
        final Set<Class<?>> scanned = OCacheClass.entireValue();
        final Set<String> idSet = new TreeSet<>();
        scanned.forEach(item -> {
            if (Arrays.asList(item.getInterfaces()).contains(HMaven.class)) {
                final String value = Ut.field(item, "BUNDLE_SYMBOLIC_NAME");
                if (Objects.nonNull(value)) {
                    idSet.add(value);
                }
            }
        });
        idSet.forEach(value -> log.info("[ XMOD ]    - {}", value));
    }
}