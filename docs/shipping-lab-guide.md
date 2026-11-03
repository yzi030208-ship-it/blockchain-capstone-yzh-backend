# ZZL 单人发货实验操作指南

当前代码、本地验证和两单真实测试网交易均已完成；A=Completed，B=Refunded。本人SH-10最终汇总原图已收到，关键实验截图归档完成。下方保留分步操作过程作报告参考，不能重新执行创建、付款、发货、退款或确认。一次只执行聊天中当前放行的一步，截图发来核对后再继续，避免错过 Paid、Shipped 等中间状态。所有角色均由 ZZL 本人控制，不需要其他组员代签。

## 当前角色与合约

| 角色 | 公开地址 |
| --- | --- |
| 买家 | 0x28D4cD8504A89249087A5b5480e4960aF68d378d |
| 卖家 | 0x6FE3bbA93ab02387c4261Fef249dA3266f1AAa36 |
| 后端仲裁人 | 0x73705dabCDb0fD57089cAEcD34C44d0cE9458Ed9 |
| 指定 YZHToken | 0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee |
| 新 ShippingEscrow | 0xbD37ceD07122Fa6b4e4Fe2c1b4Ac5BBc0c63B40E，部署区块 2729018 |

手续费已支持本人完成仲裁人部署和卖家发货，均在 POTOS Testnet 60600；未提供单独充值截图或哈希，不补造该材料。

## 第一步 取得完整本地验证截图 SH-02

在 Windows 命令提示符 CMD 中执行：

```cmd
cd /d "C:\Users\86137\Desktop\blockchain-capstone-yzh-backend-main\blockchain-capstone-yzh-backend"
scripts\shipping\verify-local.cmd --offline
```

脚本使用现有 Ubuntu-22.04 的课程 Hardhat、solc 0.8.20 和 JDK 21。已为本机准备所需 Gradle 依赖缓存，`--offline` 防止重复等待网络；其他电脑首次使用要先安装课程工具和下载依赖，不能照搬已有缓存的假设。

脚本会启动自己的临时本地链、完整编译四份 Solidity 源码、生成封装，再执行 Java 和真实本地 EVM 测试。结束后只关闭自己启动的节点。没有读取测试网私钥、部署测试网合约或花费 POT。

本轮已核验的预期末尾为：

```text
BUILD SUCCESSFUL
SHIPPING_LOCAL_VALIDATION: PASS
SHIPPING_EVM_TESTS: 8, SKIPPED: 0
TESTS: 19, PASSED: 19, SKIPPED: 0, FAILURES: 0, ERRORS: 0
EVIDENCE: ...
```

📸 保留实际命令（若画面放得下）、末尾 PASS、测试数量和 EVIDENCE 路径，发来原图。真实结果若报错则直接发错误图，不以预期输出替代。SH-01 的代码图可随后按实际文件位置补拍。

## 后续步骤 仅作路线参考

1. 准备并核对卖家/仲裁人手续费：按当时指导从本人的买家账户发送，截图真实接收地址、金额、成功回执，再读到账余额。不得发给旧候选账户 0x48160...486a7。
2. 拍 SH-01 代码：`src/main/resources/contracts/ShippingEscrow.sol` 中 enum、onlySeller、markShipped、confirm、refund；必要时上下分图。
3. 启动后端：在独立窗口通过 `scripts/shipping/start-backend.ps1` 隐藏输入自己仲裁账户的私钥，后端监听 127.0.0.1:8081。密钥输入阶段不截图，不将私钥贴到聊天、命令参数或文件。
4. 先用第二个 CMD 查询 `curl.exe -sS http://127.0.0.1:8081/api/shipping/escrow/account`，应是本人的仲裁地址；不匹配时先停止，不发送部署。
5. 核对后通过 `POST /api/shipping/escrow/deploy` 部署一次。截图 SH-03，取得新地址、部署区块和 arbiter。原主业务合约保持原样。
6. 在 Remix 编译实际 ShippingEscrow.sol（0.8.20、Paris），用 At Address 附加后端刚部署的新地址。买家创建 A/B 两单，各 1 枚指定代币、填本人的卖家，deadline 创建时再按链上时间计算。不要现在预先填固定截止时间。
7. 买家对新 Escrow approve 共 2 枚并 deposit 两单。先拍 SH-04 的 Paid 和回执；真实订单 id 由创建结果确定。
8. 买家尝试 markShipped(A) 的拒绝画面为 SH-05；预检失败没有交易哈希时如实说明。切卖家账户 markShipped(A)，拍 SH-06 的成功回执、OrderShipped 和接口 Shipped；B 不发货。
9. 等真实链上时间超过截止，买家 refund(A) 应被拒绝，拍 SH-07 并证明已超期。不能先 confirm(A)，否则失败只能证明 Completed 不可退。
10. 买家 refund(B) 成功，拍 SH-08 的 Refunded 和 Transfer/余额。最后买家 confirm(A)，拍 SH-09 的 Completed 和卖家收款。
11. 后端状态/事件及浏览器 OrderShipped 的 SH-10 可与 SH-06 等图交叉复用，不为凑数重复交易。快照助手为 `scripts/shipping/capture-state.py`，只做查询，会保存实际状态、链时间和余额。

