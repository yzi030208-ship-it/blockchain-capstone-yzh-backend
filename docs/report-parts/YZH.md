# YZH 路线 A 报告片段（阶段更新）

本片段供最终报告副本汇总。2026-10-22 已完成 SimpleStorage 实测和新 Escrow 部署；**两单验收、仲裁及时间线尚未完成**。当前交接以 [三人共用交接](../team-handoff.md) 为准，不把待测结果写成完成。

## 我的开发内容

- 建立独立 Spring Boot + Web3j 项目基线，移除示例配置和 Java 默认值中的明文私钥，改用环境变量；保留 SimpleStorage 示例。
- 实现 `EscrowController` 的六个必做路由及订单事件时间线接口，统一参数验证和错误响应。
- 实现 `EscrowEventQueryService`：按部署区块到查询时固定的最新区块，每批最多 10 个区块查询；按区块、交易、日志位置排序，以交易哈希加 logIndex 去重。
- 覆盖 Escrow ABI 中六类事件。DHT 的合约源码和生成封装已接入；本地编译与集成测试结果见 [DHT 验证记录](../dht-verification.md)，测试网事件仍待两单操作后验证。

## 解释题 4A-3 事件查询与逐单读取

若从部署区块 `B0` 查询到本次固定链头 `B1`，且 RPC 每次最多允许 10 个区块，则单次列表重建需约 `ceil((B1-B0+1)/10)` 次 `eth_getLogs`，另有 1 次 `eth_blockNumber`。这不是永远只需 1 次 RPC。若再逐笔读取 10,000 个订单的当前状态，需要额外约 10,000 次 `getOrder` 调用；事件扫描能有效取得创建时字段和交易来源，但 OrderCreated 不包含订单的最终状态，不能把事件记录冒充实时详情。本组实测的部署区块、链头区块、实际窗口数和订单数待运行后补入。

## 解释题 4A-6 无认证仲裁接口与密钥风险

`resolve` 由服务器持有的仲裁账户签名。若 REST 接口缺少身份认证与操作授权，能访问该接口的人可能让服务器替他提交争议裁决。当前演示服务仅监听 `127.0.0.1`，缩小访问范围，但不构成完整认证。实际系统至少应：①对操作者进行身份认证及按订单授权，并核实裁决依据；②对仲裁请求限流、记录不可篡改审计日志，并将密钥放在专用密钥管理系统或硬件签名环境。若仲裁私钥泄露，攻击者可对符合合约条件的 Disputed 订单发起裁决；资金只能按合约写定的买家或卖家地址转出，不能凭该权限直接提走全部未争议订单的代币。最终报告需结合本组实际部署地址及交易补证。

## 时间线实现与待测结论

正常单预期显示真实 `OrderCreated → OrderPaid → OrderCompleted` 日志；争议单预期显示 `OrderCreated → OrderPaid → OrderDisputed → OrderResolved(sellerWins=false)`。实际 id 等 DHT 创建回执，不能预填 0/1。`resolve(false)` 只发 `OrderResolved`，**不虚构 `OrderRefunded` 日志**。待两单完成后，记录每条事件的区块号、交易哈希和浏览器 Logs 对照；再重启服务并 `load` 同一地址、原部署区块 `2672683` 复测。

## YZH 本人截图清单

| 编号 | 待截图内容 | 实际文件 / 交易哈希 |
|---|---|---|
| 4A-3 | SimpleStorage 后端 deploy、set、get 的真实响应及 set 交易浏览器详情 | 已取得 `evidence/4A/4A-3-01*`、`4A-3-02*`；set 哈希见实验执行记录 |
| 4A-4 | `EscrowController.java`、`EscrowEventQueryService.java` 可读代码画面 | 文件已写；待截图 |
| 4A-5 部署 | 本次新 Escrow 部署响应、链上 arbiter | 已取得 `evidence/4A/4A-5-01*`、`4A-5-01b*`；地址和哈希见公开配置 |
| 4A-5 卖家 | YZH 钱包对本次新 Escrow 的争议单执行 `dispute(实际id)`，回执和订单状态 | 待 DHT 两单付款后实测 |
| T-1 | `GET /api/escrow/orders/{正常单实际id}/events` 三条真实事件 | 待实测 |
| T-2 | `GET /api/escrow/orders/{争议单实际id}/events` 四条真实事件，`sellerWins=false` | 待实测 |
| T-3 | 与区块浏览器 Logs 对照，并展示跨 10 区块查询及重启 load 结果 | 待实测 |

## 本人问题与 AI 使用记录

- 问题：课程示例把私钥写在配置和 Java 默认值中。处理：改为本机环境变量。后端专用公开地址 `0x6717496Fe22477B41e6Ca07353C308fF38A61797` 已充值测试网 POT；YZH 在本机注入密钥、启动后端并部署新 Escrow，真实交易见 [公开配置](../project-config.md)。私钥未写入仓库或截图。
- 问题：Web3j Gradle 插件在下载 Solidity 0.8.35 时停住。DHT 固定 Solidity 0.8.20 和 Paris EVM 目标、复用本机已核对的原生编译器，完成完整封装生成与本地集成测试；详见 [DHT 验证记录](../dht-verification.md)。默认跳过生成的测试仍不能冒充完整构建。
- AI 使用：Codex 协助检查示例、实现接口和日志查询、编写测试及文档。所有链上信息通过 RPC 或本人交易回执核对；后续错误、修正与本人签名操作继续记录。

最终报告中的组号、三人完整身份、真实贡献比例、尚未发生的订单及仲裁交易和截图由组长收齐证据后填写；已完成的部署地址与哈希按公开配置引用，保留原报告模板评分页。
