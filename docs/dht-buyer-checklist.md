# DHT 买家操作与截图清单

状态：待 YZH 新 Escrow 部署后执行。所有钱包交易由 DHT 本人确认；不要把私钥或助记词发送给 Codex 或其他成员。

## 开始前向 YZH 取得的公开信息

- 新 Escrow 地址、部署交易哈希、部署区块和链上 arbiter。arbiter 应为后端专用账户，不是 ZZL 旧仲裁地址。
- 本次 RPC、chainId=60600、浏览器地址、卖家地址；网络值由 YZH 启动前复核。
- YZH 代币 `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee` 的 decimals=18 本次核对结果。
- DHT 买家钱包 `0x5FeDaeB4369432aD2A9929EaFFA9762C46050d4C` 中至少 60 个该代币及足够 POT。钱包地址本人再次确认。
- 卖家公开地址 `0xa508c9Ff372C40F1f109EdA1F8DB532fB556c30A` 本次确认结果。

## 买家完整操作

1. Remix 编译本仓库 `src/main/resources/contracts/Escrow.sol`。Environment 选择 MetaMask 注入方式，确认网络和当前买家账户；用 At Address 附加 YZH 的新 Escrow，不能点击 Deploy 新建另一个。
2. 先读取 arbiter 和 orderCount，核对地址。新部署尚无订单应从 id 0 开始，已有订单时记录实际 id，不能强改编号。
3. 用 `createOrder` 创建两笔：seller=YZH、token=上述 YZH 代币、amount=`30000000000000000000`，deadline=当前 Unix 秒加 1800，description 分别“正常订单”“争议订单”。时间戳要在实际操作时重新算，不能使用本文固定旧值；每笔等成功回执，记录真实 id/哈希/区块。
4. 在 Remix 编译本仓库 YZHToken.sol，并 At Address 附加已有 YZH 代币。读取 balanceOf(买家)、decimals，确认足够；调用 approve(新 Escrow, `60000000000000000000`)，签名后等待确认，再读 allowance(买家, 新 Escrow)。
5. 返回同一个新 Escrow，分别 deposit(正常单 id)、deposit(争议单 id)，每笔等待回执；getOrder 核对 Paid。approve 是授权，deposit 才将代币转入托管；两类交易分别记录。
6. 通知 YZH 可对争议订单 dispute，再让后端查询并执行“正常单仲裁失败、争议单退款成功”。不要提前 confirm，以免破坏规定验收时点。
7. 退款成功后，在代币合约 balanceOf 核对买家实际增加 30 个；再对正常单 confirm，核对 Completed，通知 YZH 查询卖家增加 30 个。代币余额变化与 POT gas 支出分开说明。
8. 把创建两笔、approve、deposit 两笔、confirm 的真实哈希/区块/账户、相关截图交给 ZZL；余额读取没有新交易哈希。

计算到期时间时可在 WSL 运行 `date -d '+30 minutes' +%s`；以当时系统时间和链上时间核对。若不慎授权旧 Escrow，先如实记录并与组长核对该授权是否需撤销，不继续往错地址付款。

## DHT 本人截图

| 原编号 | 何时截图 | 截图要包含 | 当前状态 |
| --- | --- | --- | --- |
| 4A-1 | 打开编辑器的 application.properties | `web3j.private-key=${WEB3J_PRIVATE_KEY:}`、RPC/gas；本文件配置已就绪，不能截真实变量值 | 待本人截图 |
| 4A-2-01 | 运行生成任务成功后 | 命令、BUILD SUCCESSFUL、生成文件列表，显示 Escrow/YZHToken/IERC20 | 待本人重跑并截图 |
| 4A-2-02 | 打开生成 Escrow.java | BINARY、FUNC_ 常量、Order 结构、OrderCreatedEventResponse 与 getOrderCreatedEventFromLog；分多张保证清晰 | 待本人截图 |
| 4A-4 Service | 打开 EscrowService.java | 完整核心实现，含接口方法、chainId 签名管理器、共享状态及异常处理；长代码分图；Controller 由 YZH 提供 | 待本人截图 |
| 4A-5 买家创建 | 新 Escrow 已附加、创建两单成功后 | 本人账户、合约地址、createOrder 关键参数、两笔真实交易或回执 | 待测试网操作 |
| 4A-5 买家付款 | approve 后及每次 deposit 后 | 代币地址、新 spender、授权金额、成功回执；两笔 deposit 的 id 与回执 | 待测试网操作 |
| 4A-S1/4A-S2 补充 | 后端退款及买家 confirm 后 | getOrder 最终状态、买家退款余额和正常单确认回执；卖家余额由 YZH 截图 | 待测试网操作 |

报告保留 4A-2/4A-4/4A-5 主编号；图内拆分后缀仅为组织材料，不替代报告原编号。代码/构建截图可现在取得，买家链上操作必须等新部署与余额准备好。
