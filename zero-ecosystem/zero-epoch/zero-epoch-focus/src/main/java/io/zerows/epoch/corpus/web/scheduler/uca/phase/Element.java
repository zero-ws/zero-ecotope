package io.zerows.epoch.corpus.web.scheduler.uca.phase;

import io.r2mo.function.Actuator;
import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.web.scheduler.atom.Mission;
import io.zerows.epoch.corpus.web.scheduler.zdk.JobIncome;
import io.zerows.epoch.corpus.web.scheduler.zdk.JobOutcome;
import io.zerows.component.shared.context.KRunner;
import io.zerows.enums.EmJob;

import java.util.Objects;

/*
 * Assist class to help Agha object to process income / outcome extraction
 */
class Element {

    private static final Cc<String, JobIncome> CC_INCOME = Cc.open();
    private static final Cc<String, JobOutcome> CC_OUTCOME = Cc.open();

    static JobIncome income(final Mission mission) {
        final Class<?> incomeCls = mission.getIncome();
        JobIncome income = null;
        if (Objects.nonNull(incomeCls) && Ut.isImplement(incomeCls, JobIncome.class)) {
            income = CC_INCOME.pick(() -> Ut.instance(incomeCls), mission.getCode());
            // income = FnZero.po?l(Pool.INCOMES, mission.getCode(), () -> Ut.instance(incomeCls));
        }
        return income;
    }

    static JobOutcome outcome(final Mission mission) {
        final Class<?> outcomeCls = mission.getOutcome();
        JobOutcome outcome = null;
        if (Objects.nonNull(outcomeCls) && Ut.isImplement(outcomeCls, JobOutcome.class)) {
            outcome = CC_OUTCOME.pick(() -> Ut.instance(outcomeCls), mission.getCode());
            // outcome = FnZero.po?l(Pool.OUTCOMES, mission.getCode(), () -> Ut.instance(outcomeCls));
        }
        return outcome;
    }

    static void onceLog(final Mission mission, final Actuator actuator) {
        if (EmJob.JobType.ONCE == mission.getType()) {
            KRunner.run(() -> Fn.jvmAt(actuator), "once-logger-debug");
        }
    }
}