上述测试网步骤会在聊天中逐步给出准确命令、参数与截图点。当前所有实验步骤和截图收集均已完成；以下保留过程记录供报告使用。

## 本地行为设计

- 状态号：原 0..4 保留，Shipped=5。
- Paid 状态仅卖家可以 markShipped；只能在 deadline 之前或当时发货，防止过期补发货阻断退款。
- 买家确认要求 Shipped，成功变 Completed；Paid 阶段不能直接确认。
- refund 要求买家、Paid 和超期，故 Shipped 不可超期退款。
- Paid/Shipped 都允许买家或卖家发起争议，仍由指定仲裁人裁决。
- 后端 `/api/shipping/escrow` 是独立上下文；发货由真正卖家钱包签名，查询接口不冒充卖家。

SH 系列图、原始结果及最终报告材料以 [发货证据索引](../evidence/shipping/README.md) 为入口。当前发货分支尚未提交/推送，不影响订单时间线 PR #4。


## ZZL 已完成第一步

SH-02 已收到并核对：本次 19 项全部通过，0 跳过。原图和 local-validation/20261029T160955Z/ 已归档。下一步逐笔给本人卖家、仲裁账户准备测试网手续费，每笔完成并核对后再继续，避免重复转账。


## SH-01 代码图已归档

ZZL 已提供 SH-01a/b/c，全部原样归档，关键逻辑与实际源码一致。下一步在独立 CMD 运行 powershell -NoProfile -ExecutionPolicy Bypass -File scripts\shipping\start-backend.ps1，在本人本机隐藏输入仲裁测试账户私钥；不对密钥界面截图，不把密钥交到聊天。看到服务在 8081 启动后，在另一个 CMD 只查询 /api/shipping/escrow/account，核对 backendAddress 为 0x73705dabCDb0fD57089cAEcD34C44d0cE9458Ed9 并拍公开查询结果。核对后才继续部署，本阶段不发送部署交易。


## 后端账户身份已核对

ZZL 提供 SH-03a，GET /api/shipping/escrow/account 返回 0x73705dabcdb0fd57089caecd34c44d0ce9458ed9，与指定后端仲裁账户一致，截图原样归档。此步骤只证明签名账户身份，不能写成合约部署成功。部署与链上 arbiter 仍需取得实际响应和回执后核对。


## 实际部署成功及查询超时处理

ZZL 提供 SH-03b：新 ShippingEscrow `0xbD37ceD07122Fa6b4e4Fe2c1b4Ac5BBc0c63B40E`，部署哈希 `0x3d9d8578af3e7bd494fc1df62806fd1628a84d57867331e67aadd050365ea5be`，区块 `2729018`。随后两次 GET /address 分别出现 timeout 和 Connect timed out。Codex 只读查询课程 RPC，确认该交易回执 status=0x1、From 为本人指定仲裁人、合约 arbiter 相同、SHIPPING_VERSION=1，部署输入与生成封装字节码及 arbiter 构造参数逐字节相符。原始回执和验证结果存于 evidence/shipping/deployment/。再次只读 GET /address 已返回 200，内容一致，未重复部署。

