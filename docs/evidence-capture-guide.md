# 路线 A 大作业截图分工与归档

本清单供 DHT、YZH、ZZL 在真实验收时照着截图、命名和交接。依据老师《大作业实验指导》路线 A、重入挑战和《10-大作业报告模板（分组）》4.4、4.5；具体操作顺序见 [实验执行记录](experiment-runbook.md)。**4A-1 至 4A-8、4C-1 至 4C-3 是老师报告原编号**；`4A-PREP`、`4A-S1/S2`、`T-1/T-2/T-3` 是本组补充编号。下表文件名是统一约定，截图尚未产生时只是待办，不代表已完成。

## 保存位置和交接规则

- 本文档固定放 `docs/evidence-capture-guide.md`。**每张已完成的截图保留本地原图和仓库副本**：原图存于 YZH 桌面 `大三上/区块链/大作业/` 或实际操作者本机；按下表命名，复制到仓库根目录的 `evidence/prep/`、`evidence/4A/`、`evidence/4C/` 或 `evidence/timeline/`。目录在有真实文件时建立。DHT/ZZL 可以先私下交原图给 YZH，确认画面中无密钥后再放仓库。不要把微信压缩图当唯一原图。
- 完成一个阶段后，在 [三人共用交接](team-handoff.md) 登记截图在仓库中的路径及实际核对结果；将截图副本与交接记录一起提交、推送。只有推送后，其他组员才能在 GitHub 看到。尚未截图的项目继续标记“待完成”，不得用文件名代替证据。
- 操作钱包的人保存交易原始画面；YZH 在自己电脑运行后端并保存本机 API 响应；ZZL 核对公开交易并维护 `docs/transactions.md` 和 `docs/evidence-index.md`。若 ZZL 无法访问只监听 `127.0.0.1` 的 YZH 后端，由 YZH 截真实终端/API 响应，ZZL 负责核对和登记，不要求 ZZL 取得后端私钥。
- 每张至少能读清操作、合约或账户、输入/输出及必要的交易哈希。长代码或不同浏览器标签页可用同一主编号的 `-01`、`-02` 分图；报告图注保持主编号。截图不出现私钥、助记词、访问令牌或输入私钥的终端。
- 报告模板每个【截图】位置先放一张本组对应编号的主图和图注；同号的其他清晰补图紧跟其后，不删主编号。**保留老师注明不得修改的评分页**。老师示例中的地址、交易、订单号不是本组证据；订单 id 以本组新 Escrow 实际结果为准。

## 上链前准备：补充证据，不占老师 4A-5 编号

| 文件名（放 `evidence/prep/`） | 操作者 / 截图人 | 需要看见什么 | 当前状态 |
| --- | --- | --- | --- |
| `4A-PREP-01_YZH_正确代币余额.png` | YZH / YZH | 指定代币 `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee`、本人地址和转账前 `balanceOf`；另一个同名合约 `0x7640…25A6` 的 36 枚不能代替 | 聊天中已有前端画面，原图待入库 |
| `4A-PREP-02_YZH_转给DHT六十枚.png` | YZH / YZH | 正确代币 `transfer` 的接收地址、原始整数 `60000000000000000000`、成功回执与哈希 | 已链上核验：`0x698aa6302106bfe33a9a71ed0e421fead150ccf84b6519883cbfda2c20935f30`，区块 `2672655`；聊天截图原图待入库 |
| `4A-PREP-03_DHT_到账余额.png` | YZH 查询 / YZH；DHT 可补本人钱包 | 对指定代币调用 `balanceOf(DHT地址)`，结果至少 60 枚 | 链上已读取 60 枚；截图待保存 |
| `4A-PREP-04_YZH_后端账户POT准备.png` | YZH / YZH | 后端公开地址和测试网 POT 余额或充值回执；不显示私钥 | 充值哈希见实验执行记录；可选补图 |

`4A-PREP` 图可放报告 4.2 关键数据或 4A-5 前的“账户准备”说明，勿冒充新 Escrow 的部署或订单交易。

## 路线 A 必做截图：报告 4.4

