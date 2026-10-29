# 三人共用阶段交接记录

本文件是 DHT、YZH、ZZL **唯一持续更新的交接文档**。每完成一个阶段，由执行人按文末模板追加真实结果，YZH 核对后标明下一位操作者。不要覆盖旧记录、预填未发生的交易，或在文件与截图中写入私钥。当前链上公开参数统一查 [project-config.md](project-config.md)；详细操作与交易留痕查 [experiment-runbook.md](experiment-runbook.md)；截图编号查 [evidence-capture-guide.md](evidence-capture-guide.md)。

## 项目和职责

路线 A 使用 Java 21、Spring Boot 3.2.3、Web3j 4.14.0 与 POTOS Testnet，演示新 Escrow 的两笔 30 枚 YZHToken 订单；正常单完成，争议单由后端仲裁退款。订单列表和时间线须由真实链上事件查询，重启后使用原地址和部署区块 `load`。ZZL 的重入挑战是独立加分项，不能混用主业务 Escrow 或交易。

| 成员 | 当前负责 | 只能由本人完成的签名 |
| --- | --- | --- |
| DHT（买家） | 已交付 Escrow 合约、Java 封装与核心 Service；两单创建、授权、付款和正常单确认均已完成；不需重复 confirm | 买家钱包 `createOrder`、`approve`、`deposit`、`confirm` |
| YZH（组长、卖家） | 已交付 Controller、事件查询、时间线、集成及链上业务；已有时间线代码和截图供 ZZL 本轮复用，具体见 timeline-delivery.md | 卖家钱包 `dispute`；后端专用账户在本人本机签名 `deploy`、`resolve` |
| ZZL（验收） | 本轮一人接手时间线验收、补图、归档及报告 4.5；独立重入挑战沿用原分工 | 仅自己的挑战合约交易；不代替 DHT/YZH 主业务签名 |

## 当前阶段：订单时间线验收及主要截图已完成，待后续正式报告整合

| 阶段 | 负责人 | 已核实结果 / 尚需结果 | 证据与下一位 |
| --- | --- | --- | --- |
| 1. 代码集成及本地验证 | DHT、YZH | Escrow、YZHToken、IERC20 封装与 `EscrowService` 已并入主分支；2026-10-22 完整编译、封装生成和本地 EVM 测试通过（5 项）。本地测试不冒充测试网交易。 | [DHT 验证记录](dht-verification.md)；YZH 接收后进入链上准备 |
| 2. 账户与代币准备 | YZH | 后端账户获 0.0001 POT；YZH 从**正确代币合约**给 DHT 转 60 枚，转后读到 DHT 60、YZH 10。DHT 操作前仍须复查 POT 和代币余额。 | 充值哈希、转币哈希见 [公开配置](project-config.md)；DHT 接手 |
| 3. SimpleStorage 示例 | YZH | `deploy → set(42) → get=42` 已在测试网完成；set 交易 `0x0563cf0ddcbbc739071ffa15c07e76b0b13cd8b4cacc72cbb3f88c5aff835584` 成功，区块 `2672663`。 | `evidence/4A/4A-3-01*`、`4A-3-02*`；用于报告 4A-3 |
| 4. 新 Escrow 部署 | YZH | 新地址 `0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5`，部署哈希 `0x4f61670afea0a4714f89c02e177f14b7d2737fa1cad9cac797727344ec187372`，区块 `2672683`，回执 `0x1`；链上 arbiter 与后端专用账户 `0x6717496Fe22477B41e6Ca07353C308fF38A61797` 一致。**不再 deploy。** | `evidence/4A/4A-5-01*`；DHT、ZZL 使用该公开地址 |
| 5. 两单创建、授权、付款 | DHT | **2026-10-26 已完成。** 正常单 id=0、争议单 id=1，每单 30 枚；approve 共 60 枚；两笔 deposit 成功，截图中两单 status=1（Paid）。五笔交易均已只读查询 RPC 回执核对成功。 | 10 张原图副本已归档到 evidence/4A/；见下方 2026-10-26 更新、交易表和截图索引；YZH 接手争议 |
| 6. 争议、列表、失败与成功仲裁 | YZH，ZZL 核对 | **争议、失败仲裁及退款已核验。** 订单 1 的 dispute 成功；订单 0 的 resolve 回执为 `0x0`；订单 1 的 `resolve(false)` 回执为 `0x1`，含向 DHT 转出 30 枚指定代币的日志。4A-6/7 PNG 和旧副本中 ZZL 的 4A-8 详情、Transfer 日志图均已归档。 | 4A-8 原图已保留并目视核对；无需重做仲裁 |
| 7. 正常完成、时间线、重启恢复 | ZZL 本次收尾；YZH/DHT 原贡献保留 | **两单最终状态与时间线已核验。** 正常单 Completed、争议单 Refunded，T-1/T-2 原图、JSON 及旧 load/代码图已归档。本轮八项 Java 测试通过、一项原本地 EVM 测试跳过，七条事件通过实时回执与十区块窗口只读核验。本轮未重启后端。 | [时间线交付说明](timeline-delivery.md)；ZZL 的 OrderResolved、Transfer、RPC 核验和 Java 构建截图已收齐；T-1/T-2 复用原图 |
| 8. 独立重入挑战与报告 | ZZL、三人 | ZZL 挑战和最终报告证据**未在本仓库核实**；与主业务可并行。 | ZZL 交 4C-1/2/3、报告片段；三人核对贡献比例和 AI 使用后由 YZH 制作报告副本 |

