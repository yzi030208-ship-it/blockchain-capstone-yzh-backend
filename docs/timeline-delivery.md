# 订单时间线加分项交付说明

**GitHub 同步状态（2026-11-03复核）：** [PR #4](https://github.com/yzi030208-ship-it/blockchain-capstone-yzh-backend/pull/4) 已于北京时间2026-11-02 17:47:04合并至 `main`，合并提交为 `cfaf0ce`。远端main的时间线代码、脚本、说明和证据均存在，清单涉及的68个文件SHA256全部匹配。卖家发货扩展和独立重入挑战仍仅保留本机；两项加分内容的同步状态见 [本次核查](github-sync-check-2026-11-03.md)。

2026-10-29，基于 GitHub main `a76515a1516b0ee354f7b9c996b102f42ce8494d` 核验。该版本已经实现订单时间线，并已有两单完成后的真实接口截图。本轮补齐验收测试、只读链上核验脚本和报告证据索引，不重复创建订单或发送交易。

## 人员分工

当前操作者已确认为 ZZL。本项后续验收、补图及报告整理可以由 ZZL 一个人完成。已有 YZH 接口代码和截图、DHT 合约及买家操作仍按实际作者登记；本轮接手不改变历史贡献。

| 人员 | 本项职责 | 目前是否需要操作 |
| --- | --- | --- |
| ZZL | 接手时间线维护、运行测试、只读核对链上事件、补图及归档、撰写报告 4.5 小节 | 是，一人可完成本轮收尾 |
| DHT | 提供已完成的买家确认交易和 Completed 证据 | 已有，不需重复 confirm |
| YZH | 已交付时间线接口和真实 API 截图，本轮复用并保留作者记录 | 无需新增操作 |

## 作业要求与本次范围

老师 PDF 第 16 页“订单时间线”一项标为 3 分：路线 A 增加 `GET /api/escrow/orders/{id}/events`，返回该订单全部事件并按区块排序。该项不要求新增路线 B 前端。评分最终以课程任务书为准。

报告模板 4.5 要求每项独立一节，含截图和说明。`T-1/T-2/T-3` 是本组编号，老师没有为时间线指定这组编号。报告原件和评分页本轮未修改。浏览器对照、重启恢复是本组补充验收，不应误写成老师为本项新增的强制截图。

## 实现和验证结果

- 路由：`EscrowController.orderEvents`；查询与解码：`EscrowEventQueryService.timeline`。
- 支持 `OrderCreated`、`OrderPaid`、`OrderCompleted`、`OrderRefunded`、`OrderDisputed`、`OrderResolved` 六类合约事件。实际某单只返回该单真实发出的事件。
- 从已加载合约的部署区块查到请求时固定链头，每次 `eth_getLogs` 最多 10 个区块，最多并发 8 个窗口。通过合约地址、事件签名及 indexed 订单 id 过滤。
- 返回字段含订单 id、事件名、区块号、交易索引、日志索引、交易哈希和解码参数。链上整数使用十进制字符串；按数值区块号、交易索引、日志索引排序，并按交易哈希和日志索引去重；排除 removed 日志。RPC 失败不返回成功空列表。
- 本轮 Java 测试共 9 项：8 项通过、1 项本地 EVM 集成测试按原配置跳过。事件查询 3 项、时间线路由 3 项、原核心 Service 2 项通过。没有重新执行 Solidity 编译或本地 EVM 场景。
- 新增六事件 ABI 解码、同区块排序、同交易不同日志保留、重复/removed 日志排除及订单过滤验证；HTTP 测试覆盖响应、非法 id 和 RPC 错误。原测试不再依赖并发请求到达 RPC 的先后顺序。
- 2026-10-29 22:52（Asia/Shanghai）只读核验：链编号 60600、固定读到链头 2728278；两单现有 7 条事件逐条与实时回执、事件 topic、参数及最多 10 区块日志窗口匹配。另用 `eth_call getOrder` 复核最终状态。

| 订单 | 已核验事件顺序 | 当前状态 |
| --- | --- | --- |
| 0 | OrderCreated(2688642) → OrderPaid(2688806) → OrderCompleted(2689699) | Completed |
| 1 | OrderCreated(2688698) → OrderPaid(2688820) → OrderDisputed(2689118) → OrderResolved(2689270, sellerWins=false) | Refunded |

仲裁退款发出的是 `OrderResolved(false)`，并非 `OrderRefunded`；后者对应买家超期退款方法。不能为了凑齐六类事件而在本组两单时间线里加入不存在的日志。

本轮没有正在运行的后端进程，因此核验对象是“已有真实 API 响应存档 + 当前实时链上回执”，不是本轮再次启动后端或重启恢复。测试中的六事件数据是人工构造的 ABI 测试数据，不作为测试网交易证据。

## 可重复执行的命令

在本 Git 工作副本根目录执行：

```powershell
.\gradlew.bat test --console=plain
python scripts/verify-timeline.py
```

Python 脚本只使用标准库，不读私钥、不发交易。每次结果分别写入 `evidence/timeline/verification/<UTC时间>/`，包含原始回执、10 区块窗口、订单只读响应与总结，不覆盖原证据。

如果后端已由本人在原本的环境启动并 load 原 Escrow，可执行：

```bash
python3 scripts/verify-timeline.py --api http://127.0.0.1:8080
curl -sS http://127.0.0.1:8080/api/escrow/orders/0/events
curl -sS http://127.0.0.1:8080/api/escrow/orders/1/events
```

`--api` 会先核对后端加载的合约和部署区块，再取两单当前 API 时间线核验。只有这种运行模式才可称为本次实时接口验收。长历史扫描可能需要等待。Windows PowerShell 用 `curl.exe`；服务在 WSL 运行时，在同一 WSL 调用。

## 截图状态与补图步骤（以下操作已由 ZZL 完成）

已有 T-1、T-2 真实接口截图已目视核对，事件、区块与本轮回执一致，可直接用于报告 4.5。已有分批代码图和旧 load 响应图也已分类保留。无需为本项重新执行创建、付款、确认或仲裁。

建议再补 **1 张 Escrow 的 OrderResolved 日志截图**，用于 T-3 的链上对照。它是本组加强证据，不是老师额外指定的强制编号。现有 `4A-8-02_ZZL_resolve日志.png` 只拍到 ERC20 Transfer，尚未拍到下一条 Escrow 事件。

1. 打开 [已成功的退款交易](https://scan-testnet.potos.hk/tx/0x86b5d80b974abd153f5b40a4e08f72f0d5657bf1fd048289eb621878fbbf9a92)，点击 **Logs**。
2. 向下找到地址为本组 Escrow `0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5` 的日志，区别于上面的 YZHToken Transfer。
3. 画面应包含交易哈希（可保留地址栏）、Escrow 地址、该条日志的 topics/data。若已解码，拍到 `OrderResolved`、id=1、sellerWins=false；未解码则保留原始数据：topic0=`0x649ff37700d4eb8a5bc86ef06a55183597fa5c3c6b53ec4b9ee510cb2089ee26`，topic1 为 64 位十六进制末尾 `1`，data 为 32 字节全 0。
4. 建议文件名 `T-3-03_ZZL_OrderResolved浏览器日志.png`。截图人按实际拍摄者填写；交来原图或本机路径后，再复制入 `evidence/timeline/`，核对清晰度并登记 SHA256。**现已收到 ZZL 的原图，并同时收到退款 Transfer 图，分别归档 T-3-03、T-3-04。** 浏览器解码参数名与本组源码不同，且两图未包含交易哈希；具体解释和组合证据范围见 evidence/timeline/T-3_ZZL_浏览器原图核对说明.md。

ZZL 可以直接用公开交易页面补拍，无需 YZH 的后台、电脑或私钥。此次验收复用已有 T-1/T-2，并独立使用公开 RPC 核验即可完成；可选的 --api 复测依赖已启动并加载原合约的后端，本轮没有要求 ZZL取得原仲裁私钥来启动它。

截图最简交付就是这一张补图。若希望报告展示本次复测，也可自行在终端运行测试/核验命令后截图，作为可选辅助图。JSON、测试 XML 和截图各自保存，不能把机器结果冒充人工截图。

## 证据与报告入口

- [时间线证据索引](../evidence/timeline/README.md)：每张现有图、原始响应、校验范围和待补状态。
- [报告 4.5 片段](report-parts/timeline.md)：可在收齐图片后用于正式报告。
- [本机同步清单](local-sync-2026-10-29.md)：新目录、GitHub 基线及保留的旧副本独有文件。

本轮时间线材料现已通过上方 PR 上传 GitHub；截图、测试、原始核验数据与说明统一在该分支可查。以下运行记录保留各自发生时的状态。


## ZZL 本人链上核验已通过

ZZL 在本机 CMD 修正调用语法后，于 2026-10-29 23:20（Asia/Shanghai）实际运行 scripts/verify-timeline.py，RESULT=PASS，订单 0 Completed（3 事件）、订单 1 Refunded（4 事件）。原图 T-4-01 与本轮 verification/20261029T152047601888Z/ 原始结果已对应归档。本轮 chainHead=2728523；模式为存档 API 与实时回执核验，不声称本轮启动后端。CMD 问题已解决。下一步由 ZZL 运行 Java 测试并提供 BUILD SUCCESSFUL 截图。


## ZZL 时间线主要验收截图已收齐

ZZL 已提供 T-4-02 Java 构建成功原图，显示 BUILD SUCCESSFUL in 10s。独立读取其本次 23:26（Asia/Shanghai）生成的四份 JUnit XML：9 项中 8 通过、1 项原本地 EVM 场景跳过，0 失败、0 错误；本次 XML 单独存于 evidence/timeline/validation/ZZL-20261029T152619Z/，未覆盖先前验证。Solidity 编译与封装任务按原配置跳过，不将本图称为完整 Solidity/EVM 验证。

时间线已有 T-1/T-2 接口原图可复用，加上 ZZL 的 T-3-03、T-3-04、T-4-01、T-4-02，主要验收截图已齐。截图与原始数据均原样保存、登记哈希；CMD 语法问题已复测解决。下一步是后续将证据插入正式报告与核对组员信息，无需继续为本项重复交易或截图。时间线材料已上传 GitHub，见文首 PR；历史运行记录中的“未推送”为当时状态。
