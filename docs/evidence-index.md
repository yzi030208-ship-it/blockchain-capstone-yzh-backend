# 截图证据索引

本次先登记 DHT 已交付的 10 张真实原图。YZH 和 ZZL 的其他材料按各自已取得证据追加，未取得的截图不标完成。截图均归入报告 4A-5，状态查询补图不替代后续后端 Paid/Disputed 的 4A-6。

共同信息：截图人 DHT；本次归档日期 2026-10-26；Escrow `0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5`；代币 `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee`。具体截图时间未逐张登记，不能用文件修改时间冒充链上时间。

| 编号与子图 | 文件 | 证明内容 | 订单 id | 对应交易 | 核对状态 |
| --- | --- | --- | --- | --- | --- |
| 4A-5-02_DHT_两单创建_1 | [原图](../evidence/4A/4A-5-02_DHT_两单创建_1.png) | 正常单创建参数 | 0 | `0x782a3aa446b05827ba109dbd3502edec6b60f2b93d72488011e4e6d55f6524b6` | 原图与副本 SHA-256 一致；对应交易 RPC 回执成功 |
| 4A-5-02_DHT_两单创建_2 | [原图](../evidence/4A/4A-5-02_DHT_两单创建_2.png) | 正常单创建成功回执 | 0 | `0x782a3aa446b05827ba109dbd3502edec6b60f2b93d72488011e4e6d55f6524b6` | 原图与副本 SHA-256 一致；对应交易 RPC 回执成功 |
| 4A-5-02_DHT_两单创建_3 | [原图](../evidence/4A/4A-5-02_DHT_两单创建_3.png) | 争议单创建参数 | 1 | `0xd2246b70a7f676b33c70fcf31f2f25baf5c1d0fd61e1ef9f111f921e2555a333` | 原图与副本 SHA-256 一致；对应交易 RPC 回执成功 |
| 4A-5-02_DHT_两单创建_4 | [原图](../evidence/4A/4A-5-02_DHT_两单创建_4.png) | 争议单创建成功回执 | 1 | `0xd2246b70a7f676b33c70fcf31f2f25baf5c1d0fd61e1ef9f111f921e2555a333` | 原图与副本 SHA-256 一致；对应交易 RPC 回执成功 |
| 4A-5-03_DHT_approve新Escrow_1 | [原图](../evidence/4A/4A-5-03_DHT_approve新Escrow_1.png) | 授权参数与成功回执 | 0、1 | `0x279498c462501f23a680cacaf7b82a68771a47456a3c8cce1a43e5032d3f1014` | 原图与副本 SHA-256 一致；对应交易 RPC 回执成功 |
| 4A-5-03_DHT_approve新Escrow_2 | [原图](../evidence/4A/4A-5-03_DHT_approve新Escrow_2.png) | allowance 读取为 60 枚 | 0、1 | `0x279498c462501f23a680cacaf7b82a68771a47456a3c8cce1a43e5032d3f1014` | 原图与副本 SHA-256 一致；对应交易 RPC 回执成功 |
| 4A-5-04_DHT_两单deposit_1 | [原图](../evidence/4A/4A-5-04_DHT_两单deposit_1.png) | deposit(0) 成功回执与日志 | 0 | `0xe4e6c4089b9dde7fef35832cd2020f649ac5cad5842ccdc2df080f28d8247b46` | 原图与副本 SHA-256 一致；对应交易 RPC 回执成功 |
| 4A-5-04_DHT_两单deposit_2 | [原图](../evidence/4A/4A-5-04_DHT_两单deposit_2.png) | deposit(1) 成功回执与日志 | 1 | `0xdcfa559d8fed107287dbab3d29e6efd1b09872c0932f6d6d7371b35423287db7` | 原图与副本 SHA-256 一致；对应交易 RPC 回执成功 |
| 4A-5-04_DHT_两单deposit_3_1 | [原图](../evidence/4A/4A-5-04_DHT_两单deposit_3_1.png) | getOrder(0) 返回 status=1，正常订单 | 0 | `0xe4e6c4089b9dde7fef35832cd2020f649ac5cad5842ccdc2df080f28d8247b46` | 原图与副本 SHA-256 一致；对应交易 RPC 回执成功 |
| 4A-5-04_DHT_两单deposit_3_2 | [原图](../evidence/4A/4A-5-04_DHT_两单deposit_3_2.png) | getOrder(1) 返回 status=1，争议订单 | 1 | `0xdcfa559d8fed107287dbab3d29e6efd1b09872c0932f6d6d7371b35423287db7` | 原图与副本 SHA-256 一致；对应交易 RPC 回执成功 |

参数截图本身不是成功回执，需要与同单回执截图结合。allowance/getOrder 是只读调用，不产生新的交易哈希；表中列的是所对应的授权/付款交易。金额与订单 id 已按回执事件核对。
