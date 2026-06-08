package io.jenkins.plugins.demo.lifecycle.freestyle;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import io.jenkins.plugins.demo.lifecycle.DemoEventLog;
import io.jenkins.plugins.demo.lifecycle.DemoGlobalConfiguration;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * 任务属性：附加在 Job 上的配置，随 Job 保存/加载。
 */
public class DemoJobProperty extends JobProperty<Job<?, ?>> {

    private final boolean enabled;

    @DataBoundConstructor
    public DemoJobProperty(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setOwner(Job<?, ?> job) {
        super.setOwner(job);
        if (enabled && job != null && DemoGlobalConfiguration.get().isEnabled()) {
            DemoEventLog.record(
                    "JobProperty",
                    "setOwner",
                    "任务「" + job.getFullDisplayName() + "」启用了演示 Job 属性");
        }
    }

    @Extension
    public static final class DescriptorImpl extends JobPropertyDescriptor {

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "演示 Job 属性（JobProperty）";
        }
    }
}
