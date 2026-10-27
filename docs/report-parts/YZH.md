# YZH 路线 A 报告片段（阶段更新）

本片段供最终报告副本汇总。SimpleStorage、新 Escrow 部署、两单后端查询、争议单仲裁退款、正常单确认及两单时间线已有真实测试网结果。4A-6/7 和 T-1/T-2/T-3 的 YZH PNG 已归档；ZZL 的区块浏览器验收仍待补。当前交接以 [三人共用交接](../team-handoff.md) 为准。

## 我的开发内容

- 建立独立 Spring Boot + Web3j 项目基线，移除示例配置和 Java 默认值中的明文私钥，改用环境变量；保留 SimpleStorage 示例。
- 实现 `EscrowController` 的六个必做路由及订单事件时间线接口，统一参数验证和错误响应。
- 实现 `EscrowEventQueryService`：按部署区块到查询时固定的最新区块，每批最多 10 个区块查询，最多 8 个窗口并发以降低长历史查询耗时；按区块、交易、日志位置排序，以交易哈希加 logIndex 去重，RPC 错误仍返回可见错误。
- 覆盖 Escrow ABI 中六类事件。DHT 的合约源码和生成封装已接入；本地编译与集成测试结果见 [DHT 验证记录](../dht-verification.md)。2026-10-26 实际测试网订单列表与争议单四事件已由后端返回并保存。

## 解释题 4A-3 事件查询与逐单读取

若从部署区块 `B0` 查询到本次固定链头 `B1`，且 RPC 每次最多允许 10 个区块，则单次列表重建需约 `ceil((B1-B0+1)/10)` 次 `eth_getLogs`，另有 1 次 `eth_blockNumber`。并发 8 个窗口只缩短等待时间，不减少窗口数。若再逐笔读取 10,000 个订单的当前状态，需要额外约 10,000 次 `getOrder` 调用；事件扫描能取得创建时字段和交易来源，但 OrderCreated 不包含订单的最终状态，不能把事件记录冒充实时详情。本组 Escrow 从区块 `2672683` 起扫描；顺序查询曾超过 240 秒未返回，修改后 `GET /orders` 约 40 秒返回两笔真实订单，见 `evidence/4A/4A-6-01_YZH_订单列表.json`。当次精确链头和窗口数未记录，不作猜测。

## 解释题 4A-6 无认证仲裁接口与密钥风险

`resolve` 由服务器持有的仲裁账户签名。若 REST 接口缺少身份认证与操作授权，能访问该接口的人可能让服务器替他提交争议裁决。当前演示服务仅监听 `127.0.0.1`，缩小访问范围，但不构成完整认证。实际系统至少应：①对操作者进行身份认证及按订单授权，并核实裁决依据；②对仲裁请求限流、记录不可篡改审计日志，并将密钥放在专用密钥管理系统或硬件签名环境。若仲裁私钥泄露，攻击者可对符合合约条件的 Disputed 订单发起裁决；资金只能按合约写定的买家或卖家地址转出，不能凭该权限直接提走全部未争议订单的代币。本组真实操作中，未争议的订单 0 调用 `resolve(0,true)` 产生失败回执 `0x07c6c39d408ad1d599cb4b2e1039f4630f2c5d12dd052e92032111e169aab3ed`（`status=0x0`）；争议订单 1 调用 `resolve(1,false)` 成功，哈希 `0x86b5d80b974abd153f5b40a4e08f72f0d5657bf1fd048289eb621878fbbf9a92`，链上将 30 枚指定代币退回买家。

## 时间线实现与待测结论

