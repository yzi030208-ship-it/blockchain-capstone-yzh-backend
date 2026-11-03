# 卖家发货状态实验材料

本目录仅用于发货扩展版 ShippingEscrow。原主业务 Escrow、订单时间线和独立重入挑战各自保留，不混用交易地址和截图编号。SH、SH-PREP 是本组补充编号，不是老师指定编号。

## 当前结果

2026-10-30 00:04（Asia/Shanghai），Codex 在本机运行完整 Solidity 0.8.20 / EVM Paris 编译、Java 封装生成及本地链测试。19 项全部通过、0 跳过、0 失败，其中 ShippingEscrow 真实本地 EVM 测试 8 项。原始结果在 [local-validation/20261029T160409Z/summary.json](local-validation/20261029T160409Z/summary.json)。这是临时 chainId=31337 的验证，不是 POTOS 部署或交易。

ZZL 本人已重新执行验证并提供 SH-02 原图，对应 local-validation/20261029T160955Z/，19 项全部通过。新测试网合约已由 ZZL 部署并通过独立回执/字节码核对。A（0）已完成，B（1）已退款，卖家收到1枚、买家余额29枚、托管余额0；成功交易和原图见下表。最终本人运行的 SH-10 汇总原图和对应JSON已收齐，全部关键实验截图已归档。

## 已归档的截图与证明范围

