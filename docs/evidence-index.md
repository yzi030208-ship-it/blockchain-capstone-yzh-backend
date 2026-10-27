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


## 2026-10-26 DHT 补齐代码截图与退款核对

本次新增 13 张原始截图副本，均与 DHT 本机 screenshots 原图 SHA-256 一致，未移动、改写原图。仅核对截图中实际可见内容；读取调用不产生新交易哈希。

| 文件 | 归属 | 实际可见内容与核对结果 |
| --- | --- | --- |
| [4A-1-01_DHT_配置环境变量.png](../evidence/4A/4A-1-01_DHT_配置环境变量.png) | 4A-1 | RPC、私钥环境变量占位符、gas 配置完整，无实际密钥值 |
| [4A-2-01_DHT_封装生成成功与生成文件列表.png](../evidence/4A/4A-2-01_DHT_封装生成成功与生成文件列表.png) | 4A-2 | 真实终端生成命令、Solidity 0.8.20、编译成功、四个封装、BUILD SUCCESSFUL in 4s 及生成文件列表 |
| [4A-2-02_DHT_部署字节码与函数常量.png](../evidence/4A/4A-2-02_DHT_部署字节码与函数常量.png) | 4A-2 | 43—73 行包含 BINARY、FUNC_ 及事件定义；此图第 72 行右侧裁切，完整参数见下方 4A-2-02b 补图 |
| [4A-2-02b_DHT_OrderCreated事件定义.png](../evidence/4A/4A-2-02b_DHT_OrderCreated事件定义.png) | 4A-2 补图 | 67—74 行，开启自动换行后完整显示 `ORDERCREATED_EVENT` 六个参数的类型及 indexed 标记；原图与副本 SHA-256 一致 |
| [4A-2-03_DHT_事件解码方法.png](../evidence/4A/4A-2-03_DHT_事件解码方法.png) | 4A-2 | 完整 getOrderCreatedEventFromLog，163—174 行 |
| [4A-2-04_DHT_Order结构.png](../evidence/4A/4A-2-04_DHT_Order结构.png) | 4A-2 | Order 字段与两个构造函数完整，454—498 行 |
| [4A-2-05_DHT_创建事件响应.png](../evidence/4A/4A-2-05_DHT_创建事件响应.png) | 4A-2 | OrderCreatedEventResponse 完整，504—516 行 |
| [4A-4-01_DHT_EscrowService_1.png](../evidence/4A/4A-4-01_DHT_EscrowService_1.png) | 4A-4 Service | 约 1—35 行，包、依赖、状态、构造函数 |
| [4A-4-01_DHT_EscrowService_2.png](../evidence/4A/4A-4-01_DHT_EscrowService_2.png) | 4A-4 Service | 35—83 行，deploy 和 load |
| [4A-4-01_DHT_EscrowService_3.png](../evidence/4A/4A-4-01_DHT_EscrowService_3.png) | 4A-4 Service | 84—129 行，current、只读访问器、getOrder、resolve、签名管理器及仲裁账户检查 |
| [4A-4-01_DHT_EscrowService_4.png](../evidence/4A/4A-4-01_DHT_EscrowService_4.png) | 4A-4 Service | 129—180 行，状态、字段转换、参数、回执与错误处理；四张连续覆盖完整 Service |
| [4A-S1_DHT_正常单确认前Paid.png](../evidence/4A/4A-S1_DHT_正常单确认前Paid.png) | 4A-S1 前置补图 | getOrder(0) 返回 status=1（Paid），正常单尚未确认；不可标成 Completed |
| [4A-S2-02_DHT_买家退款_1.png](../evidence/4A/4A-S2-02_DHT_买家退款_1.png) | 4A-S2-02 | 指定 YZHToken 的 balanceOf(买家) 返回 30000000000000000000，即 30 枚 |
| [4A-S2-02_DHT_买家退款_2.png](../evidence/4A/4A-S2-02_DHT_买家退款_2.png) | 4A-S2-02 | getOrder(1) 返回 status=3（Refunded），争议订单；只读查询不是新退款交易 |

退款余额截图的目标为指定代币 `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee`，买家 `0x5FeDaeB4369432aD2A9929EaFFA9762C46050d4C`；状态截图对应新 Escrow `0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5`。退款链上交易引用 YZH 已核验的 `0x86b5d80b974abd153f5b40a4e08f72f0d5657bf1fd048289eb621878fbbf9a92`，不是给只读截图另造哈希。截图时间以实际捕获为准，归档日期不代替交易时间。

