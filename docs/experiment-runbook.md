# 路线 A 实验执行与证据记录（YZH 主持）

状态：DHT 交接包已导入集成分支；后端账户充值、买家代币准备、SimpleStorage 示例和新 Escrow 部署已在测试网完成；**两笔订单主业务尚待真实执行**。本文件是现场清单，不能用本地 Hardhat 的地址、交易哈希或截图代替 POTOS Testnet 证据。交易记录统一交 ZZL 填入 `docs/transactions.md`，截图索引交 ZZL 填入 `docs/evidence-index.md`；文件尚未建立时先保留原始截图和哈希。

## 现场共同规则

- 三人检查 MetaMask 是 **POTOS Testnet，chainId 60600**。YZH 买卖家钱包、DHT 买家钱包、后端专用账户都是不同角色。后端私钥仅在 YZH 自己的 WSL 终端临时注入，不给 DHT、ZZL 或聊天。
- YZH 代币为 `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee`，18 位精度。DHT 买家至少有 60 个该代币和 gas，YZH 卖家及后端专用账户有 gas。余额随时变化，每次开始前重新读取。
- 只有 YZH 后端**部署一次**新 Escrow；之后三人全程用同一合约地址。服务重启只调用 `load`，传真实部署区块，不再次 `deploy`。
- 每笔发送交易立即保存：操作人公开地址、目标合约、订单 id、输入值、交易哈希、区块号、回执状态、浏览器链接、时间和截图文件名。发送前失败要保存**原始错误文本**，标明无交易哈希；不虚构哈希或 gas。
- 截图中不要出现私钥、助记词、访问令牌或终端输入私钥的画面。代码截图保证可读，长代码拆图并保留报告主编号。

## 0. 上链前检查（YZH）

- [x] 集成分支通过 `bash gradlew test`；2026-10-22 以 `-PcompileContracts=true` 重新编译、生成封装并运行本地 EVM 测试，5 项测试全部通过（0 失败、0 跳过）。这是本地验证，DHT/本人仍需截清晰的 4A-2 构建画面。
- [x] 2026-10-22 复核 RPC 链 ID `0xecb8`（60600），YZHToken 名称 `YZHToken`、符号 `YZH202466332566`、精度 `18`，DHT 买家代币余额 `0`，YZH 钱包代币余额 `70000000000000000000`（70 枚），后端账户 POT 余额 `0x5af3107a4000`（0.0001 POT）。余额会变化，交易前仍须再读。
- [x] 同日发现 MetaMask 中另一个**同名同符号**代币：合约 `0x7640f3dee667Ea05CA6c9450cF35b596577025A6`，YZH 钱包余额为 `36000000000000000000`（36 枚）。它不是本项目指定的 `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee`。钱包资产列表中的“36 枚”与指定合约的“70 枚”分别属不同合约；所有正式交易按**完整合约地址**核对，不能凭名称、符号或钱包列表选代币。两笔订单所需 60 枚可由指定合约中的 70 枚提供，无需因此铸币。
- [x] YZH 已用本人钱包给后端专用账户 `0x6717496Fe22477B41e6Ca07353C308fF38A61797` 转入 `0.0001 POT`：哈希 `0xda990292aedba2361a4679c950f22510f511d02c7a673d2dec4bf857df681cfb`，区块 `2672654`，回执 `0x1`；截图可作为账户准备的补充证据。
- [x] YZH 已用本人钱包从正确的 YZHToken 合约 `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee` 向 DHT 买家 `0x5FeDaeB4369432aD2A9929EaFFA9762C46050d4C` 转入 60 枚：交易哈希 `0x698aa6302106bfe33a9a71ed0e421fead150ccf84b6519883cbfda2c20935f30`，区块 `2672655`，回执 `0x1`。转后读取 DHT `balanceOf` 为 `60000000000000000000`，YZH 为 `10000000000000000000`。保存转账回执截图 `4A-PREP-02` 及买家余额截图 `4A-PREP-03`。代币和 POT 是两种资产。
- [x] YZH 仅在本机 WSL 临时注入 `WEB3J_PRIVATE_KEY`，公开地址自查与后端账户相符；`bash gradlew bootRun` 启动成功，Tomcat 监听 8080。无需把私钥或包含私钥的截图交给任何人。

## 1. 先验证课程 SimpleStorage 示例（YZH，报告 4A-3）

2026-10-22，YZH 在运行后端的 WSL 中完成实际调用：

- `POST /api/storage/deploy` 返回 SimpleStorage 地址 `0xD18efF885dc9F65273F1B76E2ad9ACEEce650b3A`。该示例接口只返回地址，部署哈希尚未记录。
- `POST /api/storage/value/set?value=42` 返回交易哈希 `0x0563cf0ddcbbc739071ffa15c07e76b0b13cd8b4cacc72cbb3f88c5aff835584`，区块 `2672663`，gasUsed `27267`，回执状态 `0x1`。
- `GET /api/storage/value/get` 返回同一合约地址和 `value: "42"`。

