# RabbitMQ Demo 文档导航

这套文档是当前 `rabbitmq-demo` 的结课档案，定位是“项目说明书 + 学习复盘 + 源码导航 + 面试手册”。正文严格对应现有代码与已经完成的两组测试，不把未来方向写成已实现能力。

## 5 分钟快速入口

1. 先看[总体知识地图](./images/01-rabbitmq-knowledge-map.svg)和[项目架构图](./images/02-project-architecture.svg)。
2. 用[源码导航图](./images/03-source-code-navigation.svg)找类，再读主文档的第 3 章。
3. Producer 可靠性重点看 Confirm/Return、状态保护、状态机和自动补偿（主文档第 5～8 章）。
4. Consumer 可靠性重点看幂等原子抢占、Spring Retry、Retry Queue、DLQ 手动 ACK（主文档第 9～12 章）。
5. 最后读实际测试、项目边界和面试回答（主文档第 15、18～22 章）。

## 文档与资产

- [RabbitMQ 完整学习与面试指南](./RabbitMQ-Complete-Learning-And-Interview-Guide.md)：详细正文、源码、SQL、状态变化、测试时间线、边界和面试题。
- [`images/`](./images/)：23 张预渲染 SVG，IDEA 无需 Mermaid 插件即可查看。
- [`diagrams/`](./diagrams/)：与静态图一一对应的 Mermaid `.mmd` 源码。

## 结课结论

当前实现应准确描述为：

```text
At-Least-Once
+ Consumer 幂等
+ Producer 状态追踪与定时补偿
```

它不是 Exactly Once。MySQL 与 RabbitMQ 之间也不是原子事务；stale `PENDING`、Transactional Outbox 等只属于后续工程化方向，本项目不再继续扩展 RabbitMQ 功能。