| 编号 | 文件或内容 | 当前状态 |
| --- | --- | --- |
| SH-PREP-01 | [最初的候选账户图](screenshots/prep/SH-PREP-01_ZZL_候选卖家账户零POT.png) | 原图已保存；只显示 0x48160...486a7 和 0 POT。用户后来指定了不同的两个账户，本图只作历史准备记录，不充当当前角色证据 |
| SH-01a/b/c | [状态与结构](screenshots/01-code/SH-01a_ZZL_Shipped状态与订单结构.png)、[仅卖家权限](screenshots/01-code/SH-01b_ZZL_仅卖家权限检查.png)、[发货/确认/退款](screenshots/01-code/SH-01c_ZZL_发货确认与超期退款限制.png) | 已收到 ZZL 三张原图，关键逻辑完整；裁剪图无编辑器标题/行号，与源码和 SH-02 编译结果结合使用 |
| SH-02 | [本人完整验证原图](screenshots/02-tests/SH-02_ZZL_完整编译及19项测试通过.png)、[本次总结](local-validation/20261029T160955Z/summary.json) | 已收到；BUILD SUCCESSFUL in 55s，19 项通过、0 跳过，含 8 项 shipping EVM；未显示初始命令 |
| SH-PREP-02/03 | 本人向新卖家和仲裁人账户准备测试手续费 | 后续卖家和仲裁人已分别成功签名发货、部署；未收到单独充值图或充值哈希，不补造充值证据 |
| SH-03a | [后端仲裁账户原图](screenshots/03-deployment/SH-03a_ZZL_后端仲裁账户核对.png) | 已收到，公开地址与指定仲裁账户相符；不是部署图 |
| SH-03b | [部署响应及后续查询超时原图](screenshots/03-deployment/SH-03b_ZZL_部署响应及查询RPC超时.png)、[链上核验](deployment/verification.json) | 已收到；回执 0x1、区块 2729018、仲裁人及版本正确，部署输入与生成字节码和构造参数一致 |
| SH-03c | [成功查询原图](screenshots/03-deployment/SH-03c_ZZL_部署后地址仲裁人与区块查询成功.png)、[原始 JSON](deployment/backend-address.json) | 已收到；合约、arbiter、deployBlock 与链上回执一致，查询超时已恢复 |
| SH-03d1/2/3 | [版本 1](screenshots/03-deployment/SH-03d1_ZZL_Remix发货版本为1.png)、[仲裁人](screenshots/03-deployment/SH-03d2_ZZL_Remix仲裁人查询.png)、[订单数 0](screenshots/03-deployment/SH-03d3_ZZL_Remix订单数为0.png) | 三张返回值正确；上下文已由 SH-03d4 总览补足 |
| SH-03d4 | [Remix 买家、网络与合约总览](screenshots/03-deployment/SH-03d4_ZZL_Remix买家网络与合约总览.png) | 已收到；MetaMask、买家缩略地址、POTOS Testnet 和新合约上下文可与三张只读值图对应 |
| SH-04a | [订单 A 参数及成功记录](screenshots/04-orders-payment/SH-04a_ZZL_订单A创建参数及成功记录.png)、[创建核对](transactions/order-creation-check.json) | A=id0，Created，1 枚，截止1793326061；创建阶段原图；后续已付款、发货和确认，见对应后续行 |
| SH-04b-01 | [授权确认前原图](screenshots/04-orders-payment/SH-04b-01_ZZL_两枚代币授权确认前.png)、[成功回执核验](transactions/approval-verification.json) | 原图是请求阶段；后续实时链上 allowance=2枚，Approval 回执0x1已独立确认，不重复 approve |
| SH-04c | [订单0 Paid 实时查询原图](screenshots/04-orders-payment/SH-04c_ZZL_订单0付款后Paid.png) | 已收到；付款回执0x1与OrderPaid事件已独立核对，后续发货见 SH-06 |
| SH-04d | [订单B参数确认前图](screenshots/04-orders-payment/SH-04d-01_ZZL_订单B参数及确认前.png)、[链上创建核对](transactions/order-B-creation-check.json) | B已创建，实际id=1；成功由链上回执证明，后续付款见 SH-04e-02 |
| SH-04e-01 | [订单1 Created 原图](screenshots/04-orders-payment/SH-04e-01_ZZL_订单1付款前Created.png) | 付款前状态，不冒充Paid；进一步付款核验见 transactions/order1-payment-precheck.json |
| SH-04e-02 | [订单1 Paid 原图](screenshots/04-orders-payment/SH-04e-02_ZZL_订单1付款后Paid.png)、[付款核验](transactions/order1-deposit-verification.json) | 已收到，B实际付款成功，仍保持未发货 |
| SH-05 | [权限与退款限制合并原图](screenshots/05-restrictions/SH-05_SH-07_ZZL_真实链权限与发货超期退款预执行限制.png)、[本人原始记录](restriction-checks/20261030T021646896852Z/summary.json) | 已收到，买家对Paid的B发货预执行按权限回滚；不是链上失败交易 |
| SH-06a/b | [卖家发货请求](screenshots/06-shipping/SH-06a_ZZL_卖家markShipped订单0确认前.png)、[查询超时](screenshots/issues/SH-06b_ZZL_发货后订单查询超时.png)、[发货回执核验](transactions/shipping-verification.json) | 链上已成功 Shipped，From为卖家、在deadline前；两图本身不是成功状态截图 |
| SH-06c | [订单0 Shipped 实时接口原图](screenshots/06-shipping/SH-06c_ZZL_订单0发货后Shipped.png)、[原始 JSON](transactions/order0-shipped-api.json) | 已收到，实际发货状态和既有买卖家/金额/截止时间对应；不重复发货 |
| SH-07 | 复用上方 SH-05/SH-07 原图 | 已收到，真实时间1793326595>A截止1793326061，A仍Shipped且refund预执行回滚；无失败交易哈希 |
| SH-08-01 | [B未到期退款真实失败原图](screenshots/issues/SH-08-01_ZZL_B未到期退款链上失败.png)、[核对说明](issues/B-refund-before-deadline.md) | 区块时间10:20:47，较截止早53秒，回执0x0；当前已过期且预执行成功，随后实际退款成功，见 SH-08-03 |
| SH-08-03 | [B退款后Refunded原图](screenshots/07-refund/SH-08-03_ZZL_未发货订单1退款后Refunded.png)、[成功回执与Transfer核验](transactions/B-refund-success-verification.json) | 已收到，真实超期refund(1)回执0x1、OrderRefunded、1枚返回买家；额外估算失败弹窗不影响已确认成功交易 |
| SH-09 | [订单0 Completed 原图](screenshots/08-completion/SH-09_ZZL_订单0确认收货后Completed.png)、[确认与收款核验](transactions/A-confirm-success-verification.json) | 已收到；买家 confirm(0) 回执0x1，OrderCompleted(0) 和向卖家转1枚的 Transfer 均已独立核对 |
| SH-10 | [本人最终时间线与余额原图](screenshots/09-final-summary/SH-10_ZZL_最终两单时间线与余额核验通过.png)、[本人运行原始JSON](checkpoints/20261030T023111360218Z/snapshot.json) | 已收到；A完整4事件、B完整3事件，余额29/1/0，FINAL_PASS；7条实际回执日志和3项同区块余额已独立核对 |

