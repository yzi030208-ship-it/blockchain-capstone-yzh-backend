# ZZL（成员 3）任务与验收

先阅读 [项目目标与协作](team-workflow.md)、[公开配置](project-config.md) 和 README。你负责**独立重入挑战及主业务验收证据**。DHT 写 Escrow 核心 Service；YZH 写 Controller、事件查询并管理后端账户。实验 3 的 ZZL 仲裁钱包不是这次主业务的 arbiter。

## 独立重入挑战（加分项）

- [ ] 在 `bonus/reentrancy/` 建独立代码和运行说明，记录工具、版本、合约地址及运行命令，不让挑战依赖或改动主后端构建。
- [ ] 按课程指导实现 `VulnerableVault`（先外部转账、后更新余额）、`Attacker`（构造时接收金库地址；`attack` 存款并提现；`receive` 有**明确余额判断及次数上限**）、`FixedVault`（先更新余额、后外部转账）。只测试本组自行部署、专用于课程演示的合约。
- [ ] 使用原生测试网 POT，和 YZH 代币/主业务 Escrow 分开。指导书目标为受害者本人存 `0.002 POT`、攻击账户存 `0.001 POT`；若测试币不足，记录实际金额和原因，再计算对应预期。受害者动作由其本人签名，不收集任何成员私钥。
- [ ] 取得漏洞版攻击前后余额、攻击回执及浏览器 Internal Transactions；浏览器若未索引则如实注明并用余额、日志或**明确标记为本地**的 trace 补证。区分攻击合约余额与扣除 gas 后的外部账户净收益。
- [ ] 在**新部署**的 FixedVault 上用相同攻击思路实测修复后的失败或受阻，核对受害者资金仍在；不能改攻击函数令其主动报错来伪造修复效果。提交可复现脚本、代码和解释（重入过程、CEI 修复、其他防御方式）。

## 主业务联合验收

先建 `scripts/acceptance/`（公开数据检查脚本或可执行步骤）、`docs/transactions.md`、`docs/evidence-index.md`、`docs/report-parts/ZZL.md`。新 Escrow 尚未部署时只写步骤和空表，不填假交易。脚本不使用买卖家或后端私钥；需要签名时提示钱包主人操作，再记录其公开哈希。

YZH 公布新 Escrow 地址后，按 [协作顺序](team-workflow.md)跟踪以下关口：

1. 后端 `deploy/address`：新地址、部署哈希和区块、链上 arbiter 与后端公开地址相等。
2. DHT 两单创建、对新 Escrow 的 `approve` 和两次 `deposit`；记录两单 id、Paid 状态和实际金额。
3. YZH 卖家钱包 `dispute` 争议单；用后端 `GET /api/escrow/orders` 和两笔详情核对 Paid/Disputed。
4. 由 YZH 的本地后端请求非争议单 `resolve`，记录是**发送前失败**还是**已打包失败**。前者无交易哈希，不能伪造 gas；后者按真实回执记录 gas 与付款账户。
5. 由 YZH 后端对争议单执行 `resolve(...,false)`，核对 Refunded 和 DHT 余额；DHT 对正常单 `confirm`，核对 Completed 和 YZH 余额。
6. YZH 两条事件时间线与浏览器 Logs 对照，重启后用新地址及**原部署区块** `load` 再查。ZZL 将截图编号、实际文件和交易表对应起来。

每个 `docs/transactions.md` 条目至少写：模块、操作、订单 id、发起公开地址、目标合约、真实交易哈希（若存在）、区块、回执状态、浏览器链接、截图编号。`docs/evidence-index.md` 标“待取得/已核对”，不把别人待发的截图当已收到。

## ZZL 负责的报告材料与截图

`docs/report-parts/ZZL.md` 写 4A-4 解释：失败阶段、是否有链上哈希与 gas、谁付 gas，以及无认证后端仲裁接口被滥用的影响；结论须基于本组实际响应/回执。另写重入挑战代码和余额对照、修复分析、实际问题与复测、AI 使用记录。

| 编号 | 要收集的实际证据 | 谁提供画面 |
|---|---|---|
| 4A-5 | 新 Escrow 部署与 arbiter、两单创建/批准/付款、卖家争议 | 后端 YZH、买家 DHT、卖家 YZH 各自截图；ZZL 汇总 |
| 4A-6 | 列表至少两单，以及 Paid、Disputed 的详情 | ZZL 截运行中的后端实际响应 |
| 4A-7 | 非争议裁决的实际失败、争议裁决退款成功和最终状态 | ZZL 配合运行后端的 YZH |
| 4A-8 | 成功仲裁的浏览器详情、From/To/状态/区块与 Logs 或代币转移 | ZZL |
| 4C-1 | 受害者存款回执、漏洞版攻击成功回执及前后余额 | ZZL；受害者本人提供其钱包画面 |
| 4C-2 | 攻击交易的 Internal Transactions；若未索引，保存空列表和补充余额证据 | ZZL |
| 4C-3 | FixedVault 修复代码、修复后攻击结果和受保护余额 | ZZL |

完成定义：挑战代码与脚本能复现，真实回执和余额可核对；主业务交易表与截图索引没有漏项；本人报告片段和 PR 已交给 YZH。缺少真实交易时明确标待验收，不编造学号、贡献比例、地址、哈希或截图。