## DHT 已完成阶段的操作参考 请勿重复创建或付款

1. 更新到最新仓库，核对 POTOS Testnet `chainId=60600`、本人买家地址 `0x5FeDaeB4369432aD2A9929EaFFA9762C46050d4C`、指定 YZHToken `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee` 的余额至少 60，且有足够 POT。钱包里另一个同名代币不能代替。
2. 在 Remix 使用本仓库 `src/main/resources/contracts/Escrow.sol`，以 **At Address** 附加阶段 4 的新 Escrow，不再次部署。读取 `arbiter` 和 `orderCount`；订单 id 以实际链上结果为准。
3. 用买家钱包创建两单，各 `amount=30000000000000000000`（18 位精度），`seller=0xa508c9Ff372C40F1f109EdA1F8DB532fB556c30A`，`token` 为上述指定合约。`deadline` 在操作当时计算未来约 30 分钟的 Unix 秒，description 区分正常单/争议单。逐笔记录哈希、区块、id。
4. 在指定 YZHToken 上对**新 Escrow** `approve(60000000000000000000)`，读取 `allowance`，再分别 `deposit` 两单并核对 Paid。完成后把哈希、id 和截图交 YZH/ZZL；**等争议与退款完成后才 confirm 正常单**。

当前 DHT 已完成上述创建、授权、两笔付款及正常单 0 确认，证据见文末；YZH 已完成订单 1 争议及仲裁退款。以上仅保留历史操作参考，两单均已结束，不再重复发送这些交易。

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

### 2026-10-26 DHT 已签名确认正常单 0（更新以上暂停状态）

- DHT 本人提供了确认前 `getOrder(0)` 为 Paid(1)、`confirm(0)` 参数、Remix 成功回执与确认后 `getOrder(0)` 为 Completed(2) 的四张原始截图；副本见 [截图索引](evidence-index.md)。确认交易正确哈希为 `0x4d1ab17ee3ede2c696944a2ace7d0f8e195949cdb6928b5883b1b3c162cd6f46`，区块 `2689699`、From DHT `0x5FeDaeB4369432aD2A9929EaFFA9762C46050d4C`、To 新 Escrow `0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5`、gasUsed `62549`、两条日志。YZH 按区块独立查询到同一哈希，DHT 交接侧再次用课程 RPC 查询，回执均为 `0x1`。截图原图仍在 DHT 本机 `screenshots/`。
- 初次请求课程 RPC 曾超时；随后 YZH 按区块独立查询到正确哈希及成功回执 `0x1`，DHT 交接侧通过 `eth_getTransactionReceipt` 再次核对相同结果。交易浏览器链接和核对范围见 [交易记录](transactions.md)。此前“暂停确认”的记录是当时状态，现已由 DHT 本人实际完成，不应再指示他重复 confirm。
- 下一位：YZH 补卖家收款及 T-1 的 `OrderCompleted` 时间线；4A-6/7 PNG 已归档，ZZL 继续补 4A-8 浏览器图。订单 1 保持 Refunded，不再操作；DHT 不重复 confirm(0)。
- DHT 随后补拍了 [4A-2-02b OrderCreated 事件定义](../evidence/4A/4A-2-02b_DHT_OrderCreated事件定义.png)：67—74 行开启自动换行后，六个事件参数和 indexed 标记均完整可读，原图与仓库副本 SHA-256 一致。此前第 72 行右侧裁切的证据缺口现已补齐；截图仍为生成代码画面，不是新链上交易。