正常单 0 在 DHT `confirm(0)` 后实际有 `OrderCreated → OrderPaid → OrderCompleted` 三条日志，见[完整 T-1](../../evidence/timeline/T-1_YZH_正常单完整时间线.json)及[实时接口截图](../../evidence/timeline/T-1_YZH_正常单完整时间线_实时接口.png)。确认交易位于区块 `2689699`，正确哈希 `0x4d1ab17ee3ede2c696944a2ace7d0f8e195949cdb6928b5883b1b3c162cd6f46`，课程 RPC 回执 `status=0x1`。争议单 1 实际有 `OrderCreated → OrderPaid → OrderDisputed → OrderResolved(sellerWins=false)` 四条日志，见 `evidence/timeline/T-2_YZH_争议单时间线.json` 和[实时接口截图](../../evidence/timeline/T-2_YZH_争议单完整时间线_实时接口.png)，按区块和交易/日志索引排序。`resolve(false)` 的业务结果是 Refunded，但合约只发 `OrderResolved`，**没有 `OrderRefunded` 日志**。后端重启后已用相同地址和原部署区块 `2672683` 成功 `load`；[T-3 代码图](../../evidence/timeline/T-3-02_YZH_十区块分批并发与排序去重代码.png)及[load 响应存档图](../../evidence/timeline/T-3-01_YZH_重启load原Escrow响应存档.png)已归档，ZZL 浏览器 Logs 对照仍待补。

## YZH 本人截图清单

| 编号 | 待截图内容 | 实际文件 / 交易哈希 |
|---|---|---|
| 4A-3 | SimpleStorage 后端 deploy、set、get 的真实响应及 set 交易浏览器详情 | 已取得 `evidence/4A/4A-3-01*`、`4A-3-02*`；set 哈希见实验执行记录 |
| 4A-4 | `EscrowController.java`、`EscrowEventQueryService.java` 可读代码画面 | 事件查询分批代码已有 T-3-02 截图；Controller 及完整 4A-4 仍待补 |
| 4A-5 部署 | 本次新 Escrow 部署响应、链上 arbiter | 已取得 `evidence/4A/4A-5-01*`、`4A-5-01b*`；地址和哈希见公开配置 |
| 4A-5 卖家 | YZH 钱包对订单 1 执行 `dispute(1)`，回执和订单状态 | 截图 `evidence/4A/4A-5-05_YZH_卖家dispute成功_订单1Disputed.png`；完整哈希见交接记录 |
| T-1 | `GET /api/escrow/orders/0/events` 三条真实事件 | 完整 JSON 与实时接口 PNG 已存 `evidence/timeline/` |
| T-2 | `GET /api/escrow/orders/1/events` 四条真实事件，`sellerWins=false` | JSON 与实时接口 PNG 已存 `evidence/timeline/` |
| T-3 | 与链上 Logs 对照，并展示跨 10 区块查询及重启 load 结果 | 分批代码 PNG、原合约 load 响应存档 PNG 已存；Logs 对照可结合 T-2 实时接口与 4A-7-02 回执，ZZL 浏览器图待补 |

## 本人问题与 AI 使用记录

- 问题：课程示例把私钥写在配置和 Java 默认值中。处理：改为本机环境变量，增加隐藏输入的启动脚本。后端专用公开地址 `0x6717496Fe22477B41e6Ca07353C308fF38A61797` 已充值测试网 POT；YZH 在本机注入密钥、启动后端并部署新 Escrow，真实交易见 [公开配置](../project-config.md)。仓库与正式报告截图不保存私钥；测试账户密钥曾在聊天中暴露，正式验收后应停止使用并更换。
- 问题：Web3j Gradle 插件在下载 Solidity 0.8.35 时停住。DHT 固定 Solidity 0.8.20 和 Paris EVM 目标、复用本机已核对的原生编译器，完成完整封装生成与本地集成测试；详见 [DHT 验证记录](../dht-verification.md)。默认跳过生成的测试仍不能冒充完整构建。
- AI 使用：Codex 协助检查示例、实现接口和日志查询、编写测试及文档。所有链上信息通过 RPC 或本人交易回执核对；后续错误、修正与本人签名操作继续记录。

最终报告中的组号、三人完整身份、真实贡献比例、尚未发生的订单及仲裁交易和截图由组长收齐证据后填写；已完成的部署地址与哈希按公开配置引用，保留原报告模板评分页。