## 公开角色

以用户明确提供的 [公开配置](../../docs/shipping-public-config.json) 为准：

- 买家：`0x28D4cD8504A89249087A5b5480e4960aF68d378d`。
- 卖家：`0x6FE3bbA93ab02387c4261Fef249dA3266f1AAa36`。
- 后端仲裁人：`0x73705dabCDb0fD57089cAEcD34C44d0cE9458Ed9`。

这是 ZZL 一人操作的三个账户，不需要三个人。公开 RPC 只读预检见 [roles-preflight.json](prep/roles-preflight.json)；两个新账户当时原生币余额均为 0，买家有 30 枚本组代币。实际发送前再次核对，不用预检当作充值成功证据。

## 分类规则

- `prep/`：账户原图、角色余额和准备阶段记录。
- `local-validation/<UTC时间>/`：每次真实本地编译、生成和测试结果，独立保存。
- `checkpoints/<UTC时间>/`：后续真实运行后端后的只读状态、事件、余额和链时间快照。
- 截图原图保存在本目录或 `prep/`，收到后再登记文件名、提供者、SHA256、交易和证明范围。

截图尚未产生时不生成替代图、不将预期值填成实测。测试网回执要与本地测试数据明确区分。收到原图后更新索引；本次真实测试网部署、发货、退款及确认交易均已完成并独立核验。


## 当前发货版部署地址

ShippingEscrow：`0xbD37ceD07122Fa6b4e4Fe2c1b4Ac5BBc0c63B40E`，部署区块 `2729018`，交易 `0x3d9d8578af3e7bd494fc1df62806fd1628a84d57867331e67aadd050365ea5be`。独立 RPC 回执确认成功；后端 GET /address 最初两次返回 timeout / Connect timed out，随后只读重试已恢复，未重复部署。后续使用本地址，不使用原主业务地址。


限制核验脚本 `scripts\shipping\check-restrictions.cmd` 仅用于当时A=Shipped且过期、B=Paid的阶段；现两单已结束，不重跑该阶段检查。当前助手首轮结果见 [实际原始记录](restriction-checks/20261030T021526506558Z/summary.json)，用户成功运行截图及对应记录已归档，见SH-05/SH-07行。这是ETH_CALL预执行核验，不发送真实失败交易。


## 归档完成

本人SH-10对应区块2734053，区块时间2026-10-30 10:31:01 Asia/Shanghai，快照目录时间为UTC。截图原图未编辑、SHA256与附件相同；对应原始JSON保持不改。7条时间线事件逐条与真实成功交易回执的地址、topic0、订单id、logIndex、区块匹配，3项代币余额按同一区块回读核对。详见 [最终核对](final-evidence-audit.json) 与 checkpoints/20261030T023111360218Z/archive-verification-rpc.json。

本项关键截图已齐全，无需补发任何交易或重复实验。代码、本地测试、部署、Paid、权限/退款限制、Shipped、Refunded、Completed以及最终接口时间线/余额各类材料均可用于报告4.5。准备阶段单独充值图未提供，不作为必补实验截图；相关角色后续真实签名交易已另有证据。正式报告尚待按模板排版；代码和证据的实际GitHub发布状态见[交付说明](../../docs/shipping-delivery.md)。


## 2026-11-03 发布前复测

Codex在本机实际再次运行完整Solidity编译、封装生成、Java/HTTP和真实本地EVM测试，19通过、0跳过/失败，含Shipping EVM 8项。原始结果见[local-validation/20261103T043807Z/summary.json](local-validation/20261103T043807Z/summary.json)。这是助手发布前复测，未覆盖ZZL本人SH-02原图及20261029T160955Z记录，也未发送任何测试网交易。
