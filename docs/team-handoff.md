# 三人共用阶段交接记录

本文件是 DHT、YZH、ZZL **唯一持续更新的交接文档**。每完成一个阶段，由执行人按文末模板追加真实结果，YZH 核对后标明下一位操作者。不要覆盖旧记录、预填未发生的交易，或在文件与截图中写入私钥。当前链上公开参数统一查 [project-config.md](project-config.md)；详细操作与交易留痕查 [experiment-runbook.md](experiment-runbook.md)；截图编号查 [evidence-capture-guide.md](evidence-capture-guide.md)。

## 项目和职责

路线 A 使用 Java 21、Spring Boot 3.2.3、Web3j 4.14.0 与 POTOS Testnet，演示新 Escrow 的两笔 30 枚 YZHToken 订单；正常单完成，争议单由后端仲裁退款。订单列表和时间线须由真实链上事件查询，重启后使用原地址和部署区块 `load`。ZZL 的重入挑战是独立加分项，不能混用主业务 Escrow 或交易。

| 成员 | 当前负责 | 只能由本人完成的签名 |
| --- | --- | --- |
| DHT（买家） | 已交付 Escrow 合约、Java 封装与核心 Service；已创建两单、授权并付款；等待 YZH 仲裁退款后确认正常单；买家截图和交易哈希已归档 | 买家钱包 `createOrder`、`approve`、`deposit`、`confirm` |
| YZH（组长、卖家） | 已交付 Controller、事件查询、时间线、集成、SimpleStorage 和新 Escrow 部署；已完成订单 1 争议及退款、订单 0 失败仲裁验证；列表查询优化和重启 load 已实测，继续补报告截图与正常单完成后的时间线 | 卖家钱包 `dispute`；后端专用账户在本人本机签名 `deploy`、`resolve` |
| ZZL（验收） | 建立交易表和截图索引，逐笔核对公开回执；独立完成重入挑战、修复复测和本人报告材料 | 仅自己的挑战合约交易；不代替 DHT/YZH 主业务签名 |

## 当前阶段：订单 1 仲裁退款已核验，补证、正常单确认和时间线待完成

| 阶段 | 负责人 | 已核实结果 / 尚需结果 | 证据与下一位 |
| --- | --- | --- | --- |
| 1. 代码集成及本地验证 | DHT、YZH | Escrow、YZHToken、IERC20 封装与 `EscrowService` 已并入主分支；2026-10-22 完整编译、封装生成和本地 EVM 测试通过（5 项）。本地测试不冒充测试网交易。 | [DHT 验证记录](dht-verification.md)；YZH 接收后进入链上准备 |
| 2. 账户与代币准备 | YZH | 后端账户获 0.0001 POT；YZH 从**正确代币合约**给 DHT 转 60 枚，转后读到 DHT 60、YZH 10。DHT 操作前仍须复查 POT 和代币余额。 | 充值哈希、转币哈希见 [公开配置](project-config.md)；DHT 接手 |
| 3. SimpleStorage 示例 | YZH | `deploy → set(42) → get=42` 已在测试网完成；set 交易 `0x0563cf0ddcbbc739071ffa15c07e76b0b13cd8b4cacc72cbb3f88c5aff835584` 成功，区块 `2672663`。 | `evidence/4A/4A-3-01*`、`4A-3-02*`；用于报告 4A-3 |
| 4. 新 Escrow 部署 | YZH | 新地址 `0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5`，部署哈希 `0x4f61670afea0a4714f89c02e177f14b7d2737fa1cad9cac797727344ec187372`，区块 `2672683`，回执 `0x1`；链上 arbiter 与后端专用账户 `0x6717496Fe22477B41e6Ca07353C308fF38A61797` 一致。**不再 deploy。** | `evidence/4A/4A-5-01*`；DHT、ZZL 使用该公开地址 |
| 5. 两单创建、授权、付款 | DHT | **2026-10-26 已完成。** 正常单 id=0、争议单 id=1，每单 30 枚；approve 共 60 枚；两笔 deposit 成功，截图中两单 status=1（Paid）。五笔交易均已只读查询 RPC 回执核对成功。 | 10 张原图副本已归档到 evidence/4A/；见下方 2026-10-26 更新、交易表和截图索引；YZH 接手争议 |
| 6. 争议、列表、失败与成功仲裁 | YZH，ZZL 核对 | **争议、失败仲裁及退款已核验。** 订单 1 的 dispute 成功；订单 0 的 resolve 回执为 `0x0`；订单 1 的 `resolve(false)` 回执为 `0x1`，含向 DHT 转出 30 枚指定代币的日志。优化后的 `GET /orders` 约 40 秒返回两单，退款前 Paid/Disputed 详情与回执 JSON 已归档；4A-6/7/8 的 PNG 截图仍待补齐。 | YZH 交 4A-5/6/7，ZZL 截 4A-8 并登记实际失败类型和成功交易 |
| 7. 正常完成、时间线、重启恢复 | DHT、YZH、ZZL | **尚未核验完成。** 服务重启并按原地址/区块 load 已复查；争议单四事件 T-2 JSON 已归档，正常单确认前只有创建和付款两事件。DHT 核对退款到账后确认正常单 0，并提供真实回执；YZH 补完整 T-1、T-3 与截图，ZZL 核对浏览器 Logs。 | 4A-S1/S2、T-1/T-2/T-3 与交易索引；YZH 最终汇总 |
| 8. 独立重入挑战与报告 | ZZL、三人 | ZZL 挑战和最终报告证据**未在本仓库核实**；与主业务可并行。 | ZZL 交 4C-1/2/3、报告片段；三人核对贡献比例和 AI 使用后由 YZH 制作报告副本 |