### 2026-10-26 YZH 4A-6/7 截图归档更新

- YZH 已把订单列表实时接口、退款前订单 0 Paid/订单 1 Disputed 响应存档，以及失败仲裁、成功退款、订单 1 Refunded 的七张原始截图同时保存到本机桌面“大作业”文件夹与仓库 `evidence/4A/`。逐张文件名和证明范围见[截图索引](evidence-index.md)。订单列表存档截图另作辅助材料；所有截图不含私钥。
- 4A-7 是已完成交易的回执和状态截图，不需要再次执行 `resolve`。订单 1 的退款回执 `status=0x1`，订单 1 详情 `Refunded`；截图保存时订单 0 尚为 Paid，此后 DHT 已确认完成，见上方 DHT 阶段记录。
- 后续：ZZL 用交易哈希补 4A-8 区块浏览器截图；YZH 更新 T-1 与卖家到账证据。DHT 已提供确认回执及订单 0 Completed 状态，不需重复操作。

### 2026-10-27 YZH 正常单完成、卖家余额及时间线证据

- DHT 已对订单 0 执行 `confirm(0)`。YZH 重新从课程 RPC 核对区块 `2689699` 的交易，正确哈希 `0x4d1ab17ee3ede2c696944a2ace7d0f8e195949cdb6928b5883b1b3c162cd6f46`、`status=0x1`、From DHT、To 本组 Escrow；ERC20 Transfer 日志显示 Escrow 向 YZH 转出 30 枚。PR #3 初稿曾误写末尾为 `cd8f46`，合并前已更正；不重复 `confirm(0)`。
- 后端当前 `getOrder(0)=Completed`，YZH 的[T-1 实时接口图](../evidence/timeline/T-1_YZH_正常单完整时间线_实时接口.png)显示 `OrderCreated → OrderPaid → OrderCompleted` 三事件；[完整响应](../evidence/timeline/T-1_YZH_正常单完整时间线.json)已留存。[T-2 实时接口图](../evidence/timeline/T-2_YZH_争议单完整时间线_实时接口.png)显示四事件和 `sellerWins=false`。
- YZH 对指定代币 `balanceOf` 的[卖家余额图](../evidence/4A/4A-S2-01_YZH_卖家指定代币余额40枚.png)为 40 枚；[此前已核对的 10 枚](project-config.md)加上此次 Transfer 的 30 枚。T-3 的[分批代码图](../evidence/timeline/T-3-02_YZH_十区块分批并发与排序去重代码.png)及[原合约 load 响应存档图](../evidence/timeline/T-3-01_YZH_重启load原Escrow响应存档.png)已保存。桌面“大作业”保留 YZH 原图，仓库 `evidence/` 保留副本。
- 下一位：ZZL 补 4A-8 和确认交易的区块浏览器 Logs 图；YZH 补完整 4A-4 截图并汇总最终报告。

### 2026-10-27 YZH 4A-4 代码截图补齐

- YZH 已交付 Controller 上下两图和 EventQuery 两图，桌面“大作业”与仓库 `evidence/4A/` 各保留一份；文件路径与代码范围见[截图索引](evidence-index.md)。分批并发、排序去重代码复用已有 T-3-02 图。
- 本轮 YZH 截图清单已完成，无需重复链上操作。本批四张代码图与索引、报告片段、交接记录统一提交。
- 后续：ZZL 完成 4A-8、浏览器 Logs 对照和本人挑战材料；YZH 收齐后三人共同核对并汇总最终报告。不能据此把整个项目标为已完成。


### 2026-10-29 从本机旧下载副本保留的 ZZL 记录

以下为旧副本中的历史记录，保留其原有时间与陈述，不代表本轮重新执行；正常单现已完成，以较新记录为准。

