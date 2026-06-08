package io.jenkins.plugins.demo.lifecycle;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

/**
 * 构建监听器：在构建开始、结束、删除时介入。
 */
@Extension
public class DemoRunListener extends RunListener<Run<?, ?>> {

    @Override
    public void onStarted(Run<?, ?> run, TaskListener listener) {
        if (!DemoGlobalConfiguration.get().isEnabled()) {
            return;
        }
        String message = "构建 #" + run.getNumber() + " 已开始";
        DemoEventLog.record("RunListener", "onStarted", message);
        log(listener, "[DemoLifecycle] RunListener.onStarted - " + message);
    }

    @Override
    public void onCompleted(Run<?, ?> run, TaskListener listener) {
        if (!DemoGlobalConfiguration.get().isEnabled()) {
            return;
        }
        String message = "构建 #" + run.getNumber() + " 已完成，结果：" + run.getResult();
        DemoEventLog.record("RunListener", "onCompleted", message);
        log(listener, "[DemoLifecycle] RunListener.onCompleted - " + message);
    }

    @Override
    public void onDeleted(Run<?, ?> run) {
        if (!DemoGlobalConfiguration.get().isEnabled()) {
            return;
        }
        DemoEventLog.record(
                "RunListener",
                "onDeleted",
                "构建 #" + run.getNumber() + " 已删除");
    }

    private static void log(TaskListener listener, String message) {
        listener.getLogger().println(message);
    }
}