## DHT 已完成阶段的操作参考 请勿重复创建或付款

1. 更新到最新仓库，核对 POTOS Testnet `chainId=60600`、本人买家地址 `0x5FeDaeB4369432aD2A9929EaFFA9762C46050d4C`、指定 YZHToken `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee` 的余额至少 60，且有足够 POT。钱包里另一个同名代币不能代替。
2. 在 Remix 使用本仓库 `src/main/resources/contracts/Escrow.sol`，以 **At Address** 附加阶段 4 的新 Escrow，不再次部署。读取 `arbiter` 和 `orderCount`；订单 id 以实际链上结果为准。
3. 用买家钱包创建两单，各 `amount=30000000000000000000`（18 位精度），`seller=0xa508c9Ff372C40F1f109EdA1F8DB532fB556c30A`，`token` 为上述指定合约。`deadline` 在操作当时计算未来约 30 分钟的 Unix 秒，description 区分正常单/争议单。逐笔记录哈希、区块、id。
4. 在指定 YZHToken 上对**新 Escrow** `approve(60000000000000000000)`，读取 `allowance`，再分别 `deposit` 两单并核对 Paid。完成后把哈希、id 和截图交 YZH/ZZL；**等争议与退款完成后才 confirm 正常单**。

当前 DHT 已完成上述创建、授权和两笔付款，证据见文末；不要重复上述发送步骤。YZH 已完成订单 1 争议及仲裁退款；DHT 接下来核对退款到账，按主流程交接安排确认正常单 0 并保留回执。

## ZZL 与 YZH 的并行工作

- ZZL：在自己的分支准备 `docs/transactions.md`、`docs/evidence-index.md` 与独立重入挑战；公开交易经核对后再填表，不预写未知订单 id、哈希或结论。后端只监听 YZH 本机 `127.0.0.1:8080`，需要接口截图时由 YZH 提供原图，ZZL 核对并登记。
- YZH：已完成订单 1 争议与仲裁退款、订单 0 失败仲裁验证；继续依 [实验执行记录](experiment-runbook.md) 完成列表查询性能验证、详情和仲裁截图、时间线及重启 load 复查。后端专用账户私钥只在本人本机环境变量中使用，不在聊天、微信、仓库或截图中传递。

ZZL 的独立加分挑战放在 `bonus/reentrancy/`：只在本组自建合约上演示 `VulnerableVault`（先外部转账后改余额）、有明确余额判断和次数上限的 `Attacker`、以及先改余额后转账的 `FixedVault`。课程指导示例为受害者本人存 `0.002 POT`、攻击者存 `0.001 POT`；若测试币不足，如实记录实际金额并重算预期。受害者交易由其本人签名。比较漏洞版攻击前后金库/攻击合约余额、真实攻击回执与浏览器 Internal Transactions；若浏览器未索引内部交易，保存真实空列表并用余额或日志补证。修复版需在新部署的 FixedVault 上用同一攻击思路复测，不能故意让攻击函数自行报错。保存 4C-1 至 4C-3，提交复现步骤、报告片段和遇到的真实问题。

## 代码边界与协作