- 2026-10-26 14:59（Asia/Shanghai），ZZL：已阅读大作业要求、仓库、公开配置和截图指南；建立 `docs/transactions.md` 与 `docs/evidence-index.md`。已登记并核对当前已知的账户充值、指定代币转账、SimpleStorage 写入和新 Escrow 部署；SimpleStorage 部署哈希因接口未返回而明确记为未记录。已登记现有 4A-3、4A-5 截图及后续 4A/4C 编号、完整文件名和仓库目录。当前仍等待 DHT 提供第一笔创建订单的真实交易信息；收到后 ZZL 逐笔核对交易、订单和截图，再更新本文件与两张登记表。独立重入挑战和 `docs/report-parts/ZZL.md` 已标明待完成内容。因当前仓库副本没有 Git 元数据，分支提交、推送和 PR 待取得带 Git 的工作副本后执行。

- 2026-10-26 16:34（Asia/Shanghai），ZZL：已从 GitHub `main` 最新提交 `c5fb3e2a23457cf533a912609a318e717054c7df` 同步路线 A 新增证据文件。DHT 已完成订单 0/1 创建、approve、两笔 deposit；YZH 已完成 `dispute(1)`、订单 0 的链上失败 `resolve(0,true)`、订单 1 的成功退款 `resolve(1,false)`。已将真实交易与区块浏览器链接补入 `docs/transactions.md`，并将 DHT 原图、4A-6/4A-7 JSON、时间线 JSON 及 T-3 记录同步到 `evidence/`；4A-6/4A-7/4A-8 的报告 PNG 仍需补。成功退款哈希为 `0x86b5d80b974abd153f5b40a4e08f72f0d5657bf1fd048289eb621878fbbf9a92`，区块 `2689270`，回执 `0x1`，含 30 枚代币 Transfer 和 `OrderResolved(false)`。当前下一步是 ZZL 截 4A-8 浏览器详情/日志，DHT 核对退款余额后确认订单 0；独立 6.1 重入挑战仍单独进行，不混入路线 A 交易表。

- 2026-10-26 16:42（Asia/Shanghai），ZZL：已保存并核对 `4A-8-01_ZZL_resolve浏览器详情.png` 与 `4A-8-02_ZZL_resolve日志.png`。详情图显示成功状态、区块 `2689270`、后端账户 From、新 Escrow To 及 30 枚转给 DHT；日志图显示指定 YZHToken 的 Transfer 从 Escrow 到 DHT，原图与仓库副本 SHA-256 一致。4A-8 已完成。下一步交 DHT 核对退款后余额并由本人签名 `confirm(0)`；订单 1 不再操作。6.1 重入挑战仍是独立证据线。

- 2026-10-29（Asia/Shanghai），ZZL / 6.1 环境准备：三份独立教学合约已使用 solc 0.8.20、EVM Paris 编译通过。账户检查遇到 HTTP 403，复现后发现默认 ethers 请求失败，而加上 `User-Agent: ZZL-Course-Lab/1.0` 的请求成功。新增 `bonus/reentrancy/rpc-provider.cjs`，账户检查和部署脚本共用该配置；只读复测返回 `CHAIN_ID=60600`、`BALANCE_QUERY=OK`。本次未使用私钥、未发送交易；ZZL 本人账户余额仍待本人重新运行检查，部署及 4C-1 至 4C-3 实验截图待完成。本次修改仅在本机，尚未提交或推送。

- 2026-10-29（Asia/Shanghai），ZZL / 6.1 账户核对：本人终端返回 `CHAIN_ID=60600`、`ACCOUNT_CHECK=OK`；公开地址为 `0x28D4cD8504A89249087A5b5480e4960aF68d378d`。独立只读查询确认余额为 `0.000999947548489 POT`，不足课程规定的单次 `0.001 POT` 投入及手续费。下一步先补充本人测试账户 POT，再核对余额；尚未执行金库部署或漏洞复现实验，4C 正式截图仍待完成。

- 2026-10-29（Asia/Shanghai），ZZL / 6.1 参数与部署准备：ZZL 明确后续采用十分之一金额方案，DHT 存入 `0.0002 POT`、ZZL 投入 `0.0001 POT`，最终报告如实说明偏离指导金额。已更新截图指南和 ZZL 报告片段；部署脚本增加费用检查、账户核对、防重复部署记录以及公开 JSON 回执保存。使用公开地址只读执行 `--check`，返回链编号 60600、余额 `0.001999946177504 POT`、估算 gas 27392、部署费用上界 `0.000000000690291 POT`、`PREFLIGHT: OK`。未读取本人私钥或发送交易。下一步由 ZZL 在本人终端部署 VulnerableVault，取得真实地址和成功回执后保存 `4C-1-00_ZZL_漏洞金库部署.png`；DHT 存款需等新地址核对完成，暂不操作。修改尚未提交推送。