本例应作为查询阶段暂时连接超时的实际问题记录，不能把它记为部署失败，也不能为解决查询错误再次部署。ZZL 已提供 SH-03c，GET /address 返回的新合约、仲裁人、区块正确。下一步按该新地址在 Remix At Address 附加。若以后确需重启后端，使用本地址和 fromBlock=2729018 load 恢复；不执行第二次 deploy。


## SH-03c 成功查询图已归档

ZZL 提供 SH-03c：合约 0xbD37ceD07122Fa6b4e4Fe2c1b4Ac5BBc0c63B40E、arbiter 0x73705dabCDb0fD57089cAEcD34C44d0cE9458Ed9、deployBlock=2729018，已与此前独立回执和真实后端响应核对。部署后查询超时已恢复，无需重新部署。接下来在 Remix 附加同一新地址，仅查询 SHIPPING_VERSION、arbiter、orderCount，先不创建或付款。


## 当前 Remix 附加步骤 使用已生成 ABI

为避免重新选择编译器和误点 Deploy，当前采用 Remix 官方支持的 .abi 附加方式。实际编译生成的 ABI 已原样复制到 evidence/shipping/deployment/ShippingEscrow.abi，含 markShipped、OrderShipped 等完整接口，其部署字节码已在独立 RPC 验证中核对。

在 Remix 新建 ShippingEscrow.abi，粘贴该文件的完整 JSON 数组并保持其为活动文件。Deploy & Run 环境选择 Browser Extension 并连接 MetaMask 的 POTOS Testnet，钱包账户切到本人买家。使用 Add Contract（旧界面 At Address）附加 0xbD37ceD07122Fa6b4e4Fe2c1b4Ac5BBc0c63B40E，不点击 Deploy。展开实例，只读点击 SHIPPING_VERSION、arbiter、orderCount，预期分别为 1、本人仲裁地址和当前实际订单数（本轮准备时读得 0）。发送附加与只读结果截图 SH-03d 后，再创建订单。

参考 https://remix-ide.readthedocs.io/en/latest/run.html 的 Using the ABI to load contracts 和 Browser Extension；该方式无需浏览器内再次编译，也不进行二次部署。


## Remix 三项只读返回值已取得

ZZL 提供 SH-03d1/2/3：SHIPPING_VERSION=1、arbiter 为指定 0x73705dabCDb0fD57089cAEcD34C44d0cE9458Ed9、orderCount=0。原图原样保存。图片裁剪较窄，未含网络、当前账户、附加合约地址，尚需一张左侧面板总览作上下文关联；无需重复截图三项值。此阶段为只读附加核对，尚未记录任何订单创建/付款/发货。


## Remix 附加上下文已核对

SH-03d4 已取得，显示 Browser Extension/MetaMask、本人买家、POTOS Testnet 及附加的新 ShippingEscrow，上下文与版本/仲裁人/orderCount 三张返回值图对应。下一步只创建订单 A，计划输入单独保存在 evidence/shipping/prep/order-A-input-plan.json；deadline 按当时最新链区块时间加 1800 秒计算，以便完成后续逐步截图。该文件只是计划，不是已发生交易。订单实际 id、哈希和区块以用户创建成功回执为准。


## 订单 A 已创建

SH-04a 已取得。独立查询新合约 orderCount=1，订单 A 实际 id=0，买卖家和指定代币正确，amount=1000000000000000000，deadline=1793326061（2026-10-30 10:07:41 Asia/Shanghai），当前为 Created。创建哈希 0x6d9834ed87e5d0559ee1e32e308a11bafe4e87ed22c70f39bc81a3f44138bc11，区块 2733701，回执0x1。截图中另有钱包请求窗口，不以它推断第二次交易；避免重复确认 createOrder。

考虑逐步截图所需时间，下一步优先 approve 共2枚、付款 A 并在截止前发货，然后再创建/付款 B；两笔 Paid 可分别截图，不要求同时处于 Paid。真实代币 ABI 已复制到 evidence/shipping/deployment/YZHToken.abi，附加原组代币地址，不重新部署代币。后续授权 spender 必须为本次新 ShippingEscrow，金额2e18，而非无限授权。


## 两枚代币授权已链上核验

