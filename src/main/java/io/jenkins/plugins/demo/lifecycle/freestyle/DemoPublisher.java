package io.jenkins.plugins.demo.lifecycle.freestyle;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;
import io.jenkins.plugins.demo.lifecycle.DemoEventLog;
import io.jenkins.plugins.demo.lifecycle.DemoGlobalConfiguration;
import java.io.IOException;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * 构建后操作：构建结束后执行（通知、归档、标记等场景的演示）。
 */
public class DemoPublisher extends Recorder implements SimpleBuildStep {

    private final String message;

    @DataBoundConstructor
    public DemoPublisher(String message) {
        this.message = message == null ? "执行演示构建后操作" : message.trim();
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
            listener.getLogger().println("[DemoLifecycle] Publisher 已跳过（全局演示功能已禁用）");
            return;
        }
        listener.getLogger().println("[DemoLifecycle] Publisher.perform - " + message);
        DemoEventLog.record(
                "Publisher",
                "perform",
                "任务「" + run.getParent().getFullDisplayName() + "」构建 #" + run.getNumber());
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Symbol("demoPublisher")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "演示构建后操作（Publisher）";
        }
    }
}