## DHT 正常单确认新增截图

以下四张均由 DHT 本人提供，保留在本机 `screenshots/`，仓库副本逐字节 SHA-256 一致。确认交易已由 YZH 按区块独立查询，并以更正后的完整哈希再次读取课程 RPC 回执，核对区块 `2689699`、`status=0x1`、From DHT、To 本组 Escrow、gasUsed `62549`；截图与回执的对应关系见下表。

| 文件 | 画面实际内容 | 核对说明 |
| --- | --- | --- |
| [4A-S1-01_DHT_正常单确认前Paid.png](../evidence/4A/4A-S1-01_DHT_正常单确认前Paid.png) | 本组 Escrow 的 getOrder(0) 显示 status=1、正常订单 | 确认前只读状态，不产生交易哈希 |
| [4A-S1-02_DHT_confirm订单0参数.png](../evidence/4A/4A-S1-02_DHT_confirm订单0参数.png) | confirm(uint256) 输入 `0` | 参数图，不单独证明交易成功 |
| [4A-S1-03_DHT_confirm订单0成功回执.png](../evidence/4A/4A-S1-03_DHT_confirm订单0成功回执.png) | Remix 显示交易已挖出且执行完成；From DHT、To 新 Escrow；正确哈希 `0x4d1ab17ee3ede2c696944a2ace7d0f8e195949cdb6928b5883b1b3c162cd6f46`，区块 2689699，gas 62549 | 原始日志 2 条；YZH 按区块查询、DHT 交接侧再次通过课程 RPC 查询，均确认回执 `0x1` |
| [4A-S1-04_DHT_正常单Completed.png](../evidence/4A/4A-S1-04_DHT_正常单Completed.png) | 后续 getOrder(0) 显示 status=2（Completed）、正常订单 | 只读状态，与前一张成功回执合并证明正常单完成 |

原有 [退款余额图](../evidence/4A/4A-S2-02_DHT_买家退款_1.png) 与 [订单 1 Refunded 图](../evidence/4A/4A-S2-02_DHT_买家退款_2.png)仍是争议单退款证据，不把订单 0 确认误写为订单 1 的操作。YZH 的 4A-6/7 后端 PNG 已归档，见下节；ZZL 浏览器图仍待补。

## YZH 后端与仲裁截图（2026-10-26）

以下 PNG 是 YZH 桌面“大作业”文件夹原图的仓库副本。4A-6 的 JSON 文件画面是退款前响应存档；实时接口画面另列。订单列表接口展示创建事件，不包含当前状态，Paid/Disputed 以两份详情响应为准。

| 编号 | 截图 | 证明内容 |
| --- | --- | --- |
| 4A-6-01 | [订单列表存档](../evidence/4A/4A-6-01_YZH_订单列表.png) | 两笔创建事件响应 JSON 的可读画面，辅助证据 |
| 4A-6-01b | [订单列表实时接口](../evidence/4A/4A-6-01b_YZH_订单列表实时接口.png) | WSL 中实际调用 `GET /api/escrow/orders`，返回订单 0、1 |
| 4A-6-02a | [订单 0 退款前 Paid](../evidence/4A/4A-6-02a_YZH_订单0退款前Paid.png) | 退款前详情响应存档，订单 0 状态 Paid |
| 4A-6-02b | [订单 1 退款前 Disputed](../evidence/4A/4A-6-02b_YZH_订单1退款前Disputed.png) | 退款前详情响应存档，订单 1 状态 Disputed |
| 4A-7-01 | [未争议订单失败回执](../evidence/4A/4A-7-01_YZH_未争议resolve失败回执.png) | `resolve(0,true)` 已发生交易，回执 `status=0x0`；完整哈希在同名 JSON 中 |
| 4A-7-02 | [争议订单退款成功回执](../evidence/4A/4A-7-02_YZH_争议订单退款成功回执.png) | `resolve(1,false)` 回执 `status=0x1`，含代币 Transfer 与 `OrderResolved(false)` 日志 |
| 4A-7-03 | [订单 1 Refunded](../evidence/4A/4A-7-03_YZH_订单1_Refunded.png) | 退款后详情响应状态 Refunded |

失败与成功回执画面是 RPC 响应存档，不是区块浏览器截图。ZZL 的 4A-8 浏览器证据仍需另拍；DHT 的退款余额及订单 0 确认截图已在上节归档。