| 报告编号与建议文件名（放 `evidence/4A/`） | 操作者 / 原图截图人 | 画面必须证明的内容 | 报告位置 |
| --- | --- | --- | --- |
| `4A-1-01_DHT_配置环境变量.png` | DHT / DHT | `application.properties` 中 RPC、gas、`${WEB3J_PRIVATE_KEY:}` 占位符；无密钥值 | 【截图 4A-1】 |
| `4A-2-01_DHT_封装生成成功.png`、`4A-2-02_DHT_封装结构.png` | DHT / DHT | 完整 Solidity 编译与 Java 封装生成成功、Escrow/YZHToken/IERC20 文件；`Escrow.java` 的 Order 字段和事件解码方法 | 【截图 4A-2】 |
| `4A-3-01_YZH_SimpleStorage部署写入读取42.png`、`4A-3-02_YZH_SimpleStorage写入42链上成功.png` | YZH 后端 / YZH | SimpleStorage `deploy`、`set`、`get` 的真实 curl 响应，get 等于本组实际设置值；set 交易浏览器页有成功状态和哈希 | 【截图 4A-3】 |
| `4A-4-01_DHT_EscrowService.png`、`4A-4-02_YZH_EscrowController.png`、`4A-4-03_YZH_EventQuery.png` | 各代码作者 / 各自 | 本组完整 Service、Controller 和事件查询实现；多图保持代码可读，不能截老师示例 TODO 当成实现 | 【截图 4A-4】；事件查询作补图 |
| `4A-5-01_YZH_后端部署新Escrow_地址哈希区块.png`、`4A-5-01b_YZH_新Escrow仲裁人核对.png` | YZH 后端 / YZH | `deploy` 返回新 Escrow 地址、部署哈希/区块；`address` 返回同一地址和链上 arbiter，等于后端公开地址 | 【截图 4A-5】起始 |
| `4A-5-02_DHT_两单创建.png` | DHT 买家 / DHT | 在**本次新 Escrow** 的两笔 30 代币订单、真实 id、买卖家及 token 地址、创建成功证据 | 【截图 4A-5】续 |
| `4A-5-03_DHT_approve新Escrow.png`、`4A-5-04_DHT_两单deposit.png` | DHT 买家 / DHT | 对新 Escrow 授权共 60 枚、allowance，以及两单付款回执或 Paid 状态；不要使用实验 3 旧 Escrow | 【截图 4A-5】续 |
| `4A-5-05_YZH_卖家dispute.png` | YZH 卖家钱包 / YZH | 争议单 `dispute(id)` 回执、卖家地址与订单 Disputed 状态 | 【截图 4A-5】末 |
| `4A-6-01_YZH_订单列表.png`、`4A-6-02_YZH_两单详情.png` | YZH 本机运行并提供原图；ZZL 负责本项验收与归档 | `GET /orders` 含至少两单，`GET /orders/{id}` 分别为 Paid、Disputed；id、地址、金额可对照 | 【截图 4A-6】 |
| `4A-7-01_YZH_未争议resolve失败.png`、`4A-7-02_YZH_争议resolve成功.png` | YZH 后端运行并提供原图；ZZL 负责本项验收与归档 | 非争议单的原始失败响应；争议单 `sellerWins=false` 成功响应及 Refunded 状态。发送前失败写“无哈希”，已上链失败则记录真实回执与 gas | 【截图 4A-7】 |
| `4A-8-01_ZZL_resolve浏览器详情.png`、必要时 `4A-8-02_ZZL_resolve日志.png` | ZZL 用公开哈希查询并截图；YZH 提供哈希 | 成功 resolve 的浏览器 From（后端账户）、To（新 Escrow）、状态、区块、Logs 或代币转移；跨标签页可分图 | 【截图 4A-8】 |

老师的 4A-5 是一个主编号，**允许多张清晰子图**；ZZL 将 DHT/YZH 提供的原图和真实交易逐项对应，不代替钱包主人签名。老师 PDF 的业务顺序是：后端部署 → DHT 创建两单和授权/付款 → YZH 争议 → 查询列表/详情 → 失败裁决 → 成功退款 → DHT 确认正常单。

## 主线结果补图

| 文件名（放 `evidence/4A/`） | 谁截图 | 内容与插入位置 |
| --- | --- | --- |
| `4A-S1_DHT_正常单Completed.png` | DHT 保存 confirm 回执；YZH 可补订单详情 | 正常单 Completed、真实 id/哈希；紧接 4A-8 |
| `4A-S2-01_YZH_卖家收款.png`、`4A-S2-02_DHT_买家退款.png` | YZH 截本人余额；DHT 截退款余额，ZZL 汇总 | 本组代币合约下 YZH 收到 30、DHT 争议退款 30 的前后对比；紧接 4A-8 |

## 加分项：报告 4.5 各自独立小节

