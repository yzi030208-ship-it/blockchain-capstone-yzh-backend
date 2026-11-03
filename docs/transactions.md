# 实际交易记录

先登记 DHT 本人完成的五笔创建、授权及付款交易；2026-10-26 使用课程 RPC 核对了这五笔的 chainId=60600、From、To、区块、回执状态和事件金额/订单编号。正常单确认交易随后由 DHT 本人提供真实 Remix 截图，单独列出其核对来源。组长部署、准备转币、争议、仲裁与独立挑战等记录仍由 ZZL 汇总；不要将本表理解为整组已全部完成。

发起账户均为 DHT `0x5FeDaeB4369432aD2A9929EaFFA9762C46050d4C`。前两笔及两次 deposit 的目标为新 Escrow；approve 的目标为指定代币合约，spender 为新 Escrow。

| 操作 | 订单 id | 交易哈希 | 浏览器链接 | 区块 | 回执 | gasUsed |
| --- | --- | --- | --- | --- | --- | --- |
| createOrder(0) 正常订单 | 0 | `0x782a3aa446b05827ba109dbd3502edec6b60f2b93d72488011e4e6d55f6524b6` | [交易详情](https://scan-testnet.potos.hk/tx/0x782a3aa446b05827ba109dbd3502edec6b60f2b93d72488011e4e6d55f6524b6) | 2688642 | 0x1 | 80531 |
| createOrder(1) 争议订单 | 1 | `0xd2246b70a7f676b33c70fcf31f2f25baf5c1d0fd61e1ef9f111f921e2555a333` | [交易详情](https://scan-testnet.potos.hk/tx/0xd2246b70a7f676b33c70fcf31f2f25baf5c1d0fd61e1ef9f111f921e2555a333) | 2688698 | 0x1 | 80531 |
| approve 新 Escrow 共 60 枚 | 两单合计授权 | `0x279498c462501f23a680cacaf7b82a68771a47456a3c8cce1a43e5032d3f1014` | [交易详情](https://scan-testnet.potos.hk/tx/0x279498c462501f23a680cacaf7b82a68771a47456a3c8cce1a43e5032d3f1014) | 2688760 | 0x1 | 28423 |
| deposit(0) 付款 30 枚 | 0 | `0xe4e6c4089b9dde7fef35832cd2020f649ac5cad5842ccdc2df080f28d8247b46` | [交易详情](https://scan-testnet.potos.hk/tx/0xe4e6c4089b9dde7fef35832cd2020f649ac5cad5842ccdc2df080f28d8247b46) | 2688806 | 0x1 | 70217 |
| deposit(1) 付款 30 枚 | 1 | `0xdcfa559d8fed107287dbab3d29e6efd1b09872c0932f6d6d7371b35423287db7` | [交易详情](https://scan-testnet.potos.hk/tx/0xdcfa559d8fed107287dbab3d29e6efd1b09872c0932f6d6d7371b35423287db7) | 2688820 | 0x1 | 70217 |

所有 gasUsed 是 gas 数量，不是 POT 金额。浏览器详情 URL 按仓库已登记的浏览器地址及真实交易哈希构成；本次已核验的是 RPC 回执。截图路径和对应关系见 [截图索引](evidence-index.md)。

## DHT 正常单确认（按本人 Remix 截图记录）

| 操作 | 订单 id | 交易哈希 | 浏览器链接 | 区块 | 截图显示状态 | 截图显示 gasUsed |
| --- | --- | --- | --- | --- | --- | --- |
| confirm(0) 正常完成 | 0 | `0x4d1ab17ee3ede2c696944a2ace7d0f8e195949cdb6928b5883b1b3c162cd6f46` | [交易详情](https://scan-testnet.potos.hk/tx/0x4d1ab17ee3ede2c696944a2ace7d0f8e195949cdb6928b5883b1b3c162cd6f46) | 2689699 | YZH 按区块独立查询回执 `0x1`；DHT 后续 getOrder(0) 为 Completed(2) | 62549 |

来源：[DHT 成功回执截图](../evidence/4A/4A-S1-03_DHT_confirm订单0成功回执.png)及[确认后状态截图](../evidence/4A/4A-S1-04_DHT_正常单Completed.png)。YZH 按区块查询到正确哈希后，DHT 交接侧再次通过课程 RPC 的 `eth_getTransactionReceipt` 读取同一哈希，确认区块 `2689699`、回执 `0x1`、From 为 DHT、To 为本组 Escrow、gasUsed `62549`、日志 2 条。先前错误哈希来自截图转录失误，已更正。浏览器链接按公开浏览器 URL 与核实哈希构成，浏览器截图仍由 ZZL 负责。订单 1 已退款，不重复确认。


## ZZL 卖家发货扩展（独立合约、每单1枚）

本节仅适用于ShippingEscrow `0xbD37ceD07122Fa6b4e4Fe2c1b4Ac5BBc0c63B40E`，与上文原主业务30枚订单分开。公开角色见[发货配置](shipping-public-config.json)，原始成功/失败回执及Transfer核对见[evidence/shipping/transactions/](../evidence/shipping/transactions/)。下列均是已核对的真实交易，不包含两项无交易哈希的eth_call限制预执行。

| 操作 | id | 实际交易哈希 | 区块 | 回执 |
| --- | --- | --- | --- | --- |
| 部署 | — | `0x3d9d8578af3e7bd494fc1df62806fd1628a84d57867331e67aadd050365ea5be` | 2729018 | 0x1 |
| approve2枚 | 两单 | `0xd69d8e09a419c24dbcf1ac60a041c9d058f98934bcc12a0e9eb86d9598535fbd` | 2733741 | 0x1 |
| A 创建 | 0 | `0x6d9834ed87e5d0559ee1e32e308a11bafe4e87ed22c70f39bc81a3f44138bc11` | 2733701 | 0x1 |
| A 付款 | 0 | `0x514b02e7a0f238a71efa343c3d87cf4b2afbb0ee6bcee4a8171a84a2e1f98e92` | 2733777 | 0x1 |
| A 发货 | 0 | `0xc2dc02a4ad4e0e054c2fa6d6b76477ef17eb8f92112464a94516653f2c4e66e6` | 2733808 | 0x1 |
| A 确认收货 | 0 | `0x5bff8401efb274336efba076a3ddcbaa2c9486bbea03094d9389f214716991ae` | 2734016 | 0x1 |
| B 创建 | 1 | `0xe4db36290dbd5e17f4b454ef80f6c22b8ef62dda261f81979ea943b0dbf30b94` | 2733859 | 0x1 |
| B 付款 | 1 | `0xdb9201f80b61c98f0e559ce85ef21689ead699844b7fbe9c49aaf947b4ca15a2` | 2733903 | 0x1 |
| B 超期退款 | 1 | `0xd1553b94b32bf331e74b6257ca7017b7eb97e26838ebc10a9b7ed7287fa0a427` | 2733983 | 0x1 |
| B未到期退款（实际失败） | 1 | `0x432cea4867bee9abc398848e2b2b2c1519beb76d9ff8b2bdd5b7ba237d203c27` | 2733962 | 0x0 |

B失败交易时间10:20:47，比10:21:40截止早53秒；后续10:23:02超期退款成功。A发货10:02:34早于10:07:41截止，超期后退款被状态限制，最终10:27:08确认并向卖家转1枚。时间均为2026-10-30 Asia/Shanghai。
