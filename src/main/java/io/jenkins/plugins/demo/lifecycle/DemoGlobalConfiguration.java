package io.jenkins.plugins.demo.lifecycle;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalConfigurationCategory;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest2;

/**
 * 全局配置：在「系统管理 → 系统配置」中可见，控制插件全局行为。
 */
@Extension
public class DemoGlobalConfiguration extends GlobalConfiguration {

    private boolean enabled = true;
    private String welcomeMessage = "Jenkins 插件生命周期演示";

    public DemoGlobalConfiguration() {
        load();
    }

    public static DemoGlobalConfiguration get() {
        return GlobalConfiguration.all().get(DemoGlobalConfiguration.class);
    }

    public boolean isEnabled() {
        return enabled;
    }

    @DataBoundSetter
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    @DataBoundSetter
    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage == null ? "" : welcomeMessage.trim();
    }

    @Override
    public boolean configure(StaplerRequest2 req, JSONObject json) {
        req.bindJSON(this, json);
        save();
        DemoEventLog.record(
                "GlobalConfiguration",
                "配置已保存",
                "演示功能" + (enabled ? "已启用" : "已禁用") + "，欢迎语：" + welcomeMessage);
        return true;
    }

    @Override
    public GlobalConfigurationCategory getCategory() {
        return GlobalConfigurationCategory.get(GlobalConfigurationCategory.Unclassified.class);
    }

    public FormValidation doCheckWelcomeMessage(@QueryParameter String value) {
        if (value == null || value.trim().isEmpty()) {
            return FormValidation.warning("建议填写一句说明，便于在演示中心展示。");
        }
        return FormValidation.ok();
    }
}
