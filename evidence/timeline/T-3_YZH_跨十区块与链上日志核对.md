# T-3：分批查询、链上日志与重启恢复

本文件记录真实接口/RPC 数据的核对过程，不代替报告中的 PNG 截图。

## 跨 10 区块

- 本次 Escrow 部署区块：`2672683`；退款区块：`2689270`。从部署到退款（两端包含）跨 `16588` 个区块，即使只扫到退款区块，也至少需要 `1659` 个最多 10 区块的窗口。实际列表查询时链头更晚，不能把 `1659` 写成当次精确窗口数。
- 订单 1 的真实事件分别位于区块 `2688698`、`2688820`、`2689118`、`2689270`，并非同一 10 区块窗口。后端 `GET /api/escrow/orders/1/events` 返回四条事件，原始响应见 [T-2 JSON](T-2_YZH_争议单时间线.json)。优化后的 `GET /api/escrow/orders` 约 40 秒返回两单，原始响应见 [4A-6 列表 JSON](../4A/4A-6-01_YZH_订单列表.json)。
- 实现仍将每次 `eth_getLogs` 限制为 10 区块，最多并发 8 个窗口；全部响应按区块号、交易索引、日志索引排序，再按交易哈希与日志索引去重。RPC 失败时接口显示错误，不返回伪空列表。相关现有测试通过。

## 与 RPC 回执 Logs 对照

- 后端订单 1 时间线最后一条：`OrderResolved`，区块 `2689270`，交易索引 `0`，日志索引 `1`，交易哈希 `0x86b5d80b974abd153f5b40a4e08f72f0d5657bf1fd048289eb621878fbbf9a92`，参数 `sellerWins=false`。
- 同一交易的 [RPC 成功回执](../4A/4A-7-02_YZH_争议订单退款成功回执.json) 中，Logs 第 2 条地址为本次 Escrow、`logIndex=0x1`、订单 id 的 indexed topic 末尾为 `1`，data 为 `0`（false）。Logs 第 1 条是指定 ERC20 从 Escrow 向 DHT 转出 30 枚的 Transfer。后端时间线没有额外的 `OrderRefunded`，与回执一致。

## 重启 load

2026-10-26 后端重启后调用 `POST /api/escrow/load`，仍传原地址 `0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5` 和原部署区块 `2672683`，返回同一合约地址、链上 arbiter `0x6717496fe22477b41e6ca07353c308ff38a61797` 及部署区块，见 [load JSON](T-3_YZH_重启load原Escrow.json)。之后重新查询列表与争议单时间线成功；没有重新部署。

YZH 的[10 区块分批代码截图](T-3-02_YZH_十区块分批并发与排序去重代码.png)和[重启 load 原响应存档截图](T-3-01_YZH_重启load原Escrow响应存档.png)已保存；ZZL 仍需在区块浏览器对照上述成功交易 Logs 并截图。`load` 文件图只证明此前响应存档，须结合此前重启后的接口调用证据说明。


## 2026-10-29 补充核验

ZZL 的既有浏览器详情和 Transfer 日志原图已从旧本机副本保留到 evidence/4A/；该日志图尚未拍到 Escrow 的 OrderResolved。时间线本次七条事件的实时回执和十区块窗口核验为 PASS，见 [总结](verification/20261029T145204614030Z/summary.json)。本次不声称重启后端或重扫完整历史；建议补图及完整证据范围见 [交付说明](../../docs/timeline-delivery.md)。