ZZL 提供 SH-04b-01，画面为钱包支出上限请求，不能单独作成功证据。后续只读查询 owner=买家、spender=新ShippingEscrow 的 allowance 为 2000000000000000000；按10区块窗口查询到 Approval 事件，交易 0xd69d8e09a419c24dbcf1ac60a041c9d058f98934bcc12a0e9eb86d9598535fbd，区块 2733741，回执0x1、金额2枚，已保存原始回执。Remix 画面同时有 error.indexOf 异常和通用 gas 提示，不据此判断 gas 不足；以真实链上结果为准，避免重复授权。下一步只对订单0 deposit，再尽快由卖家 markShipped，B 后建。


## 订单 A 付款已核验

SH-04c 返回订单0为 Paid，1枚指定代币，买卖家与deadline正确。独立核对 deposit 交易 0x514b02e7a0f238a71efa343c3d87cf4b2afbb0ee6bcee4a8171a84a2e1f98e92，区块 2733777，回执0x1、发出 OrderPaid(0)，原始回执已保存。下一步优先切本人卖家在截止前 markShipped(0)，非卖家拒绝可在订单B的Paid阶段另行验证；不提前confirm或refund订单A。


## 订单 A 已在截止前发货

订单0已由本人卖家签名 markShipped(0)，交易 0xc2dc02a4ad4e0e054c2fa6d6b76477ef17eb8f92112464a94516653f2c4e66e6，区块 2733808，回执0x1，From与卖家一致，OrderShipped(0)日志存在，发货区块时间 1793325754 <= deadline1793326061。当前链上状态Shipped。用户提交的SH-06a为确认前请求，SH-06b为随后查询超时，旧Paid行不是当前状态。独立链上结果已归档，成功返回Shipped的用户图SH-06c已取得。保留Shipped，不confirm，等超期后验证买家refund受阻。无需再发markShipped。


## Shipped 状态原图已归档

SH-06c 已取得，订单0为Shipped，和先前卖家发货回执相符，买卖家、token、1枚金额、deadline保持一致。查询超时恢复，当前保留Shipped，不confirm。下一步创建订单B作为未发货对照，单独按当时链上时间加900秒设置deadline。B的输入计划单独保存，不预填未发生的订单id和交易；剩余授权按当前RPC核对后使用，不无故重复approve。


## 未发货对照单 B 已创建

B实际id=1，创建交易 0xe4db36290dbd5e17f4b454ef80f6c22b8ef62dda261f81979ea943b0dbf30b94，区块 2733859，回执0x1。买卖家、token和1枚金额正确，deadline=1793326900。用户图SH-04d-01为确认前请求，不将底部旧发货交易当作B成功证据；成功另由RPC回执确认。下一步由买家deposit本单，保持Paid不发货，等待超期退款。


## B 付款前 Created 原图

ZZL 提供 SH-04e-01，最后一条GET orders/1返回Created而非Paid。该图保存为付款前状态，不能写成deposit成功；当前独立链上状态、授权和nonce检查另存transactions/order1-payment-precheck.json。下一步需核对用户钱包中的deposit(1)是否已经确认，再进行付款，不重复创建订单或重新授权。


## 未发货订单 B 付款已核验

SH-04e-02 返回订单1为Paid，原有Created是之前查询状态。deposit交易 0xdb9201f80b61c98f0e559ce85ef21689ead699844b7fbe9c49aaf947b4ca15a2，区块 2733903，回执0x1、From为买家并有OrderPaid(1)，原始回执已保存。B保持Paid不发货；A保持Shipped不确认。下一步只读验证买家调用markShipped(B)被拒绝，以及A确已超期后refund(A)被拒绝，再等B超期由买家实际退款。


## 真实测试网只读预执行限制核验

新增 scripts/shipping/check-restrictions.cmd（Python标准库）。读取同一个真实链区块的时间和两单状态后，以本人买家地址为from，对新合约使用eth_call预执行refund(A)及markShipped(B)。要求A为Shipped且链上时间确已超过deadline、B为Paid，并核对真实RPC回滚原因分别为 Escrow: wrong status 与 Escrow: caller is not the seller，任一调用成功或出现非预期网络错误均不标PASS。原始请求/响应及区块哈希独立保存，不发送交易、不签名、不扣gas；失败调用没有交易哈希，不能写成两笔链上失败交易。

