# DHT 配置封装与 Service 代码截图指南

本指南补齐报告 4A-1、4A-2 和 4A-4 的 DHT 部分。截图必须从本人真实编辑器或终端取得；本文件和构建日志不是截图，不标为已截图。所有操作均无需钱包私钥，也不调用 deploy、resolve 或其他测试网写交易。

## 统一准备

1. 用 VS Code、IDEA 或 Codex 编辑器打开当前后端项目，确认路径是本组 `blockchain-capstone-yzh-backend`。
2. 编辑器显示文件名、行号，关闭或缩小无关面板，字号保持能读清。长代码分图，不缩成密集小字。
3. Windows 用 `Win + Shift + S` 截取实际窗口，保存 PNG 到项目外的 `微众银行实验/大作业/screenshots/`。不要只复制到剪贴板，不截密钥或输入密钥的终端。
4. 截好后交给当前 Codex 核对，再复制一份到仓库 `evidence/4A/`，更新截图索引后提交推送。本地原图始终保留。未截图前不要把索引标成完成。

## 4A-1 配置截图

文件：`src/main/resources/application.properties`，当前共 11 行。

打开后截全文件。必须清楚显示 RPC、`web3j.private-key=${WEB3J_PRIVATE_KEY:}`、gas price、gas limit，截图中不能出现实际私钥值。无需启动服务或打开环境变量设置。

建议文件名：`4A-1-01_DHT_配置环境变量.png`。

## 4A-2 实际封装生成与结构截图

### 先跑真实生成

在 Ubuntu/WSL 终端输入：

```bash
cd '/mnt/d/桌面文件/HOMEWORK/大三上/区块链/微众银行实验/大作业/blockchain-capstone-yzh-backend'
bash scripts/dht/prepare-wrapper-screenshot.sh
```

脚本显式打开 Solidity 编译，并使用 `--rerun-tasks`，不会用 UP-TO-DATE 冒充重新生成。本机如存在原生 solc 0.8.20 缓存则自动使用；其他电脑可用 `SOLC_EXECUTABLE=/absolute/path/to/solc bash scripts/dht/prepare-wrapper-screenshot.sh` 指定。若未找到本地编译器，由固定版本的 Gradle 插件按原逻辑处理下载。发生错误时先保留错误，不截图或写成成功。

成功输出含 Solidity Compiler run successful、Generating Escrow/IERC20/YZHToken、BUILD SUCCESSFUL 和文件列表。终端滚动前后可分两图：

- `4A-2-01_DHT_封装生成成功.png`：真实生成命令、编译/生成输出和 BUILD SUCCESSFUL。
- `4A-2-01b_DHT_生成文件列表.png`：文件列表；前一图空间不足时补充。

生成器可能提示非 view 函数有返回值，这是写交易封装提示，不是编译失败。构建日志保存在忽略的 `build/dht-wrapper-screenshot.log`，便于核对；不能把日志文本人工拼成终端截图。

### 再截生成类的关键结构

文件：`src/main/java/com/wetech/demo/web3j/contracts/escrow/Escrow.java`。以下行号来自当前生成类；重新生成后可用编辑器搜索同名符号定位。

| 建议文件名 | 定位 | 截图内容 |
| --- | --- | --- |
| `4A-2-02_DHT_部署字节码与函数常量.png` | 约 45 至 78 行，搜索 `BINARY`、`FUNC_CREATEORDER`、`ORDERCREATED_EVENT` | BINARY 声明、FUNC_ 常量及事件类型定义 |
| `4A-2-03_DHT_事件解码方法.png` | 约 163 至 175 行，搜索 `getOrderCreatedEventFromLog` | 方法如何从 indexed/non-indexed 参数组装事件响应 |
| `4A-2-04_DHT_Order结构.png` | 约 454 至 500 行，搜索 `class Order extends DynamicStruct` | buyer/seller/token/amount/deadline/status/description 及构造函数；可分两图 |
| `4A-2-05_DHT_创建事件响应.png` | 约 504 至 517 行，搜索 `class OrderCreatedEventResponse` | 创建事件响应中的 id、买卖家、token、金额与截止时间 |

BINARY 是很长的一行，可以关闭自动换行，只显示声明和开头，不需要展开几屏字节码；不要删改或手工缩写生成代码来凑图。其余结构应完整可读。报告仍统一归入【截图 4A-2】。

## 4A-4 核心 Service 截图

文件：`src/main/java/com/wetech/demo/web3j/service/EscrowService.java`，当前约 180 行。用户需求为完整代码截图，不能只截类名或部署方法。建议按以下范围分图，相邻图可稍有重叠以便阅读：

| 建议文件名 | 建议行号 | 主要内容 |
| --- | --- | --- |
| `4A-4-01_DHT_EscrowService_1.png` | 1 至 34 | imports、Service 注解、状态和依赖、共享状态、构造函数 |
| `4A-4-01_DHT_EscrowService_2.png` | 35 至 70 | deploy 及 load 的前半部分 |
| `4A-4-01_DHT_EscrowService_3.png` | 71 至 103 | load 后半部分、current、只读访问器、getOrder、resolve 起始 |
| `4A-4-01_DHT_EscrowService_4.png` | 104 至 138 | resolve、chainId 交易管理器、仲裁账户核对及共享状态 |
| `4A-4-01_DHT_EscrowService_5.png` | 139 至 169 | Order 字段转换、uint256 参数验证、成功回执检查及异常包装起始 |
| `4A-4-01_DHT_EscrowService_6.png` | 170 至文件末尾 | 失败回执中的哈希/区块/status/gasUsed 保留，以及类结束 |

如果你的屏幕能容纳更多清晰代码，可减少分图；若更小可增加分图。以完整且可读为准，不要求机械复刻行号。Controller 和 EventQuery 属于 YZH，不需要你重复截取。

## 取得截图后交接

把这些 PNG 保存后发给当前 Codex，说明“这些是真实截图，帮我核对并按截图指南归档、更新索引和推送”。Codex 再核对文件、复制原图、更新报告占位及 PR。未取得 4A-S1/S2、退款及确认结果时保持待完成，本次补代码截图不改变链上流程。
