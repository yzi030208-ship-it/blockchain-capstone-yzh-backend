# 实际交易记录

先登记 DHT 本人已完成的五笔交易，2026-10-26 使用课程 RPC 核对 chainId=60600、From、To、区块、回执状态和事件金额/订单编号。后续由 ZZL 追加组长部署、准备转币、争议、仲裁、确认与独立挑战等实际交易；不要将本表理解为整组已全部完成。

发起账户均为 DHT `0x5FeDaeB4369432aD2A9929EaFFA9762C46050d4C`。前两笔及两次 deposit 的目标为新 Escrow；approve 的目标为指定代币合约，spender 为新 Escrow。

| 操作 | 订单 id | 交易哈希 | 浏览器链接 | 区块 | 回执 | gasUsed |
| --- | --- | --- | --- | --- | --- | --- |
| createOrder(0) 正常订单 | 0 | `0x782a3aa446b05827ba109dbd3502edec6b60f2b93d72488011e4e6d55f6524b6` | [交易详情](https://scan-testnet.potos.hk/tx/0x782a3aa446b05827ba109dbd3502edec6b60f2b93d72488011e4e6d55f6524b6) | 2688642 | 0x1 | 80531 |
| createOrder(1) 争议订单 | 1 | `0xd2246b70a7f676b33c70fcf31f2f25baf5c1d0fd61e1ef9f111f921e2555a333` | [交易详情](https://scan-testnet.potos.hk/tx/0xd2246b70a7f676b33c70fcf31f2f25baf5c1d0fd61e1ef9f111f921e2555a333) | 2688698 | 0x1 | 80531 |
| approve 新 Escrow 共 60 枚 | 两单合计授权 | `0x279498c462501f23a680cacaf7b82a68771a47456a3c8cce1a43e5032d3f1014` | [交易详情](https://scan-testnet.potos.hk/tx/0x279498c462501f23a680cacaf7b82a68771a47456a3c8cce1a43e5032d3f1014) | 2688760 | 0x1 | 28423 |
| deposit(0) 付款 30 枚 | 0 | `0xe4e6c4089b9dde7fef35832cd2020f649ac5cad5842ccdc2df080f28d8247b46` | [交易详情](https://scan-testnet.potos.hk/tx/0xe4e6c4089b9dde7fef35832cd2020f649ac5cad5842ccdc2df080f28d8247b46) | 2688806 | 0x1 | 70217 |
| deposit(1) 付款 30 枚 | 1 | `0xdcfa559d8fed107287dbab3d29e6efd1b09872c0932f6d6d7371b35423287db7` | [交易详情](https://scan-testnet.potos.hk/tx/0xdcfa559d8fed107287dbab3d29e6efd1b09872c0932f6d6d7371b35423287db7) | 2688820 | 0x1 | 70217 |

所有 gasUsed 是 gas 数量，不是 POT 金额。浏览器详情 URL 按仓库已登记的浏览器地址及真实交易哈希构成；本次已核验的是 RPC 回执。截图路径和对应关系见 [截图索引](evidence-index.md)。