### 2026-10-29 ZZL 接手订单时间线收尾

- 已从 GitHub main a76515a 建立带 Git 的新工作副本，保留旧下载副本独有的挑战、截图和历史记录，未覆盖旧目录。
- 时间线功能在同步的主分支已实现。本轮补充自动验收：六事件解码、同区块排序、订单过滤、去重、removed 日志排除及 HTTP 参数和错误响应；8 项通过，1 项原本地 EVM 场景跳过。
- 22:52（Asia/Shanghai）七条存档事件逐条通过实时链上回执和十区块窗口核验；订单 0 Completed、订单 1 Refunded。原始结果见 evidence/timeline/verification/20261029T145204614030Z/。本轮未重启后端或发交易。
- 本项由 ZZL 一人整理，DHT 无需重复 confirm，YZH 无需新增操作。现有 YZH 的 T-1/T-2 可复用并保留截图人；建议 ZZL 补一张 Escrow OrderResolved 浏览器日志，现有 ZZL 日志图只拍到 Transfer。详细步骤及报告片段见 timeline-delivery.md。
- 本轮仍为本机修改，未提交或推送。


### 当前操作者身份更正

用户本人明确当前操作者为 ZZL。本轮时间线由 ZZL 独立收尾，原分工中误把当前用户当作 YZH 的计划已更正。YZH 原接口代码、原截图与 DHT 的合约、签名操作保留历史署名。截图补图可从公开浏览器完成；无需其他成员再次交易或提供私钥。


## ZZL 时间线浏览器补图已归档

本次收到 ZZL 原图两张：T-3-03_OrderResolved 与 T-3-04_退款Transfer，均原样保存在 evidence/timeline/，已比对 SHA256。OrderResolved 订单 1 的原始布尔 data=false，Transfer 为 30 枚退回买家。浏览器将布尔参数命名为 refundToBuyer，与本组源码 sellerWins 不同；报告按源码及原始 data、Transfer 解释。两图未含交易哈希，与已有 4A-8-01 及 RPC 回执组合使用。此前“待补 T-3-03”的记录由本记录更新；截图文件与详细核对说明见时间线索引。本轮未发送交易。


### ZZL 本人终端核验遇到 CMD 调用语法问题

收到原始截图并保存至 evidence/timeline/issues/T-I-01_ZZL_CMD误用PowerShell调用符.png。当前 CMD 不接受 PowerShell 的开头调用符 &，脚本尚未执行。已提供去掉 & 的 CMD 命令，等待用户执行并提供成功结果截图；本记录不标记核验完成。


## ZZL 本人链上核验已通过

ZZL 在本机 CMD 修正调用语法后，于 2026-10-29 23:20（Asia/Shanghai）实际运行 scripts/verify-timeline.py，RESULT=PASS，订单 0 Completed（3 事件）、订单 1 Refunded（4 事件）。原图 T-4-01 与本轮 verification/20261029T152047601888Z/ 原始结果已对应归档。本轮 chainHead=2728523；模式为存档 API 与实时回执核验，不声称本轮启动后端。CMD 问题已解决。下一步由 ZZL 运行 Java 测试并提供 BUILD SUCCESSFUL 截图。


## ZZL 时间线主要验收截图已收齐

ZZL 已提供 T-4-02 Java 构建成功原图，显示 BUILD SUCCESSFUL in 10s。独立读取其本次 23:26（Asia/Shanghai）生成的四份 JUnit XML：9 项中 8 通过、1 项原本地 EVM 场景跳过，0 失败、0 错误；本次 XML 单独存于 evidence/timeline/validation/ZZL-20261029T152619Z/，未覆盖先前验证。Solidity 编译与封装任务按原配置跳过，不将本图称为完整 Solidity/EVM 验证。

时间线已有 T-1/T-2 接口原图可复用，加上 ZZL 的 T-3-03、T-3-04、T-4-01、T-4-02，主要验收截图已齐。截图与原始数据均原样保存、登记哈希；CMD 语法问题已复测解决。下一步是后续将证据插入正式报告与核对组员信息，无需继续为本项重复交易或截图。当前改动仍在本机，未提交推送。
