# Demo Lifecycle Plugin

Jenkins 插件生命周期轻量演示项目。不实现具体业务，只展示常见扩展点在什么阶段介入、能控制什么，并在页面上给出可见反馈。

**目标 Jenkins 版本：** 2.479.3+（较新的 LTS 线）  
**JDK：** 17+（推荐 21）

## 演示的扩展点

| 扩展点 | 生命周期 | 页面体现 |
|--------|----------|----------|
| `Plugin.start/stop` | 插件加载/卸载 | 演示中心事件 + 后台日志 |
| `Initializer` | Jenkins 启动 | 演示中心事件 |
| `PeriodicWork` | 后台每 5 分钟 | 演示中心事件 |
| `GlobalConfiguration` | 全局配置保存 | 系统配置页 |
| `ManagementLink` | 管理入口 | 系统管理 → 插件生命周期演示 |
| `JobProperty` | 任务配置 | Freestyle 任务配置页 |
| `BuildWrapper` | 构建前/后 | Freestyle 配置 + 控制台 |
| `Builder` | 构建步骤 | Freestyle 配置 + 控制台 |
| `Publisher` | 构建后 | Freestyle 配置 + 控制台 |
| `RunListener` | 构建开始/完成/删除 | 控制台 + 演示中心 |
| `demoLifecycle()` | Pipeline 步骤 | Pipeline 脚本 + 控制台 |

## 构建与安装

### 打包 `.hpi`

```bash
mvn clean package
```

产物路径：

```
target/demo-lifecycle.hpi          # Maven 构建输出
releases/demo-lifecycle-1.0-SNAPSHOT.hpi   # 可直接安装的发布包（已纳入仓库）
```

在 Jenkins：**系统管理 → 插件管理 → 高级 → 上传插件**，选择 `releases/` 下的 `.hpi` 文件即可。

### 本地调试（嵌入式 Jenkins）

```bash
mvn hpi:run
```

浏览器访问：`http://localhost:8080/jenkins`

首次启动会跳过安装向导。默认无安全限制，便于本地演示。

## 使用步骤

### 1. 打开演示中心

**系统管理 → 插件生命周期演示**

可查看扩展点说明、触发次数、最近事件记录。

### 2. 全局配置（可选）

**系统管理 → 系统配置 → 插件生命周期演示（GlobalConfiguration）**

- 启用/禁用演示功能
- 修改欢迎语

保存后返回演示中心，可看到 `GlobalConfiguration` 事件。

### 3. Freestyle 任务体验

1. 新建 **Freestyle project**
2. **General** → 勾选「启用演示 Job 属性（JobProperty）」
3. **构建环境** → 勾选「演示构建包装（BuildWrapper）」
4. **构建** → 添加「演示构建步骤（Builder）」
5. **构建后操作** → 添加「演示构建后操作（Publisher）」
6. 保存并 **立即构建**

查看 **控制台输出** 与 **演示中心** 的事件表。

### 4. Pipeline 任务体验

新建 **Pipeline** 任务，使用如下脚本：

```groovy
pipeline {
  agent any
  stages {
    stage('Demo') {
      steps {
        demoLifecycle('Pipeline 演示步骤')
      }
    }
  }
}
```

构建后同样可在控制台与演示中心看到 `Pipeline Step` 与 `RunListener` 记录。

## 项目结构

```
src/main/java/io/jenkins/plugins/demo/lifecycle/
├── DemoLifecyclePlugin.java      # 插件入口
├── DemoEventLog.java             # 内存事件记录
├── DemoGlobalConfiguration.java  # 全局配置
├── DemoManagementLink.java       # 演示中心页面
├── DemoInitializer.java          # 启动初始化
├── DemoPeriodicWork.java         # 定时任务
├── DemoRunListener.java          # 构建监听
├── freestyle/                    # Freestyle 扩展点
└── pipeline/DemoStep.java        # Pipeline 步骤
```

## 说明

- 事件记录保存在内存中，**重启 Jenkins 后会清空**。
- `PeriodicWork` 默认每 **5 分钟** 触发一次，便于观察后台扩展点。
- 本插件仅供学习演示，未发布到 Jenkins 更新中心。

## 许可证

MIT