`EscrowService implements EscrowGateway` 提供 `deploy/load/current/getOrder/resolve`；`current()` 是事件查询获取合约地址与部署区块的唯一来源。YZH 维护 Controller 与事件查询；DHT 不另建 Controller，ZZL 验收脚本不使用他人私钥。独立改代码时从最新 `main` 建分支，PR 写明修改、实际测试和待办；YZH 审查合并。主业务只用上述新 Escrow，重启时 `load(address, fromBlock=2672683)`，不重复部署。

## 每完成一阶段怎样追加记录

把以下记录追加在本文件末尾；已完成阶段的原始记录保留，修正时另写“更正”。**每张已完成的截图保留两份**：原图先保存在 YZH 桌面 `大三上/区块链/大作业/` 或实际操作者本机；按 [截图指南](evidence-capture-guide.md) 命名后，复制一份到仓库对应的 `evidence/prep/`、`evidence/4A/`、`evidence/4C/` 或 `evidence/timeline/`。在下方记录中写明仓库截图路径和核对结果，并将文档与截图一起提交、推送，供其他组员查看；未推送的本地文件不会出现在 GitHub。没有哈希的发送前失败写“无交易哈希”。

```text
日期时间（Asia/Shanghai）：
阶段 / 操作人：
实际操作与输入：
公开账户 / 合约 / 订单 id：
交易哈希、区块、回执状态（若发生交易）：
核对结果和截图文件名：
未完成 / 实际错误：
下一位操作者与下一步：
```

## 阶段更新记录

- 2026-10-22，YZH：已核对新 Escrow 部署回执与 `/api/escrow/address`，阶段 4 完成；交 DHT 在新 Escrow 创建两单、批准与付款，交 ZZL 跟踪公开交易。阶段 5 结果待 DHT 本人补入。


### 2026-10-26 DHT 买家创建授权付款完成并归档

以下保留 DHT 交付时点的历史状态；后续进度以顶部阶段表及文末更新为准。

- 操作人：DHT；核验时间（Asia/Shanghai）：2026-10-26T15:40:37.376339+08:00。交易由 DHT 本人通过 MetaMask 签名；Codex 只读查询公开 RPC 核验并归档截图，没有代签交易。
- 本次 Escrow：`0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5`；代币：`0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee`；买卖家沿用公开配置。
- 实际创建正常单 0、争议单 1；每单金额 `30000000000000000000`，批准额度 `60000000000000000000`。付款后原始 getOrder 截图中两单 status 均为 1（Paid）。这是截图时点的状态，不预断后续链上状态。

| 操作 | 交易哈希 | 区块 | 回执 | gasUsed |
| --- | --- | --- | --- | --- |
| createOrder(0) 正常订单 | `0x782a3aa446b05827ba109dbd3502edec6b60f2b93d72488011e4e6d55f6524b6` | 2688642 | 0x1 | 80531 |
| createOrder(1) 争议订单 | `0xd2246b70a7f676b33c70fcf31f2f25baf5c1d0fd61e1ef9f111f921e2555a333` | 2688698 | 0x1 | 80531 |
| approve 新 Escrow 共 60 枚 | `0x279498c462501f23a680cacaf7b82a68771a47456a3c8cce1a43e5032d3f1014` | 2688760 | 0x1 | 28423 |
| deposit(0) 付款 30 枚 | `0xe4e6c4089b9dde7fef35832cd2020f649ac5cad5842ccdc2df080f28d8247b46` | 2688806 | 0x1 | 70217 |
| deposit(1) 付款 30 枚 | `0xdcfa559d8fed107287dbab3d29e6efd1b09872c0932f6d6d7371b35423287db7` | 2688820 | 0x1 | 70217 |

- 原图保留在 DHT 本机 `微众银行实验/大作业/screenshots/`，未移动或改写；逐字节一致的仓库副本为：

- [`4A-5-02_DHT_两单创建_1.png`](../evidence/4A/4A-5-02_DHT_两单创建_1.png)
- [`4A-5-02_DHT_两单创建_2.png`](../evidence/4A/4A-5-02_DHT_两单创建_2.png)
- [`4A-5-02_DHT_两单创建_3.png`](../evidence/4A/4A-5-02_DHT_两单创建_3.png)
- [`4A-5-02_DHT_两单创建_4.png`](../evidence/4A/4A-5-02_DHT_两单创建_4.png)
- [`4A-5-03_DHT_approve新Escrow_1.png`](../evidence/4A/4A-5-03_DHT_approve新Escrow_1.png)
- [`4A-5-03_DHT_approve新Escrow_2.png`](../evidence/4A/4A-5-03_DHT_approve新Escrow_2.png)
- [`4A-5-04_DHT_两单deposit_1.png`](../evidence/4A/4A-5-04_DHT_两单deposit_1.png)
- [`4A-5-04_DHT_两单deposit_2.png`](../evidence/4A/4A-5-04_DHT_两单deposit_2.png)
- [`4A-5-04_DHT_两单deposit_3_1.png`](../evidence/4A/4A-5-04_DHT_两单deposit_3_1.png)
- [`4A-5-04_DHT_两单deposit_3_2.png`](../evidence/4A/4A-5-04_DHT_两单deposit_3_2.png)

