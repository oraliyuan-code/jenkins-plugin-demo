package io.jenkins.plugins.demo.lifecycle.pipeline;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.demo.lifecycle.DemoEventLog;
import io.jenkins.plugins.demo.lifecycle.DemoGlobalConfiguration;
import java.util.Collections;
import java.util.Set;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Pipeline 步骤：在 Declarative/Scripted Pipeline 中通过 demoLifecycle() 调用。
 */
public class DemoStep extends Step {

    private final String message;

    @DataBoundConstructor
    public DemoStep(String message) {
        this.message = message == null ? "执行 Pipeline 演示步骤" : message.trim();
    }

    public String getMessage() {
        return message;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new Execution(message, context);
    }

    private static final class Execution extends SynchronousStepExecution<Void> {

        private static final long serialVersionUID = 1L;

        private final String message;

        Execution(String message, StepContext context) {
            super(context);
            this.message = message;
        }

        @Override
        protected Void run() throws Exception {
            Run<?, ?> run = getContext().get(Run.class);
            TaskListener listener = getContext().get(TaskListener.class);
            if (!DemoGlobalConfiguration.get().isEnabled()) {
                listener.getLogger().println("[DemoLifecycle] demoLifecycle 已跳过（全局演示功能已禁用）");
                return null;
            }
            listener.getLogger().println("[DemoLifecycle] Pipeline Step - " + message);
            if (run != null) {
                DemoEventLog.record(
                        "Pipeline Step",
                        "demoLifecycle()",
                        "任务「" + run.getParent().getFullDisplayName() + "」构建 #" + run.getNumber());
            } else {
                DemoEventLog.record("Pipeline Step", "demoLifecycle()", message);
            }
            return null;
        }
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }

        @Override
        public String getFunctionName() {
            return "demoLifecycle";
        }

        @Override
        public String getDisplayName() {
            return "演示 Pipeline 步骤（demoLifecycle）";
        }
    }
}
