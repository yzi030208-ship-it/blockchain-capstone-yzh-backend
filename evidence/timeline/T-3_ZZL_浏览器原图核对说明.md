# ZZL 浏览器日志原图核对记录

ZZL 在本次聊天提供两张原图，已原样复制，不裁切或修改。接收归档时间：2026-10-29T23:18:52+08:00（Asia/Shanghai）；不把接收时间冒充交易时间。

| 编号 | 画面内容 | 核对结论 |
| --- | --- | --- |
| T-3-03 | 本组 Escrow 地址、OrderResolved、orderId=1；topic0=0x649ff37700d4eb8a5bc86ef06a55183597fa5c3c6b53ec4b9ee510cb2089ee26，布尔 data 为全零 | 与已核验回执的 Escrow logIndex=1 一致 |
| T-3-04 | YZHToken Transfer，from 是本组 Escrow，to 为买家地址缩略显示，value=30000000000000000000 | 18 位精度下为 30 枚；结合此前完整地址图和原始回执证明退款 |

两张新图未拍到交易哈希或地址栏，因此不能单独作为交易身份的完整证据。报告中与已有 4A-8-01（完整哈希、Success、区块 2689270）及本组原始 RPC 回执交叉引用。当前报告无需仅为此重复拍同一页面；如另拍整套独立图，可补上地址栏。

浏览器解码显示 `OrderResolved(uint256 indexed orderId, bool refundToBuyer)`，布尔值 false；本组源码和 Java 封装定义为 `OrderResolved(uint256 indexed id, bool sellerWins)`。事件签名哈希只由事件名称与参数类型决定，不包含参数名，所以布尔类型和原始字节可核对，但不能仅按浏览器给出的参数名解释业务方向。当前已确认原始 data=0，按本组源码解释为 sellerWins=false，再由 ERC20 Transfer 确认代币退给买家。未修改浏览器画面或把其标签抄成源码字段。

后续报告表述建议：订单 1 发出 OrderResolved，原始布尔值为 false；按本组合约对应 sellerWins=false；同一回执的 Transfer 显示 Escrow 将 30 枚代币退回买家。浏览器的参数名显示与本组源码不同。
