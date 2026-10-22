# 三人共用阶段交接记录

本文件是 DHT、YZH、ZZL **唯一持续更新的交接文档**。每完成一个阶段，由执行人按文末模板追加真实结果，YZH 核对后标明下一位操作者。不要覆盖旧记录、预填未发生的交易，或在文件与截图中写入私钥。当前链上公开参数统一查 [project-config.md](project-config.md)；详细操作与交易留痕查 [experiment-runbook.md](experiment-runbook.md)；截图编号查 [evidence-capture-guide.md](evidence-capture-guide.md)。

## 项目和职责

路线 A 使用 Java 21、Spring Boot 3.2.3、Web3j 4.14.0 与 POTOS Testnet，演示新 Escrow 的两笔 30 枚 YZHToken 订单；正常单完成，争议单由后端仲裁退款。订单列表和时间线须由真实链上事件查询，重启后使用原地址和部署区块 `load`。ZZL 的重入挑战是独立加分项，不能混用主业务 Escrow 或交易。

| 成员 | 当前负责 | 只能由本人完成的签名 |
| --- | --- | --- |
| DHT（买家） | 已交付 Escrow 合约、Java 封装与核心 Service；下一阶段创建两单、授权、付款，最后确认正常单；交买家截图和交易哈希 | 买家钱包 `createOrder`、`approve`、`deposit`、`confirm` |
| YZH（组长、卖家） | 已交付 Controller、事件查询、时间线、集成、SimpleStorage 和新 Escrow 部署；下一阶段在争议单付款后 `dispute`，再做后端查询、仲裁、重启复查和报告 | 卖家钱包 `dispute`；后端专用账户在本人本机签名 `deploy`、`resolve` |
| ZZL（验收） | 建立交易表和截图索引，逐笔核对公开回执；独立完成重入挑战、修复复测和本人报告材料 | 仅自己的挑战合约交易；不代替 DHT/YZH 主业务签名 |

## 当前阶段：等待 DHT 买家创建和付款

| 阶段 | 负责人 | 已核实结果 / 尚需结果 | 证据与下一位 |
| --- | --- | --- | --- |
| 1. 代码集成及本地验证 | DHT、YZH | Escrow、YZHToken、IERC20 封装与 `EscrowService` 已并入主分支；2026-10-22 完整编译、封装生成和本地 EVM 测试通过（5 项）。本地测试不冒充测试网交易。 | [DHT 验证记录](dht-verification.md)；YZH 接收后进入链上准备 |
| 2. 账户与代币准备 | YZH | 后端账户获 0.0001 POT；YZH 从**正确代币合约**给 DHT 转 60 枚，转后读到 DHT 60、YZH 10。DHT 操作前仍须复查 POT 和代币余额。 | 充值哈希、转币哈希见 [公开配置](project-config.md)；DHT 接手 |
| 3. SimpleStorage 示例 | YZH | `deploy → set(42) → get=42` 已在测试网完成；set 交易 `0x0563cf0ddcbbc739071ffa15c07e76b0b13cd8b4cacc72cbb3f88c5aff835584` 成功，区块 `2672663`。 | `evidence/4A/4A-3-01*`、`4A-3-02*`；用于报告 4A-3 |
| 4. 新 Escrow 部署 | YZH | 新地址 `0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5`，部署哈希 `0x4f61670afea0a4714f89c02e177f14b7d2737fa1cad9cac797727344ec187372`，区块 `2672683`，回执 `0x1`；链上 arbiter 与后端专用账户 `0x6717496Fe22477B41e6Ca07353C308fF38A61797` 一致。**不再 deploy。** | `evidence/4A/4A-5-01*`；DHT、ZZL 使用该公开地址 |
| 5. 两单创建、授权、付款 | DHT | **待本人实测。** 本次 Escrow 的订单 id、创建/approve/deposit 哈希均待取得。 | DHT 交每笔真实哈希、区块、两单 id 和 4A-5 截图给 YZH/ZZL；付款后 YZH 接手争议 |
| 6. 争议、列表、失败与成功仲裁 | YZH，ZZL 核对 | **待两单付款。** YZH 卖家争议一单；后端查询 Paid/Disputed；未争议单 resolve 失败、争议单 `sellerWins=false` 成功并退款。 | YZH 交 4A-5/6/7，ZZL 截 4A-8 并登记实际失败类型和成功交易 |
| 7. 正常完成、时间线、重启恢复 | DHT、YZH、ZZL | **待阶段 6。** DHT 确认正常单；YZH 查两单时间线和余额，服务重启后用原地址/区块 load 复查；ZZL 核对浏览器 Logs。 | 4A-S1/S2、T-1/T-2/T-3 与交易索引；YZH 最终汇总 |
| 8. 独立重入挑战与报告 | ZZL、三人 | ZZL 挑战和最终报告证据**未在本仓库核实**；与主业务可并行。 | ZZL 交 4C-1/2/3、报告片段；三人核对贡献比例和 AI 使用后由 YZH 制作报告副本 |