| 编号与建议文件名 | 原图截图人 | 画面必须证明的内容 | 仓库目录 |
| --- | --- | --- | --- |
| `4C-1-01_ZZL_受害者存款.png`、`4C-1-02_ZZL_漏洞攻击结果.png` | ZZL；受害者本人提供钱包画面 | VulnerableVault 受害者真实存款、攻击成功回执、攻击前后金库及攻击合约余额和地址 | `evidence/4C/` |
| `4C-2-01_ZZL_攻击内部交易.png` | ZZL | attack 交易的浏览器 Internal Transactions；若未索引，截真实空列表并补余额证据，不编造内部调用 | `evidence/4C/` |
| `4C-3-01_ZZL_修复代码及攻击失败.png` | ZZL | FixedVault 修复代码、重新攻击失败/受阻的真实结果和受保护余额 | `evidence/4C/` |
| `T-1_YZH_正常单时间线.png` | YZH | `GET /orders/{正常单id}/events`：`OrderCreated`、`OrderPaid`、`OrderCompleted` 的实际事件及区块、哈希 | `evidence/timeline/` |
| `T-2_YZH_争议单时间线.png` | YZH | `OrderCreated`、`OrderPaid`、`OrderDisputed`、`OrderResolved(false)` 的实际事件；不虚构 `OrderRefunded` 日志 | `evidence/timeline/` |
| `T-3-01_YZH_跨十区块与浏览器对照.png`、`T-3-02_YZH_重启load复查.png` | YZH | 同一事件与浏览器 Logs 对照、跨 10 区块查询仍完整；重启后按原部署区块 load 并复查 | `evidence/timeline/` |

`T-1` 至 `T-3` 是本组订单时间线加分材料，**不是老师原有的 4A 截图编号**。重入挑战与时间线在报告 4.5 分两个小节，各自附真实截图和结论。

## 提交前由 YZH 与 ZZL 共同核对

1. `docs/evidence-index.md` 每行登记编号、实际文件路径、截图人、时间、合约地址、订单 id、交易哈希和核对状态；`docs/transactions.md` 补老师 4.3 原表漏列的第二单创建、第二单付款及准备代币转账。尚未发生的交易不填哈希。
2. 报告 4.1 是分工表，4.2 是公开关键数据，4.3 是交易表，4.4 是上述路线 A 原编号，4.5 是两个加分项，4.6 是真实问题与解决，最后如实写 AI 使用及错误修正。解释题 4A-1 至 4A-6 引用本组截图里的具体值。
3. 封面身份、贡献比例由真人确认；报告模板原件不覆盖，老师注明不得修改的评分页原样保留。GitHub 仓库只收公开代码、文档、无密钥的原图，不提交私钥、助记词或真实 `.env`。


## 2026-10-29 时间线最新截图安排

本项由 ZZL 一人收尾，DHT 正常单确认已经完成，不重复交易。现有 T-1/T-2 真实接口图可直接复用，旧副本的 ZZL 4A-8 两张原图已保留。建议再补一张 T-3-03：同笔成功退款交易的 Escrow OrderResolved 日志，区别于已拍到的 YZHToken Transfer。它是本组补充验收，不是老师指定的额外强制编号；具体页面、必须显示字段、文件名及待补状态见 [时间线交付说明](timeline-delivery.md)。用户提供原图后再核对、分类入库与登记，不生成替代截图。


## ZZL 时间线浏览器补图已归档

本次收到 ZZL 原图两张：T-3-03_OrderResolved 与 T-3-04_退款Transfer，均原样保存在 evidence/timeline/，已比对 SHA256。OrderResolved 订单 1 的原始布尔 data=false，Transfer 为 30 枚退回买家。浏览器将布尔参数命名为 refundToBuyer，与本组源码 sellerWins 不同；报告按源码及原始 data、Transfer 解释。两图未含交易哈希，与已有 4A-8-01 及 RPC 回执组合使用。此前“待补 T-3-03”的记录由本记录更新；截图文件与详细核对说明见时间线索引。本轮未发送交易。


## ZZL 时间线主要验收截图已收齐

ZZL 已提供 T-4-02 Java 构建成功原图，显示 BUILD SUCCESSFUL in 10s。独立读取其本次 23:26（Asia/Shanghai）生成的四份 JUnit XML：9 项中 8 通过、1 项原本地 EVM 场景跳过，0 失败、0 错误；本次 XML 单独存于 evidence/timeline/validation/ZZL-20261029T152619Z/，未覆盖先前验证。Solidity 编译与封装任务按原配置跳过，不将本图称为完整 Solidity/EVM 验证。

时间线已有 T-1/T-2 接口原图可复用，加上 ZZL 的 T-3-03、T-3-04、T-4-01、T-4-02，主要验收截图已齐。截图与原始数据均原样保存、登记哈希；CMD 语法问题已复测解决。下一步是后续将证据插入正式报告与核对组员信息，无需继续为本项重复交易或截图。当前改动仍在本机，未提交推送。
