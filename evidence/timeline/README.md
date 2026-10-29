# 订单时间线证据索引

本组时间线编号用于报告 4.5，不属于老师路线 A 的 4A-1 至 4A-8。正常单和争议单已完成，历史中“待 confirm”的说明不代表当前状态。2026-10-29 已结合原图与实时公开 RPC 复核。

| 编号 | 原始材料 | 证明内容 | 状态 |
| --- | --- | --- | --- |
| T-1 | [正常单实时接口原图](T-1_YZH_正常单完整时间线_实时接口.png)、[完整 JSON](T-1_YZH_正常单完整时间线.json) | 订单 0 的三事件、区块与完整哈希，最后为 OrderCompleted | 原图清晰，和链上回执匹配；主图 |
| T-2 | [争议单实时接口原图](T-2_YZH_争议单完整时间线_实时接口.png)、[完整 JSON](T-2_YZH_争议单时间线.json) | 订单 1 四事件，最后 OrderResolved，sellerWins=false | 原图清晰，和链上回执匹配；主图 |
| T-3-01 | [旧 load 响应原图](T-3-01_YZH_重启load原Escrow响应存档.png)、[JSON](T-3_YZH_重启load原Escrow.json) | 相同合约、arbiter 和部署区块 2672683 | 文件画面，只作历史恢复补充，不冒充本轮实时重启 |
| T-3-02 | [分批并发及排序去重代码原图](T-3-02_YZH_十区块分批并发与排序去重代码.png) | 每批最多 10 区块，排序、去重 | 已有代码补图 |
| T-3-03 | [ZZL OrderResolved 原图](T-3-03_ZZL_OrderResolved浏览器日志.png) | Escrow 地址、订单 1、布尔原始值 false、topics/data | 已收到并匹配回执；浏览器参数名差异见核对说明 |
| T-3-04 | [ZZL 退款 Transfer 原图](T-3-04_ZZL_退款Transfer浏览器日志.png) | Escrow 退给买家 30 枚代币 | 已收到；与 4A-8-01 及原始回执结合使用 |
| T-4-01 | [ZZL 本人核验 PASS 原图](T-4-01_ZZL_本人链上时间线核验PASS.png)、[本次总结](verification/20261029T152047601888Z/summary.json) | 用户实际运行脚本，七条事件回执/窗口核验 PASS，订单 0 Completed、订单 1 Refunded | 2026-10-29 23:20（Asia/Shanghai），chainHead=2728523；核验存档 API 与实时回执，不声称启动后端 |
| T-4-02 | [ZZL Java 测试成功原图](T-4-02_ZZL_Java测试构建成功.png)、[本次测试总结](validation/ZZL-20261029T152619Z/summary.json) | BUILD SUCCESSFUL in 10s，核对 XML 为 8 通过、1 跳过、0 失败/错误 | 2026-10-29 23:26（Asia/Shanghai）；本轮 Java 测试，不冒充完整 Solidity/EVM 验证 |
| 4A-8-01 复用 | [浏览器交易详情](../4A/4A-8-01_ZZL_resolve浏览器详情.png) | 成功退款哈希、区块 2689270、仲裁人 From、Escrow To、30 枚退款 | 从旧副本保留的 ZZL 原图，已目视核对 |
| 4A-8-02 复用 | [浏览器 Transfer 日志](../4A/4A-8-02_ZZL_resolve日志.png) | Escrow 向买家转出 30 枚指定代币 | 原图只显示 ERC20 日志，不证明 Escrow OrderResolved 的 topics |
| 历史 | [正常单确认前响应](T-1_YZH_正常单确认前时间线.json) | 确认前仅 Created、Paid | 只作历史阶段材料，不能替代完整 T-1 |
| 文字记录 | [跨十区块及旧重启记录](T-3_YZH_跨十区块与链上日志核对.md) | 历史核对过程 | 补充说明 |
| 本轮 RPC 核验 | [结果总结](verification/20261029T145204614030Z/summary.json) 及同目录回执、日志窗口、eth_call | 七条已记录事件与实时链上数据匹配，订单 0 Completed、订单 1 Refunded | PASS；不是本轮全历史扫描或启动后端 |
| 本轮 Java 测试 | [测试结果目录](validation/) | 8 项通过，1 项原本地 EVM 场景跳过 | 测试 XML 是机器结果，不是截图 |

原图文件未编辑。各文件 SHA256 见 [文件清单](manifest.json)。ZZL 四张成功验收新图和一张问题原图均已入清单；[原图核对说明](T-3_ZZL_浏览器原图核对说明.md)记录浏览器参数名差异及未拍到交易哈希的范围。

截图步骤及复现命令见 [交付说明](../../docs/timeline-delivery.md)。截图由用户提供；本轮没有生成替代截图。


[CMD 调用问题记录](issues/README.md)及[ZZL 原图](issues/T-I-01_ZZL_CMD误用PowerShell调用符.png)作为报告 4.6 的辅助材料；不是核验成功截图。


## 本项截图收齐状态

订单时间线所用 T-1/T-2 真实接口图（原作者 YZH）可复用。ZZL 补拍的 OrderResolved、Transfer、本人 RPC 核验 PASS、Java BUILD SUCCESSFUL 均已入库，主要验收截图已齐，无需继续补拍。本轮报告说明、原始数据、问题与修正及测试结果均已对应整理。此结论仅针对订单时间线，不代表独立重入挑战或整份课程报告完成。尚未自动提交或推送 GitHub。
