package io.jenkins.plugins.demo.lifecycle;

import hudson.Extension;
import hudson.model.PeriodicWork;

/**
 * 定时任务：由 Jenkins 周期性调度，演示插件可在后台持续运行。
 */
@Extension
public class DemoPeriodicWork extends PeriodicWork {

    private static final long FIVE_MINUTES = 5 * 60 * 1000L;

    @Override
    public long getRecurrencePeriod() {
        return FIVE_MINUTES;
    }

    @Override
    protected void doRun() {
        if (!DemoGlobalConfiguration.get().isEnabled()) {
            return;
        }
        DemoEventLog.record(
                "PeriodicWork",
                "定时触发（每 5 分钟）",
                "演示插件可在后台周期性执行任务，如清理、同步、巡检等。");
    }
}
