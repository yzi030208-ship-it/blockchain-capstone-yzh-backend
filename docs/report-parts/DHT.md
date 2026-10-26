# DHT 大作业报告片段

本片段对应报告 4.4 的 DHT 部分及本人 4.6、AI 使用说明。状态：代码和本地验证已完成并接入主分支；新 Escrow 已由 YZH 部署，DHT 已在 2026-10-26 完成两单创建、60 枚授权与两单付款，创建付款 10 张原图已归档；配置、封装、Service 10 张及退款核对 3 张也已补齐。截图中买家余额 30 枚、订单 1 Refunded、订单 0 Paid；正常单确认等待 YZH 保存当前状态证据后明确放行，最终回执及 Completed 仍待补。实际进度以 [三人共用交接](../team-handoff.md) 为准。所有“待补”必须在正式提交前替换为真实证据，不能直接把本文作为已完成报告。

## 分工与实现

DHT 负责接入 Escrow 及代币源码、生成 Web3j Java 封装、实现 `EscrowGateway` 的核心 `EscrowService`，并以本人钱包担任测试网买家。Controller、事件列表及时间线由 YZH 负责，重入挑战和证据汇总由 ZZL 负责。

本次核心 Service 沿用组长定义的同步接口 deploy/load/current/getOrder/resolve，使用已有 DTO。部署与仲裁采用后端环境变量对应账户本机签名，读取 RPC chainId 生成带链标识的原始交易；部署回执成功后才保存唯一共享合约状态。加载检查代码和 arbiter，失败保留之前有效状态；单笔订单状态转文字，整数用十进制字符串。

构建与关键测试结果以 `../dht-verification.md` 为准；本地 EVM 验证和测试网交易分开记载。部署/仲裁由 YZH 的后端账户执行，DHT 不获取该账户私钥。

## 截图 4A-1 配置占位符

【已取得本人截图】[4A-1 配置环境变量](../../evidence/4A/4A-1-01_DHT_配置环境变量.png) 显示 RPC、`web3j.private-key=${WEB3J_PRIVATE_KEY:}` 和 gas 参数，无真实密钥值。当前配置为组长基线提供，本次沿用，不把它写成 DHT 独自新写的配置。

### 解释 4A-1 草稿 待结合本组截图定稿

示例的公开 Hardhat 私钥是全世界可获得的测试凭据。如果当成自己测试网账户的秘密使用，别人可以使用同一私钥签名，转走该账户的币，并行使该账户获得的合约权限。不能因为测试网资产无实际价值，就认为仲裁实验中的权限与结果不受影响。

本组配置使用截图 4A-1 所示的环境变量占位符，实际值仅由 YZH 在本机提供；提交仓库没有保存真实私钥。服务器持有仲裁账户私钥意味着平台负责凭据保护、操作授权、审计和 gas 支出；若放在某同学 MetaMask 中，则由本人在钱包中逐笔确认。本次路线 A 是服务器代签，调用后端接口的人不必等于链上 From，因此必须理解无认证接口可能滥用服务器权限。

【定稿待补】截图文件名、实际后端公开地址、实际配置行。不要展示真实私钥，不把环境变量等同于完整安全系统。

## 截图 4A-2 生成 Java 封装

【已取得本人截图】终端原图显示真实生成命令、Solidity 编译、四个封装生成与 BUILD SUCCESSFUL in 4s；生成类关键结构各有截图，见 [截图索引](../evidence-index.md)。事件定义第 72 行右侧裁切，建议补充完整参数图。生成文件不手改。

### 解释 4A-2 草稿 待结合生成代码截图定稿

Solidity 的 `Order memory` 在本次生成封装中映射为静态内部类 `Escrow.Order`，继承 Web3j 的 `DynamicStruct`。buyer/seller/token 是 Java String，amount/deadline/status 是 BigInteger，description 是 String；结构中有动态字符串，所以不能把整个结构误称为纯静态 ABI 元组。Service 再把 status 索引映射成 Created、Paid、Completed、Refunded、Disputed。

`BINARY` 是部署字节码，`FUNC_` 常量保存函数名。`ORDERCREATED_EVENT` 定义事件名与各参数的 ABI 类型及 indexed 标记，事件签名可编码为 topic0，用于筛选日志。id/buyer/seller 的 indexed 值在 topics 中，token/amount/deadline 在 data 中；解码方法把这些数据组合成 EventResponse，并保留原日志的交易和区块标识。

【定稿待补】实际生成类包名与截图文件；引用截图显示的 Order 字段及事件参数，并说明对应本组哪笔创建交易。未取得创建交易前，不写已经解码过本组测试网事件。

## 截图 4A-4 核心 Service

【已取得本人截图】`src/main/java/com/wetech/demo/web3j/service/EscrowService.java` 四张分图连续覆盖第 1—180 行；见 [截图索引](../evidence-index.md)。Controller 由 YZH 提供。方法签名和异常说明以现有代码与 [三人共用交接](../team-handoff.md) 为准。

实现要点：部署成功回执作为地址和区块来源；load 的账户核对和失败不覆盖状态；current 的唯一数据来源；链上枚举转换；resolve 保留真实失败信息。未增加发货状态、多签或路线 B。

## 截图 4A-5 买家操作材料

