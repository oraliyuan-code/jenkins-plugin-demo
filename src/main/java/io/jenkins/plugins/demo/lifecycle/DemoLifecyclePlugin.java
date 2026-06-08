package io.jenkins.plugins.demo.lifecycle;

import hudson.Plugin;
import java.util.logging.Logger;
import jenkins.model.Jenkins;

/**
 * 插件入口：Jenkins 加载/卸载插件时触发。
 */
public class DemoLifecyclePlugin extends Plugin {

    private static final Logger LOGGER = Logger.getLogger(DemoLifecyclePlugin.class.getName());

    @Override
    public void start() throws Exception {
        DemoEventLog.record(
                "Plugin.start()",
                "插件启动",
                "Jenkins 加载本插件时调用，用于初始化资源、注册服务等。");
        LOGGER.info("[DemoLifecycle] 插件已启动");
        super.start();
    }

    @Override
    public void stop() throws Exception {
        DemoEventLog.record(
                "Plugin.stop()",
                "插件停止",
                "Jenkins 卸载或关闭时调用，用于释放资源。");
        LOGGER.info("[DemoLifecycle] 插件已停止");
        super.stop();
    }

    public static DemoLifecyclePlugin get() {
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if (jenkins == null) {
            return null;
        }
        return jenkins.getPlugin(DemoLifecyclePlugin.class);
    }
}
