package io.jenkins.plugins.demo.lifecycle.freestyle;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import io.jenkins.plugins.demo.lifecycle.DemoEventLog;
import io.jenkins.plugins.demo.lifecycle.DemoGlobalConfiguration;
import java.io.IOException;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * 构建步骤：Freestyle 任务「构建」阶段执行的动作。
 */
public class DemoBuilder extends Builder implements SimpleBuildStep {

    private final String message;

    @DataBoundConstructor
    public DemoBuilder(String message) {
        this.message = message == null ? "执行演示构建步骤" : message.trim();
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void perform(
            Run<?, ?> run,
            FilePath workspace,
            EnvVars env,
            Launcher launcher,
            TaskListener listener)
            throws InterruptedException, IOException {
        if (!DemoGlobalConfiguration.get().isEnabled()) {
            listener.getLogger().println("[DemoLifecycle] Builder 已跳过（全局演示功能已禁用）");
            return;
        }
        listener.getLogger().println("[DemoLifecycle] Builder.perform - " + message);
        DemoEventLog.record(
                "Builder",
                "perform",
                "任务「" + run.getParent().getFullDisplayName() + "」构建 #" + run.getNumber());
    }

    @Symbol("demoBuilder")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "演示构建步骤（Builder）";
        }
    }
}