Codex首轮真实核验在区块2733918，时间戳1793326514，A截止1793326061已过，A=Shipped、B=Paid，托管原始余额2000000000000000000。两项预执行都按预期revert，机器结果见 evidence/shipping/restriction-checks/20261030T021526506558Z。用户本人运行的合并截图SH-05/SH-07仍待取得；它可同时证明权限限制和超期已发货退款受阻，但不替代B实际退款与A确认收款的成功交易截图。


## 本人 SH-05 与 SH-07 合并图已归档

ZZL 原图显示MODE、链60600、区块2733928、时间1793326595（2026-10-30 10:16:35 +08:00）、本人买家、A=Shipped且deadline1793326061已过、B=Paid、两条预期RPC回滚原因和托管余额2e18。RESTRICTIONS_PASS与本人运行目录 evidence/shipping/restriction-checks/20261030T021646896852Z 的原始请求/响应相符。图复用于SH-05及SH-07，不重复复制。此为eth_call预执行，不签名、不上链、无失败交易哈希，不写成两笔链上失败交易。下一步等待B在10:21:40超期后，由买家实际refund(1)，再完成A确认收款。


## B截止前真实退款失败与重试准备

SH-08-01为实际失败交易 0x432cea4867bee9abc398848e2b2b2c1519beb76d9ff8b2bdd5b7ba237d203c27，区块2733962，回执0x0，gasUsed28021。块时间10:20:47比B的10:21:40截止时间早53秒，钱包提示deadline not reached，故B仍为Paid。失败上链会消耗gas，不能写成无交易哈希的预检。随后链上10:22:13已过期，最新区块refund(1)只读预执行成功返回0x，原始结果见transactions/B-refund-too-early-and-readiness.json；这还不是实际退款成功，等待本人重提一次交易并提供成功结果。


## B未发货超期退款已实际成功

SH-08-03 返回订单1为Refunded。实际成功交易 0xd1553b94b32bf331e74b6257ca7017b7eb97e26838ebc10a9b7ed7287fa0a427，区块2733983，回执0x1，From为买家；退款区块时间 1793326982 大于deadline1793326900，包含OrderRefunded(1)和指定代币从新Escrow向买家返回1枚的Transfer。交易输入refund(1)已核对，不是仲裁退款。当前B=Refunded，A状态以本次核验为准；余额与状态见transactions/B-refund-success-verification.json。用户另提供gas估算失败弹窗图，画面同时有此成功绿色记录；不根据弹窗断定前笔失败，也不强行发送额外请求。


## 最终确认和只读汇总 SH-10

SH-09已收到，A确认交易0x5bff8401efb274336efba076a3ddcbaa2c9486bbea03094d9389f214716991ae，区块2734016，10:27:08，回执0x1、OrderCompleted(0)及指定代币向卖家Transfer 1枚已核验。B仍Refunded，托管余额0。无需再签名或发送交易。

在项目根目录的CMD运行：

```cmd
scripts\shipping\capture-final.cmd
```

脚本读取真实后端详情/时间线及同一真实RPC区块的代币余额，将原始JSON写入新的checkpoints目录。只有合约/角色/金额、两单状态、完整事件序列、成功交易哈希以及余额全匹配时才输出FINAL_PASS。最终图应保留命令、两单状态和EVENTS、TOKEN_BALANCES、FINAL_PASS及EVIDENCE路径。查询超时可重跑此只读脚本，不重发任何钱包交易。

助手先行实际运行通过，记录在checkpoints/20261030T022930018186Z/；该机器记录不能冒充ZZL本人运行的截图。


## 截图收尾已完成

ZZL已执行capture-final.cmd并提供SH-10原图，包含命令、链60600、新合约与仲裁人、两单完整时间线、29/1/0余额、FINAL_PASS和EVIDENCE路径。本人原始JSON为evidence/shipping/checkpoints/20261030T023111360218Z/snapshot.json；对应区块2734053，链时间10:31:01。原图和附件SHA256一致，7条事件逐条核对真实成功交易日志，余额按同一区块独立RPC复核，结果ARCHIVE_VERIFIED。

本项关键截图已齐，无需补充截图或发送交易。最终材料索引为evidence/shipping/README.md，报告素材为docs/report-parts/shipping.md。历史章节的“待操作”仅描述当时进度，不是当前待办。