【买家创建及付款已完成】2026-10-26 在本组新 Escrow 完成两笔 createOrder、一笔 approve 共 60 枚、两笔 deposit。两单原始 getOrder 截图均为 status=1（Paid）；五笔实际交易及事件经只读 RPC 核对。10 张截图见 [证据索引](../evidence-index.md)，哈希、区块和金额见 [交易记录](../transactions.md)。争议单退款已在本人截图核对：余额 30 枚、订单 1 Refunded。正常单仍 Paid，等 YZH 明确通知可以确认后再执行，不把待执行结果写成已完成。

| 待填项 | 真实值 |
| --- | --- |
| 本次新 Escrow / 部署区块 | `0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5` / `2672683`；详见公开配置 |
| 链上 arbiter / resolve From | arbiter 为 `0x6717496Fe22477B41e6Ca07353C308fF38A61797`；实际 resolve From 待交易后核对 |
| 买家地址 / 卖家地址 | `0x5FeDaeB4369432aD2A9929EaFFA9762C46050d4C` / `0xa508c9Ff372C40F1f109EdA1F8DB532fB556c30A`；创建回执事件及钱包截图已核对 |
| 正常单 id / 创建、付款、确认哈希 | id=0；创建 `0x782a3aa446b05827ba109dbd3502edec6b60f2b93d72488011e4e6d55f6524b6`；付款 `0xe4e6c4089b9dde7fef35832cd2020f649ac5cad5842ccdc2df080f28d8247b46`；confirm 待执行 |
| 争议单 id / 创建、付款、退款哈希 | id=1；创建 `0xd2246b70a7f676b33c70fcf31f2f25baf5c1d0fd61e1ef9f111f921e2555a333`；付款 `0xdcfa559d8fed107287dbab3d29e6efd1b09872c0932f6d6d7371b35423287db7`；退款仲裁 `0x86b5d80b974abd153f5b40a4e08f72f0d5657bf1fd048289eb621878fbbf9a92`，由 YZH 交接核验，DHT 另核对到账截图 |
| approve spender / 金额 / 哈希 | 新 Escrow `0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5` / `60000000000000000000` / `0x279498c462501f23a680cacaf7b82a68771a47456a3c8cce1a43e5032d3f1014` |
| 仲裁前后买家与托管的 ERC20 余额 | 本人退款后 balanceOf 截图为 30 枚；退款前与托管余额引用相应真实回执或后续读取，不预填 |

### 解释 4A-5 草稿 待结合本组交易定稿

本组路线 A 的 resolve 交易由后端专用账户签名，链上 From 应是后端账户，而不是发 HTTP 请求的同学或买卖家。`sellerWins=false` 时，Escrow 把当前争议订单的 token 从 Escrow 合约地址转给该订单 buyer；数量是 order.amount。退款代币来自托管余额，gas 则由发送 resolve 交易的后端账户以原生 POT 支付。

调用链为 `EscrowController.resolve` → `EscrowGateway` 的 `EscrowService.resolve` → 生成的 `Escrow.resolve(...).send()` → Web3j Contract 交易执行 → `RawTransactionManager` 签名并发送。Service 的 `transactionManager()` 从 Credentials 构造管理器，Credentials 来自 `Web3jConfig` 对环境变量配置的读取；业务方法发起调用不等于私钥在浏览器中签名。

【定稿待补】引用 4A-8 的实际 resolve 哈希、From、Escrow 地址、买家地址、Transfer 数量，以及 4A-4 中方法所在位置。地址和数量必须来自本组真实截图，不能把本地测试或旧实验 3 的哈希填入。

## 4.6 本人遇到的问题与解决

1. **编译器下载与版本漂移。** 组长交接记载 Solidity 0.8.35 下载曾卡住。本次检查本机已有原生 solc 0.8.20，固定编译版本及 Paris EVM 目标，通过 Gradle 可选路径使用缓存编译器，实际完成生成。不是通过手写 Java 封装绕过任务。
2. **测试依赖下载 TLS 错误。** 初次基线测试在 Maven 下载 spring-test/byte-buddy 时出现 bad_record_mac。重试后仍有 byte-buddy 下载失败，于是从官方 Maven 地址下载 JAR 并核对官方校验和，再补入本机 Gradle 缓存；业务代码未为此更换依赖版本。
3. **AI 测试代码 API 误用。** 初稿把 Web3j HttpService 放进 try-with-resources，但该版本不实现 AutoCloseable，编译明确失败。改成 finally 调用 close 后重新编译验证；最终结果见验证记录。此问题发生在新增测试代码，不能描述为同学原合约的错误。

其他问题若实际出现，由 DHT 补充现象、原因、修改、复测结果，不编造。

## AI 使用说明

本次使用 Codex 阅读课程材料、检查组长项目、整理接口边界、接入源码、编写 EscrowService 与验证测试、辅助解决构建错误，并生成本报告片段及操作清单。合约主体沿用已有实验 3 源码；Java 封装由 Gradle Web3j 工具生成，未由 AI 手写替代。

AI 曾误用 HttpService 的资源管理接口，实际编译指出错误，已据此修正并重新验证。真实钱包确认、截图、课程理解和最终报告核对由本人完成。尚未执行的测试网流程与截图保持待补，不写成 AI 已经完成。
