# 4.5 订单时间线

本组选择路线 A。YZH 已完成原有订单时间线接口与事件查询；本轮由 ZZL 接手验收、补图、证据归档及本节整理。接口为 `GET /api/escrow/orders/{id}/events`，根据 Escrow 的真实链上日志重建订单历史。DHT 已完成的合约与买家操作沿用原分工登记。

查询使用已加载的新 Escrow 地址 `0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5`，从部署区块 `2672683` 查到请求时固定的链头。每次 eth_getLogs 最多查 10 个区块，最多并发 8 个窗口。用合约地址、事件签名和 indexed 订单 id 限定结果，再按区块号、交易索引和日志索引排序，以交易哈希加日志索引去重。这样既满足课程 RPC 范围限制，也保留同一交易的不同事件。

【截图 T-1】使用 `evidence/timeline/T-1_YZH_正常单完整时间线_实时接口.png`。

正常订单 0 的三条事件依次是 OrderCreated（区块 2688642）、OrderPaid（2688806）、OrderCompleted（2689699）。确认交易哈希为 `0x4d1ab17ee3ede2c696944a2ace7d0f8e195949cdb6928b5883b1b3c162cd6f46`。三条事件跨越多个查询窗口，完整时间线仍按数值区块号顺序返回。

【截图 T-2】使用 `evidence/timeline/T-2_YZH_争议单完整时间线_实时接口.png`。

争议订单 1 的四条事件依次是 OrderCreated（2688698）、OrderPaid（2688820）、OrderDisputed（2689118）、OrderResolved（2689270）。最后一条的 sellerWins=false，交易哈希为 `0x86b5d80b974abd153f5b40a4e08f72f0d5657bf1fd048289eb621878fbbf9a92`。业务结果为 Refunded；该仲裁方法发出 OrderResolved 事件，不会另发 OrderRefunded。时间线保留真实事件名称，未根据状态编造退款日志。

【补充截图 T-3-02】可使用 `evidence/timeline/T-3-02_YZH_十区块分批并发与排序去重代码.png` 解释分批处理。

【补充截图 T-3-03】使用 ZZL 提供的 `evidence/timeline/T-3-03_ZZL_OrderResolved浏览器日志.png`。画面显示本组 Escrow、orderId=1、OrderResolved 和全零布尔 data。浏览器将布尔参数显示为 refundToBuyer，本组源码该参数为 sellerWins；参数名不参与事件签名哈希，故以原始 data=false 和本组源码解释为 sellerWins=false。

【补充截图 T-3-04】使用 `evidence/timeline/T-3-04_ZZL_退款Transfer浏览器日志.png`，value=30000000000000000000 即 30 枚，显示由 Escrow 转给买家。两张图均未拍到交易哈希，和已有 4A-8-01 交易详情图及已核验 RPC 回执共同说明交易身份，不把浏览器的参数名直接当作本组源码语义。

2026-10-29 22:52（Asia/Shanghai），本组用只读核验脚本将已保存的两单 API 响应共七条事件与当前 POTOS Testnet 回执逐一对照，验证事件签名、indexed 参数、非 indexed 参数、区块及日志位置，并用最多 10 区块的 eth_getLogs 查询复核。链编号为 60600，本轮读取链头为 2728278；eth_call 读得订单 0 Completed、订单 1 Refunded。核验通过。该过程复核的是响应存档和实时链上数据，没有声称本轮重启后端或重扫全部历史。

Java 验收覆盖全部六类事件解码、数值排序、去重、removed 日志排除、订单过滤及 HTTP 参数/错误响应；8 项通过，1 项本地 EVM 场景按原配置跳过。六事件测试数据为模拟 ABI 数据，不属于本组测试网订单历史。

AI 使用说明补充：使用 Codex 核对作业要求、同步 GitHub、补充测试及只读核验脚本、整理证据。AI 最初误把当前操作者 ZZL 当成 YZH，已按本人澄清修正本轮分工，保留历史代码与截图作者；此外，AI 依据旧下载副本提出“DHT 可能还需确认正常单”，对比 GitHub 新版证据和实时链上状态后，确认订单 0 已完成，及时修正，不重复发送交易。新增测试也修正了并发窗口测试依赖请求到达顺序的问题。


## 可用于 4.6 的实际问题记录

ZZL 在 CMD 中运行了按 PowerShell 给出的命令，因开头的 & 调用符出现“此时不应有 &”。终端标题和 C:\...> 提示符表明这是 CMD；移除开头的 &，保留带引号的 Python 路径即可使用 CMD 语法。失败原图保存在 evidence/timeline/issues/；该次脚本未启动。ZZL 按修正后的 CMD 命令再次执行，已取得 RESULT=PASS 的 T-4-01 原图，问题解决。


【补充截图 T-4-01】使用 ZZL 本人执行脚本的 `evidence/timeline/T-4-01_ZZL_本人链上时间线核验PASS.png`。2026-10-29 23:20（Asia/Shanghai），RESULT=PASS，mode=archived-api-and-live-receipts，读取链头 2728523；订单 0 Completed、3 条事件，订单 1 Refunded、4 条事件。原始数据单独保存在 verification/20261029T152047601888Z/，与此前 AI 协助核验的运行目录区分。


【补充截图 T-4-02】使用 ZZL 提供的 `evidence/timeline/T-4-02_ZZL_Java测试构建成功.png`。截图显示 BUILD SUCCESSFUL in 10s，4 个任务实际执行。2026-10-29 23:26（Asia/Shanghai）生成的 JUnit XML 共 9 项，8 通过、1 项原本地 EVM 测试跳过、0 失败和错误；按类分别为事件查询 3 项、时间线 HTTP 3 项、Service 2 项通过，LocalIntegration 1 项跳过。本轮未运行 Solidity 编译和 Java 封装生成，不能将构建成功写成完整 Solidity 或本地 EVM 验证。原始测试 XML 与总结单独归档，不覆盖先前运行数据。

本小节的主要图片已齐：T-1/T-2 为已有 YZH 真实接口图，T-3-03/T-3-04/T-4-01/T-4-02 为 ZZL 本人补充验收图；正式报告按需插入，保留实际贡献与截图人。