- 详细索引：[截图索引](evidence-index.md)；浏览器链接及交易表：[交易记录](transactions.md)。截图不含私钥、助记词或访问令牌；参数图与对应回执图分开保留，不拼接或改写画面。
- 尚未完成：YZH 的 dispute(1)、后端列表/详情、resolve(0,true) 失败验收、resolve(1,false) 退款、DHT confirm(0)、最终余额及时间线、重启复查。没有预填这些交易的哈希或结果。DHT 的 4A-1、4A-2、4A-4 代码截图也仍需补齐。
- 下一位：YZH 用卖家钱包 dispute(1)，取得 Paid/Disputed 查询证据，再完成未争议单失败裁决及争议单退款。ZZL 核对回执并补浏览器证据。DHT 等退款完成后再核对退款及 confirm(0)。无需重新 deploy、createOrder、approve 或 deposit。


### 2026-10-26 YZH 接收 DHT PR 并更新后续进度

- 已审阅并合并 [DHT PR #2](https://github.com/yzi030208-ship-it/blockchain-capstone-yzh-backend/pull/2)，合并提交 `857236440cb445b1ac75395c1d5cc61c0b77a161`。已逐张检查 10 张截图，创建、授权、付款参数及对应关系与文档一致，未见私钥；五笔公开交易回执重新通过 RPC 核验，状态均为 `0x1`，区块、gasUsed、From/To 与 DHT 交易表一致。本 PR 只涉及截图和文档，未重跑代码测试；GitHub 未返回提交状态检查记录，不能写作 CI 已通过。
- 后续真实记录：卖家争议交易 `0xe394176ba8ee598af750fc5cf9528dd1062bbf365cf3728df9ac0a7027e4db91`，区块 `2689118`，回执 `0x1`；订单 0 的失败仲裁 `0x07c6c39d408ad1d599cb4b2e1039f4630f2c5d12dd052e92032111e169aab3ed`，区块 `2689253`，回执 `0x0`；订单 1 的退款仲裁 `0x86b5d80b974abd153f5b40a4e08f72f0d5657bf1fd048289eb621878fbbf9a92`，区块 `2689270`，回执 `0x1`，含指定 ERC20 向 DHT 退款 30 枚及 `OrderResolved(false)` 日志。这些为已核验的后续进度，不重复 dispute 或 resolve。
- YZH 另一个本地任务仍在处理列表扫描耗时、截图和时间线；其本地代码、截图和回执文件尚未随本次 PR 发布。此次远端合并未改动该任务的本地工作文件。不能把 JSON 回执文件当成报告所需的界面截图，也不能将待完成的列表、时间线或重启恢复标为通过。
- 下一位：DHT 核对退款余额，按主流程交接安排确认订单 0 并交真实回执；ZZL 保存成功仲裁浏览器 4A-8、补交易表与截图索引；YZH 完成列表优化验证、4A-6/7 及 T-1 至 T-3。订单 0 的确认回执尚未在本次核验材料中取得。

### 2026-10-26 YZH 后端列表、时间线与证据更新

- 卖家订单 1 争议前后的真实 Remix 原图及仓库副本：[争议前 Paid](../evidence/4A/4A-5-05a_YZH_订单1争议前Paid.png)、[争议成功和 Disputed](../evidence/4A/4A-5-05_YZH_卖家dispute成功_订单1Disputed.png)。后端恢复加载原 Escrow 的截图见 [4A-5-01c](../evidence/4A/4A-5-01c_YZH_原Escrow恢复加载核对.png)。卖家争议完整哈希 `0xe394176ba8ee598af750fc5cf9528dd1062bbf365cf3728df9ac0a7027e4db91`，区块 `2689118`，回执 `0x1`。
- 退款前保存后端实际响应：[订单 0 Paid](../evidence/4A/4A-6-02a_YZH_订单0_Paid.json)、[订单 1 Disputed](../evidence/4A/4A-6-02b_YZH_订单1_Disputed.json)。`resolve(0,true)` 真实失败回执 `0x07c6c39d408ad1d599cb4b2e1039f4630f2c5d12dd052e92032111e169aab3ed`，区块 `2689253`，`status=0x0`、gasUsed `25889`，后端 HTTP 502，见[失败回执 JSON](../evidence/4A/4A-7-01_YZH_未争议resolve失败回执.json)；订单 0 保持 Paid。`resolve(1,false)` 成功哈希 `0x86b5d80b974abd153f5b40a4e08f72f0d5657bf1fd048289eb621878fbbf9a92`，区块 `2689270`，`status=0x1`，ERC20 日志显示新 Escrow 向 DHT 退 30 枚，见[成功回执 JSON](../evidence/4A/4A-7-02_YZH_争议订单退款成功回执.json)与[订单 1 Refunded 响应](../evidence/4A/4A-7-03_YZH_订单1_Refunded.json)。指定代币 `balanceOf(DHT)` 读到退款后余额 30 枚；DHT 本人仍需截图核对。
- 10 区块顺序扫链曾超过 240 秒未返回。YZH 改为最多 8 个窗口并发，每个窗口仍最多 10 区块；原有事件测试通过。重启后以原地址和部署区块 `2672683` 成功 `load`，`GET /api/escrow/orders` 约 40 秒返回 HTTP 200 和[两笔真实订单](../evidence/4A/4A-6-01_YZH_订单列表.json)。[争议单 T-2](../evidence/timeline/T-2_YZH_争议单时间线.json)包含创建、付款、争议、`OrderResolved(false)` 四事件，不虚构 `OrderRefunded` 日志。[正常单确认前时间线](../evidence/timeline/T-1_YZH_正常单确认前时间线.json)只有创建、付款两事件；DHT `confirm(0)` 尚未发生。
- 以上 JSON 是真实后端/RPC 响应存档，**不是报告要求的 PNG 截图**。YZH/ZZL 仍须补 4A-6/7、T-1/2/3 可读画面和 4A-8 浏览器详情；DHT 核对买家退款余额后，再按交接确认订单 0，并保存正常完成回执。

- T-3 的实际跨十区块数量、成功退款的后端事件与 RPC Logs 对照、以及重启后原地址 `load` 响应已整理在 [T-3 核对记录](../evidence/timeline/T-3_YZH_跨十区块与链上日志核对.md) 和 [load JSON](../evidence/timeline/T-3_YZH_重启load原Escrow.json)。浏览器画面及报告 PNG 仍待补。


### 2026-10-26 DHT 退款到账核对及代码截图归档

- DHT 本人通过 Remix 查询指定 YZHToken 的 balanceOf，截图返回 `30000000000000000000`，即 30 枚；getOrder(1) 为 `status=3`（Refunded），getOrder(0) 仍为 `status=1`（Paid）。这三项为所提供截图时点的核对结果，未新发送退款、仲裁或确认交易。
- 退款与状态原图：[买家余额](../evidence/4A/4A-S2-02_DHT_买家退款_1.png)、[订单 1 Refunded](../evidence/4A/4A-S2-02_DHT_买家退款_2.png)、[订单 0 确认前 Paid](../evidence/4A/4A-S1_DHT_正常单确认前Paid.png)。本地原图仍保存在 DHT 的 `微众银行实验/大作业/screenshots/`。
- 同时归档 DHT 的 4A-1 配置 1 张、4A-2 封装 5 张、4A-4 Service 4 张。Service 四张连续覆盖第 1—180 行，不要求为凑数再截六张；封装事件定义第 72 行右侧仍建议补充清晰完整参数图。完整文件及核对状态见 [截图索引](evidence-index.md)。
- 保存与推送范围：13 张副本全部位于 `evidence/4A/`，与原图逐字节一致；代码截图指南和可复现生成脚本一并交付。未修改合约或 Java 业务源码。
- **当前保持暂停正常单确认。** 按 YZH 最新直接交接消息，DHT 先核对到账并归档，等 YZH 主任务明确已保存所需当前状态证据、通知可以确认后，DHT 才执行 confirm(0)。本记录不自动授权该步骤，也不以退款成功替代放行消息。
- 下一位：YZH/ZZL 核对本次截图，继续保存后端状态、仲裁浏览器 4A-8 等证据；YZH 明确放行后由 DHT 本人签名确认并追加成功回执与 Completed 截图。不得重复 deposit、dispute、resolve 或重新部署。
