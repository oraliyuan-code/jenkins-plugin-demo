package io.jenkins.plugins.demo.lifecycle;

import hudson.init.InitMilestone;
import hudson.init.Initializer;
import java.util.logging.Logger;

/**
 * 初始化器：Jenkins 启动到特定里程碑后执行，早于大多数用户操作。
 */
public class DemoInitializer {

    private static final Logger LOGGER = Logger.getLogger(DemoInitializer.class.getName());

    @Initializer(after = InitMilestone.PLUGINS_STARTED)
    public static void onPluginsStarted() {
        DemoEventLog.record(
                "Initializer",
                "PLUGINS_STARTED 之后",
                "Jenkins 插件加载完成后执行，适合做一次性初始化。");
        LOGGER.info("[DemoLifecycle] Initializer 已执行");
    }
}
