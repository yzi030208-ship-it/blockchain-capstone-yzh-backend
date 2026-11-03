# ZZL 卖家发货状态加分项交付说明

2026-10-30（Asia/Shanghai）：实现、更新测试、真实测试网实验、本人关键截图和证据归档均已完成。正式报告尚待按课程模板排版；本文件记录本项验收结论。SH 为本组整理编号。

## 完成范围与人员

ZZL 一人操作自己控制的买家、卖家、后端仲裁三个测试账户，完成部署、两单创建与支付、卖家发货、未发货超期退款、买家确认及全部截图。三个人员角色无需三名操作者。Codex 协助实现、测试、指导、公开链数据核对和原图归档；保留本组原 Escrow 与时间线的 DHT/YZH 历史贡献。

公开账户、实际订单和全部关键哈希统一见 [shipping-public-config.json](shipping-public-config.json)。发货扩展使用独立 ShippingEscrow，原主业务地址和30枚订单不用于本项1枚对照实验。

## 验收对应表

| 要求/行为 | 实现及实际结果 | 证据入口 |
| --- | --- | --- |
| Shipped 与 markShipped(id) | Shipped=5追加；仅订单卖家、Paid、未超过deadline可以发货，成功发出OrderShipped | [合约](../src/main/resources/contracts/ShippingEscrow.sol)、SH-01、SH-06 |
| 权限和状态约束 | 非卖家不能发货、重复发货/过期补发货受阻；确认要求Shipped | [8项本地EVM测试](../src/test/java/com/wetech/demo/web3j/shipping/ShippingEscrowLocalIntegrationTest.java)、SH-05/07 |
| 超期且已发货不能退款 | A在截止前Shipped，超期后以买家进行真实eth_call退款按wrong status回滚 | [本人限制检查](../evidence/shipping/restriction-checks/20261030T021646896852Z/summary.json)、SH-05/07 |
| 超期且未发货可以退款 | B仍Paid，超期后refund(1)回执0x1、OrderRefunded及返还买家1枚Transfer | [退款核验](../evidence/shipping/transactions/B-refund-success-verification.json)、SH-08 |
| 已发货确认与收款 | 买家confirm(0)回执0x1，A变Completed、卖家实际收到1枚 | [确认及收款核验](../evidence/shipping/transactions/A-confirm-success-verification.json)、SH-09 |
| 更新测试 | Solidity0.8.20/Paris编译、生成Java封装、HTTP与真实本地EVM测试共19项通过、0跳过/失败 | [本人测试结果](../evidence/shipping/local-validation/20261029T160955Z/summary.json)、SH-02 |
| 更新接口 | 独立/api/shipping/escrow详情文字状态、列表、含OrderShipped的七事件时间线；load检查版本 | [Controller](../src/main/java/com/wetech/demo/web3j/shipping/ShippingEscrowController.java)、SH-06/10 |
| 独立截图和说明 | 27张本人原图按11类目录归档，含代码、测试、部署、中间态、限制、终态和问题过程 | [截图分类目录](../evidence/shipping/screenshots/README.md)、[证明范围总表](../evidence/shipping/README.md) |

本地EVM验证使用临时chainId=31337；真实部署和实验使用POTOS Testnet chainId=60600。两项限制核验是eth_call预执行，不签名、不广播、不产生失败交易哈希或gas费用；另有一笔B未到期退款的实际失败交易，已独立保留，不能混写。

## 最终链上结果

新合约：`0xbD37ceD07122Fa6b4e4Fe2c1b4Ac5BBc0c63B40E`，部署区块2729018。

| 订单 | 实际状态转移 | 每单金额 | 结算结果 |
| --- | --- | --- | --- |
| A（id=0） | Created → Paid → Shipped → Completed | 1枚指定YZHToken | 卖家收到1枚 |
| B（id=1） | Created → Paid → Refunded | 1枚指定YZHToken | 买家退回1枚 |

ZZL本人运行 `scripts\shipping\capture-final.cmd` 的原始记录为 [最终快照](../evidence/shipping/checkpoints/20261030T023111360218Z/snapshot.json)。区块2734053，链时间2026-10-30 10:31:01+08:00，买家29枚、卖家1枚、托管0枚，结果FINAL_PASS。7条事件逐条与真实成功交易回执匹配，3项余额在同一区块回读一致，见 [最终归档核对](../evidence/shipping/final-evidence-audit.json)。

## 资料结构与报告使用

- [screenshots/](../evidence/shipping/screenshots/README.md)：代码3、测试1、部署7、订单/付款6、限制1、发货2、退款1、收货1、最终汇总1、准备1、问题3，共27张。候选账户图仅作历史准备，确认前请求和问题图不充当成功证据。
- `evidence/shipping/deployment/`：部署、ABI、版本及地址核对。
- `evidence/shipping/transactions/`：实际成功与失败回执、Transfer及状态核对。
- `evidence/shipping/restriction-checks/`：限制预执行的原始请求、响应和区块时间。
- `evidence/shipping/local-validation/`：实际本地测试XML、日志和汇总。
- `evidence/shipping/checkpoints/`：本人及助手各自运行的API、事件和余额快照，按UTC目录区分。
- [报告4.5素材](report-parts/shipping.md)：最终事实、截图安排、交易、问题和AI使用说明，可直接用于后续正式报告。

全部原图保持原始字节；原附件名、SHA256、分类和证明范围见 [截图收据](../evidence/shipping/screenshot-receipts.json)，全部证据文件见 [完整性清单](../evidence/shipping/manifest.json)。从GitHub检出后的文件内容也按该清单核对。

实验与关键截图已齐，无需再次创建、付款、发货、退款或确认。若后续只复核结果，在已恢复新ShippingEscrow的后端上运行 `scripts\shipping\capture-final.cmd` 即可；限制脚本需要当时的Shipped/Paid中间态，现两单结束，不再重跑该阶段。

## 发布与协作

2026-11-03已完成GitHub同步：功能提交 `3f3358cee470f23bd313d1063341d516c7ad1f0b` 推送至 `feat/zzl-shipping-status`，创建[PR #5](https://github.com/yzi030208-ship-it/blockchain-capstone-yzh-backend/pull/5)，目标为main，当前open、尚未合并。时间线PR #4此前已合并，本分支基于最新main，PR仅展示本次发货交付及相关说明。

已直接读取GitHub上的提交树及manifest：104个证据文件逐个核对远端Git blob、声明SHA256与字节数，27张本人原图全部存在且一致；合约、生成封装、服务/接口、测试、脚本和文档均在远端。核对结果见[shipping-github-publication.json](shipping-github-publication.json)。发布状态说明的后续提交不改变已验证的功能和证据字节。上传分支不等于已合并main。

独立重入挑战 `bonus/` 和4C材料不属于此次提交，仍保留本机原状态。正式报告排版与多签仲裁实验另行进行。


2026-11-03发布前，助手实际完整复测19项全部通过、0跳过/失败（Shipping EVM 8项），[本次记录](../evidence/shipping/local-validation/20261103T043807Z/summary.json)与10月30日本人原图/测试数据分别保留。
