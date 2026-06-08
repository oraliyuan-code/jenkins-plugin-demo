package io.jenkins.plugins.demo.lifecycle;

import hudson.Extension;
import hudson.model.ManagementLink;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.interceptor.RequirePOST;

/**
 * 管理链接：在「系统管理」页面添加入口，展示演示中心。
 */
@Extension
public class DemoManagementLink extends ManagementLink {

    @Override
    public String getIconFileName() {
        return "symbol-help-circle plugin-ionicons-api";
    }

    @Override
    public String getUrlName() {
        return "demo-lifecycle";
    }

    @Override
    public String getDisplayName() {
        return "插件生命周期演示";
    }

    @Override
    public String getDescription() {
        return "查看 Jenkins 插件扩展点与生命周期触发记录";
    }

    public List<DemoEventLog.Event> getRecentEvents() {
        return DemoEventLog.getRecentEvents();
    }

    public Map<String, Integer> getTriggerCounts() {
        return DemoEventLog.getTriggerCounts();
    }

    public int getTriggerCount(String extensionPoint) {
        return getTriggerCounts().getOrDefault(extensionPoint, 0);
    }

    public DemoGlobalConfiguration getGlobalConfig() {
        return DemoGlobalConfiguration.get();
    }

    public List<DemoExtensionPointInfo> getExtensionPoints() {
        return DemoExtensionPointInfo.all();
    }

    @RequirePOST
    public HttpResponse doClearLog() throws IOException {
        Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        DemoEventLog.clear();
        return HttpResponses.redirectTo(".");
    }

    public HttpResponse doExportLog(@QueryParameter String format) throws IOException {
        Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        StringBuilder builder = new StringBuilder();
        builder.append("时间,扩展点,阶段,详情\n");
        for (DemoEventLog.Event event : DemoEventLog.getRecentEvents()) {
            builder.append(event.formattedTime())
                    .append(',')
                    .append(escapeCsv(event.getExtensionPoint()))
                    .append(',')
                    .append(escapeCsv(event.getPhase()))
                    .append(',')
                    .append(escapeCsv(event.getDetail()))
                    .append('\n');
        }
        return HttpResponses.text(builder.toString());
    }

    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public static final class DemoExtensionPointInfo {
        private final String name;
        private final String lifecycle;
        private final String controls;
        private final String whereVisible;
        private final String demoClass;

        public DemoExtensionPointInfo(
                String name, String lifecycle, String controls, String whereVisible, String demoClass) {
            this.name = name;
            this.lifecycle = lifecycle;
            this.controls = controls;
            this.whereVisible = whereVisible;
            this.demoClass = demoClass;
        }

        public String getName() {
            return name;
        }

        public String getLifecycle() {
            return lifecycle;
        }

        public String getControls() {
            return controls;
        }

        public String getWhereVisible() {
            return whereVisible;
        }

        public String getDemoClass() {
            return demoClass;
        }

        public static List<DemoExtensionPointInfo> all() {
            return Arrays.asList(
                    new DemoExtensionPointInfo(
                            "Plugin.start/stop",
                            "插件加载/卸载",
                            "初始化与释放插件级资源",
                            "后台日志 + 本页事件记录",
                            "DemoLifecyclePlugin"),
                    new DemoExtensionPointInfo(
                            "Initializer",
                            "Jenkins 启动",
                            "插件加载完成后的初始化逻辑",
                            "后台日志 + 本页事件记录",
                            "DemoInitializer"),
                    new DemoExtensionPointInfo(
                            "PeriodicWork",
                            "后台定时",
                            "周期性巡检、清理、同步",
                            "本页事件记录（每 5 分钟）",
                            "DemoPeriodicWork"),
                    new DemoExtensionPointInfo(
                            "GlobalConfiguration",
                            "全局配置",
                            "系统级开关与参数",
                            "系统管理 → 系统配置",
                            "DemoGlobalConfiguration"),
                    new DemoExtensionPointInfo(
                            "ManagementLink",
                            "管理入口",
                            "自定义管理页面",
                            "系统管理 → 插件生命周期演示",
                            "DemoManagementLink"),
                    new DemoExtensionPointInfo(
                            "JobProperty",
                            "任务配置",
                            "为 Job 附加元数据或行为",
                            "任务配置 → 勾选「启用演示 Job 属性」",
                            "DemoJobProperty"),
                    new DemoExtensionPointInfo(
                            "BuildWrapper",
                            "构建前/后",
                            "包装构建环境（如设置变量、准备目录）",
                            "Freestyle 配置 + 构建日志",
                            "DemoBuildWrapper"),
                    new DemoExtensionPointInfo(
                            "Builder",
                            "构建步骤",
                            "Freestyle 构建过程中执行的步骤",
                            "Freestyle 配置 + 构建日志",
                            "DemoBuilder"),
                    new DemoExtensionPointInfo(
                            "Publisher",
                            "构建后",
                            "构建结束后的动作（通知、报告等）",
                            "Freestyle 配置 + 构建日志",
                            "DemoPublisher"),
                    new DemoExtensionPointInfo(
                            "RunListener",
                            "构建生命周期",
                            "监听构建开始、完成、删除",
                            "构建日志 + 本页事件记录",
                            "DemoRunListener"),
                    new DemoExtensionPointInfo(
                            "Pipeline Step",
                            "Pipeline 步骤",
                            "在 Declarative/Scripted Pipeline 中调用",
                            "Pipeline 脚本 + 构建日志",
                            "demoLifecycle()"));
        }
    }
}