保存三次接口响应和 set 的区块浏览器详情，截图统一标 **4A-3**。SimpleStorage 与主业务 Escrow 是不同合约，地址不能混用。

## 2. 后端部署新的 Escrow（YZH，4A-5）

2026-10-22，YZH 后端调用 `POST /api/escrow/deploy`，返回新 Escrow 地址 `0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5`、部署哈希 `0x4f61670afea0a4714f89c02e177f14b7d2737fa1cad9cac797727344ec187372`、部署区块 `2672683`。RPC 部署回执 `status=0x1`，`from=0x6717496Fe22477B41e6Ca07353C308fF38A61797`，`contractAddress` 与返回地址相同。随后只读查询 `GET /api/escrow/address` 返回同一地址和区块，链上 arbiter `0x6717496fe22477b41e6ca07353c308ff38a61797`，与后端专用账户一致（地址比较忽略大小写）。不要再次部署；服务重启时使用 `load` 和原部署区块。保存 **4A-5 部署**截图。把这四项**公开数据**发给 DHT、ZZL 后才能开始两笔订单。

## 3. 买家两单创建并付款（DHT 本人签名，4A-5）

DHT 在 Remix/MetaMask 附加**上述新 Escrow**，创建正常单和争议单，各 30 个 YZHToken（原始整数 `30000000000000000000`）；seller 为 YZH `0xa508c9Ff372C40F1f109EdA1F8DB532fB556c30A`，token 为上述 YZHToken，deadline 为操作当时**未来约 30 分钟的 Unix 秒**。记录实际 id（新合约通常为 0、1）及两笔创建交易，截图 **4A-5 创建**。

DHT 对**新 Escrow 地址**执行代币 `approve` 共 60 个（原始整数 `60000000000000000000`），核对 allowance，再分别 `deposit` 两单。记录 approve 和两笔 deposit 的哈希及两个 `getOrder` 为 Paid 的画面，截图 **4A-5 付款**。订单 id 一律按实际回执，不硬填 0/1。

## 4. 争议、查询和仲裁（YZH/ZZL，4A-6 至 4A-8）

- [ ] YZH 用**卖家钱包**在同一新 Escrow 上 `dispute(争议单 id)`，截图回执和状态 Disputed（4A-5 卖家）。
- [ ] YZH 在后端查询 `GET /api/escrow/orders`、两笔 `GET /api/escrow/orders/{id}`；ZZL 截列表至少两单及 Paid/Disputed 详情（4A-6）。
- [ ] YZH 后端尝试 `POST /api/escrow/orders/{正常单id}/resolve?sellerWins=true`。记录实际失败响应：若发送前拒绝就写无哈希；若有失败回执就保存其哈希/区块/gas。ZZL 截 4A-7 失败。
- [ ] YZH 后端执行 `POST /api/escrow/orders/{争议单id}/resolve?sellerWins=false`。保存成功回执、交易浏览器 From/To/Logs，核对订单变 Refunded、DHT 买家代币余额增加 30；ZZL 截 4A-7 成功与 4A-8 浏览器。
- [ ] DHT 对正常单 `confirm`；保存回执，核对 Completed 和 YZH 卖家余额增加 30；DHT/YZH 分别提供本人钱包及余额截图（4A-S1/4A-S2）。

## 5. 时间线与重启恢复（YZH，4.5 的 T-1 至 T-3）

- [ ] 两单调用 `GET /api/escrow/orders/{id}/events`。正常单预期实际日志 `OrderCreated → OrderPaid → OrderCompleted`（T-1）；争议单预期 `OrderCreated → OrderPaid → OrderDisputed → OrderResolved(false)`（T-2）。`resolve(false)` 导致业务状态 Refunded，**不会额外生成 OrderRefunded 日志**。
- [ ] 记录每条事件的区块、交易索引、日志索引、交易哈希；与浏览器 Logs 至少对照一个事件。用实际跨越超过 10 区块的记录验证分批查询，截图 T-3。
- [ ] 停止并重启服务，调用 `POST /api/escrow/load?address=<新地址>&fromBlock=<原部署区块>`，再次查询列表、详情、时间线；保存前后可对照的响应，不重部署。

## 6. ZZL 独立加分挑战及报告

ZZL 按 [tasks-zzl.md](tasks-zzl.md) 完成自身 VulnerableVault、Attacker、FixedVault 的测试网演示，保存 **4C-1 至 4C-3**。挑战使用原生 POT 和独立合约，不混入本次 Escrow 交易表。三人分别把本人报告片段、真实截图和问题记录交 YZH；YZH 收齐后再制作最终报告副本。
