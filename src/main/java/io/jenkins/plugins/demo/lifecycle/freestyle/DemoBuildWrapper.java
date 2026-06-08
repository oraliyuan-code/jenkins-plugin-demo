package io.jenkins.plugins.demo.lifecycle.freestyle;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import io.jenkins.plugins.demo.lifecycle.DemoEventLog;
import io.jenkins.plugins.demo.lifecycle.DemoGlobalConfiguration;
import java.io.IOException;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * 构建包装器：在构建步骤执行前后设置/清理环境。
 */
public class DemoBuildWrapper extends BuildWrapper {

    private final String prepareMessage;

    @DataBoundConstructor
    public DemoBuildWrapper(String prepareMessage) {
        this.prepareMessage = prepareMessage == null ? "准备构建环境" : prepareMessage.trim();
    }

    public String getPrepareMessage() {
        return prepareMessage;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener)
            throws IOException, InterruptedException {
        AbstractProject project = build.getProject();
        if (DemoGlobalConfiguration.get().isEnabled()) {
            listener.getLogger().println("[DemoLifecycle] BuildWrapper.setUp - " + prepareMessage);
            DemoEventLog.record(
                    "BuildWrapper",
                    "setUp",
                    "任务「" + project.getFullDisplayName() + "」构建 #" + build.getNumber());
        }
        return new Environment() {
            @Override
            public boolean tearDown(AbstractBuild build, BuildListener listener)
                    throws IOException, InterruptedException {
                if (DemoGlobalConfiguration.get().isEnabled()) {
                    listener.getLogger().println("[DemoLifecycle] BuildWrapper.tearDown - 清理构建环境");
                    DemoEventLog.record("BuildWrapper", "tearDown", "构建 #" + build.getNumber() + " 环境已清理");
                }
                return true;
            }
        };
    }

    @Symbol("demoBuildWrapper")
    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "演示构建包装（BuildWrapper）";
        }
    }
}