## 现在由 DHT 做的具体操作

1. 更新到最新仓库，核对 POTOS Testnet `chainId=60600`、本人买家地址 `0x5FeDaeB4369432aD2A9929EaFFA9762C46050d4C`、指定 YZHToken `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee` 的余额至少 60，且有足够 POT。钱包里另一个同名代币不能代替。
2. 在 Remix 使用本仓库 `src/main/resources/contracts/Escrow.sol`，以 **At Address** 附加阶段 4 的新 Escrow，不再次部署。读取 `arbiter` 和 `orderCount`；订单 id 以实际链上结果为准。
3. 用买家钱包创建两单，各 `amount=30000000000000000000`（18 位精度），`seller=0xa508c9Ff372C40F1f109EdA1F8DB532fB556c30A`，`token` 为上述指定合约。`deadline` 在操作当时计算未来约 30 分钟的 Unix 秒，description 区分正常单/争议单。逐笔记录哈希、区块、id。
4. 在指定 YZHToken 上对**新 Escrow** `approve(60000000000000000000)`，读取 `allowance`，再分别 `deposit` 两单并核对 Paid。完成后把哈希、id 和截图交 YZH/ZZL；**等争议与退款完成后才 confirm 正常单**。

## ZZL 与 YZH 的并行工作

- ZZL：在自己的分支准备 `docs/transactions.md`、`docs/evidence-index.md` 与独立重入挑战；公开交易经核对后再填表，不预写未知订单 id、哈希或结论。后端只监听 YZH 本机 `127.0.0.1:8080`，需要接口截图时由 YZH 提供原图，ZZL 核对并登记。
- YZH：保持当前后端运行，收到 DHT 两笔 Paid 的证据后再使用卖家钱包 `dispute(争议单 id)`。后续依 [实验执行记录](experiment-runbook.md) 做列表、详情、失败/成功仲裁与时间线。后端专用账户私钥只在本人本机环境变量中使用，不在聊天、微信、仓库或截图中传递。

ZZL 的独立加分挑战放在 `bonus/reentrancy/`：只在本组自建合约上演示 `VulnerableVault`（先外部转账后改余额）、有明确余额判断和次数上限的 `Attacker`、以及先改余额后转账的 `FixedVault`。课程指导示例为受害者本人存 `0.002 POT`、攻击者存 `0.001 POT`；若测试币不足，如实记录实际金额并重算预期。受害者交易由其本人签名。比较漏洞版攻击前后金库/攻击合约余额、真实攻击回执与浏览器 Internal Transactions；若浏览器未索引内部交易，保存真实空列表并用余额或日志补证。修复版需在新部署的 FixedVault 上用同一攻击思路复测，不能故意让攻击函数自行报错。保存 4C-1 至 4C-3，提交复现步骤、报告片段和遇到的真实问题。

## 代码边界与协作

`EscrowService implements EscrowGateway` 提供 `deploy/load/current/getOrder/resolve`；`current()` 是事件查询获取合约地址与部署区块的唯一来源。YZH 维护 Controller 与事件查询；DHT 不另建 Controller，ZZL 验收脚本不使用他人私钥。独立改代码时从最新 `main` 建分支，PR 写明修改、实际测试和待办；YZH 审查合并。主业务只用上述新 Escrow，重启时 `load(address, fromBlock=2672683)`，不重复部署。

## 每完成一阶段怎样追加记录

把以下记录追加在本文件末尾；已完成阶段的原始记录保留，修正时另写“更正”。截图原图先保存在 YZH 桌面 `大三上/区块链/大作业/` 或操作者本机，再按 [截图指南](evidence-capture-guide.md) 归档到仓库 `evidence/`。没有哈希的发送前失败写“无交易哈希”。

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
