# 项目目标与三人协作

## 这个项目要完成什么

本组选择课程大作业**路线 A：Java + Spring Boot + Web3j 平台后端**，在 POTOS Testnet 上操作新部署的 Escrow。后端提供部署、加载、地址与仲裁人查询、单笔订单、订单列表、争议裁决六个必做接口；另提供完整订单事件时间线。主业务使用 YZH 的 ERC20 `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee`（18 位精度），**不能使用实验 3 的旧 Escrow 或 DHT 的旧代币**。ZZL 另做独立的原生 POT 重入挑战；挑战金库与主业务 Escrow 分开。

要实际完成并留证的结果：

1. 后端专用账户部署**一份新的** Escrow，链上 `arbiter()` 为 `0x6717496Fe22477B41e6Ca07353C308fF38A61797`。部署地址、交易哈希、部署区块来自真实回执。
2. DHT 买家与 YZH 卖家完成两笔各 30 个代币的订单：订单 0 正常完成，订单 1 争议后由后端仲裁退款。实际订单 id 以新合约返回为准。
3. 后端的列表和详情与链上状态一致；时间线从实际日志展示 `OrderCreated / OrderPaid / OrderCompleted` 和 `OrderCreated / OrderPaid / OrderDisputed / OrderResolved(false)`。`resolve(false)` 不虚构 `OrderRefunded` 事件。
4. 展示未争议订单裁决失败、服务重启后按原地址和部署区块 `load` 恢复查询、跨 10 区块分批读取与区块浏览器 Logs 对照。
5. ZZL 在本组自行部署的测试合约上演示重入漏洞及修复前后差异，保存真实余额、回执和截图。
6. 三人按报告模板交付代码、交易表、编号截图、解释题、问题记录和 AI 使用说明。YZH 收齐证据后制作最终报告副本，保留模板要求不改的评分页。

当前只是**可构建的 YZH 后端基线**：Controller、事件列表与时间线已写，DHT 的 Escrow 核心 Service 和 Solidity 封装还未接入；因此此时 Escrow 路由返回 503，不能把它写成联调完成。真实部署、两笔订单、SimpleStorage 链上验证和重入挑战均待执行。公开参数与待核对状态见 [project-config.md](project-config.md)。

## 谁写代码，谁签名

| 成员 | 开发与材料 | 本人钱包/后端操作 |
|---|---|---|
| DHT（成员 1） | 合约与 Java 封装、`EscrowGateway` 核心实现、4A-1/2/5 解释和本人材料，详见 [tasks-dht.md](tasks-dht.md) | 买家钱包创建订单、`approve`、两次 `deposit`、正常订单 `confirm`，本人截图 |
| YZH（组长、成员 2） | 现有 Controller、事件查询、时间线、公开配置、合并与最终报告 | 卖家钱包 `dispute`；自己的后端专用账户在本机签名部署与 `resolve`。私钥不交给组员 |
| ZZL（成员 3） | 独立重入挑战、验收脚本、交易和截图索引、4A-4 解释，详见 [tasks-zzl.md](tasks-zzl.md) | 仅在自己部署的挑战合约使用测试账户；主业务记录公开交易，不替买卖家签名 |

## 仓库协作步骤

仓库：[blockchain-capstone-yzh-backend](https://github.com/yzi030208-ship-it/blockchain-capstone-yzh-backend)（私有）。DHT/ZZL 先接受 GitHub 邀请，再各自在 WSL 克隆并从最新 `main` 建分支。推荐 `feat/dht-service`、`feat/zzl-reentrancy-evidence`；若已按先前通知使用 `dht-escrow-service` 或 `zzl-validation`，可继续使用，不必改名。不要直接把未审查的工作推到 `main`。

```bash
gh repo clone yzi030208-ship-it/blockchain-capstone-yzh-backend
cd blockchain-capstone-yzh-backend
git switch -c feat/dht-service       # ZZL 改为 feat/zzl-reentrancy-evidence
bash gradlew test
```

各人只改自己的任务范围，分阶段提交并推送分支，向 `main` 发 Pull Request（PR）。PR 写清修改文件、构建/测试命令和结果、未完成项、需要 YZH 配合的公开数据。YZH 审查并合并；合并后所有人 `git switch main && git pull`，再从新 `main` 继续。出现接口冲突时先对照 [handoff-dht.md](handoff-dht.md) 与现有 `EscrowGateway`，不要另建 Controller 或另一份地址状态。禁止提交私钥、助记词、真实 `.env` 或将其放在截图中。

## 集成和验收顺序

1. **现在并行**：DHT 实现 Service/封装；ZZL 实现独立重入挑战及空白交易/截图索引；YZH 给后端账户准备少量测试网 POT，并协调买家至少持有 60 个 YZH 代币及 gas。
2. **DHT PR 合并后**：YZH 和 DHT 一起跑完整 Solidity 编译、封装生成、Java 测试；YZH 在自己 WSL 本地注入后端账户私钥并启动服务。DHT/ZZL 不需要该私钥。
3. **唯一新部署**：YZH 调 `POST /api/escrow/deploy`，核对回执、链上 `arbiter()`、部署区块，并只共享公开的新 Escrow 地址。三人均使用该地址；不要各自部署一份主业务 Escrow。
4. **两单实测**：严格依次执行分工指南第 9 节；每笔签名由钱包主人完成。ZZL 立即记录哈希、区块、回执、浏览器链接和截图编号。DHT 的买家操作、YZH 的卖家操作分别由本人截图。
5. **后端与加分验收**：YZH/ZZL 核对列表、详情、失败裁决、成功退款、正常完成、两条时间线、重启 `load`。ZZL 独立完成重入挑战并提交真实证据。
6. **报告**：各人交 `docs/report-parts/姓名缩写.md` 和截图索引；YZH 核对三人真实证据、贡献比例与 AI 使用说明后制作报告副本。没有交易哈希的发送前失败明确写“无交易哈希”。

## 交接时必须写清

每次 PR 或群内交接给出：分支/提交、完成文件、实际运行命令与结果、公开地址或交易哈希、未完成项、下一位操作者、需要截图的编号。**“做好了”不足以开始集成。**截图编号和责任人见 [tasks-dht.md](tasks-dht.md)、[tasks-zzl.md](tasks-zzl.md) 及 [YZH 报告片段](report-parts/YZH.md)。
