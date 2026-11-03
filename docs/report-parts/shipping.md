# 卖家发货状态：报告 4.5 素材

本片段依据 ZZL 本人的原始截图、真实 POTOS Testnet 回执、后端响应及本地测试结果整理。实验交易及SH-01至SH-10关键截图收集均已完成。最终本人运行原图、对应JSON和独立链上核对记录均已归档，可用于正式报告4.5排版。SH 编号为本组整理编号，并非课程规定编号。

## 实现与人员分工

ZZL 一人控制三个独立测试账户，完成买家、卖家和仲裁账户的操作及截图，不需要三个人。买家为 `0x28D4cD8504A89249087A5b5480e4960aF68d378d`，卖家为 `0x6FE3bbA93ab02387c4261Fef249dA3266f1AAa36`，仲裁人为 `0x73705dabCDb0fD57089cAEcD34C44d0cE9458Ed9`。部署、买家付款/退款/确认、卖家发货的真实交易 From 分别对应这些账户。本轮未发生仲裁交易。

基于本组 Escrow 设计新增独立 ShippingEscrow。枚举追加 Shipped=5，保留旧状态0至4。markShipped(id) 要求调用者是订单卖家、状态为 Paid 且未超过 deadline，成功改为 Shipped 并发出 OrderShipped。过期后不能补发货阻断退款。confirm(id) 要求买家且状态为 Shipped，成功改为 Completed 并将托管代币转给卖家；refund(id) 要求买家、Paid 且已超过 deadline，故已发货订单无法超期退款。Paid/Shipped 仍保留争议和仲裁入口。

生成独立 Java 封装，新增 `/api/shipping/escrow` 上下文、文字状态查询及含 OrderShipped 的七事件时间线。load 检查 SHIPPING_VERSION，避免加载旧合约。卖家通过钱包签名发货，后端提供查询和仲裁功能。

## 实际环境与部署

Solidity 0.8.20、EVM Paris，Java 21、Gradle 8.13。POTOS Testnet chainId=60600，结算代币为 `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee`，18位小数。

ShippingEscrow 地址为 `0xbD37ceD07122Fa6b4e4Fe2c1b4Ac5BBc0c63B40E`，部署区块2729018，交易 `0x3d9d8578af3e7bd494fc1df62806fd1628a84d57867331e67aadd050365ea5be`，回执0x1。部署输入与生成字节码及仲裁人构造参数逐字节匹配，SHIPPING_VERSION=1、arbiter正确。部署原始回执及核验位于 evidence/shipping/deployment/。

## 实际结果与截图安排

所有下列时间为 2026-10-30 Asia/Shanghai。两笔订单均为1枚代币，原始金额1000000000000000000。

| 证明内容 | 实际结果 | 用户原图/原始材料 |
| --- | --- | --- |
| 合约状态、权限、发货及退款实现 | Shipped追加、onlySeller、Paid/日期限制、confirm和refund状态条件 | SH-01a/b/c |
| 编译、生成及更新测试 | 本人运行19项通过，0跳过、0失败，其中8项真实本地Shipping EVM测试 | SH-02，local-validation/20261029T160955Z/ |
| 新合约、网络、角色、版本 | 独立部署成功，版本1，arbiter匹配；Remix连接POTOS | SH-03a至SH-03d4 |
| A创建并付款 | A=id0，描述ZZL shipping A，截止10:07:41，已付款 | SH-04a/b/c及交易回执 |
| B创建并付款 | B=id1，描述ZZL shipping B unshipped，截止10:21:40，已付款且未发货 | SH-04d/SH-04e-01/02 |
| 非卖家不能发货 | 买家对Paid的B调用markShipped的真实eth_call按caller is not the seller回滚 | SH-05/SH-07合并原图及restriction-checks/20261030T021646896852Z/ |
| 卖家正确发货 | A在10:02:34由卖家markShipped，回执成功，发出OrderShipped(0)，接口返回Shipped | SH-06a/c及transactions/shipping-verification.json |
| 已发货且超期不能退款 | 10:16:35区块时间已超过A截止，A仍Shipped，买家refund(A)真实eth_call按wrong status回滚 | 同一SH-05/SH-07原图，复用不重复复制 |
| 未发货超期可以退款 | B在10:23:02由买家refund(1)成功，OrderRefunded(1)，1枚从托管退回买家，接口Refunded | SH-08-03及transactions/B-refund-success-verification.json |
| 确认与卖家收款 | A在10:27:08由买家confirm(0)成功，OrderCompleted(0)，1枚从托管转卖家，接口Completed | SH-09及transactions/A-confirm-success-verification.json |
| 最终接口时间线和余额 | A为Created→Paid→Shipped→Completed；B为Created→Paid→Refunded。买家29枚、卖家1枚、托管0枚 | 本人SH-10原图，checkpoints/20261030T023111360218Z/snapshot.json；FINAL_PASS，7条事件日志及3项同区块余额独立核对一致 |

本地链测试使用临时chainId=31337，与POTOS实际交易区分。权限及已发货超期退款的两项限制是只读eth_call预执行，未签名、未广播、没有失败交易哈希或gas消耗。

## 关键成功交易

订单A（id=0）：

- 创建：`0x6d9834ed87e5d0559ee1e32e308a11bafe4e87ed22c70f39bc81a3f44138bc11`。
- 付款：`0x514b02e7a0f238a71efa343c3d87cf4b2afbb0ee6bcee4a8171a84a2e1f98e92`。
- 卖家发货：`0xc2dc02a4ad4e0e054c2fa6d6b76477ef17eb8f92112464a94516653f2c4e66e6`。
- 确认收货：`0x5bff8401efb274336efba076a3ddcbaa2c9486bbea03094d9389f214716991ae`。

订单B（id=1）：

- 创建：`0xe4db36290dbd5e17f4b454ef80f6c22b8ef62dda261f81979ea943b0dbf30b94`。
- 付款：`0xdb9201f80b61c98f0e559ce85ef21689ead699844b7fbe9c49aaf947b4ca15a2`。
- 超期退款：`0xd1553b94b32bf331e74b6257ca7017b7eb97e26838ebc10a9b7ed7287fa0a427`。

## 实际问题与 AI 使用

部署后和订单查询曾出现 timeout / Connect timed out，后续只读重试恢复。使用真实回执确定原交易已成功，没有因查询超时重复部署、付款或发货。

B的首次退款实际发生在10:20:47，比10:21:40截止早53秒，失败回执0x0、gasUsed28021，钱包提示deadline not reached。它是实际链上失败交易，会消耗gas；与只读预执行的两项回滚不同。之后10:23:02退款实际成功，回执0x1。成功后出现额外退款请求的估算失败弹窗，不影响已完成退款，取消额外请求即可。故障原图及说明位于evidence/shipping/issues/。

Codex协助代码、测试、公开链数据核验、操作指导及原图归档。首次测试清理钩子遗漏HttpService.close的异常声明，修正后完整19项通过；WSL首次下载慢，复用本机Gradle缓存完成离线验证。测试网钱包操作和签名由ZZL本人完成。所有用户截图原样复制，SHA256登记在screenshot-receipts.json；manifest.json用于归档完整性核对。未制作替代实验截图，也未将计划输入或预期输出写成实测。
