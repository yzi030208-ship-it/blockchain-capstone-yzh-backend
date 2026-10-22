# DHT 实际验证记录

验证日期：2026-10-20（Asia/Shanghai）。**这是一份当日 DHT 本地验证历史记录**，下文“尚未完成”均指该次验证当时；2026-10-22 之后的测试网进度请看 [三人共用交接](team-handoff.md) 和 [实验执行记录](experiment-runbook.md)。本地测试不代表 POTOS 测试网交易已发生。

## 环境与生成

- 系统：现有 Ubuntu WSL；OpenJDK 21.0.12.1；Node.js v22.22.1；Gradle Wrapper 8.13。
- 沿用 Spring Boot 3.2.3、Web3j 4.14.0。Solidity 固定 0.8.20，目标 EVM Paris。
- 本机已有原生编译器：`/home/spike/.cache/hardhat-nodejs/compilers-v2/linux-amd64/solc-linux-amd64-v0.8.20+commit.a1b79de6`。这是本机可选路径，不是团队成员必须有的路径。
- Escrow 来源为 DHT 实验 3 源码，复制前后逐字节一致，SHA-256 为 `12ee8299ee98afb1a8e93130fe7051498db9adeba5d97c1e5aa93baa2732b2b9`。
- YZHToken 来源为本机 YZH 实验 3 项目中的源码；生成接口并用于本地测试，未重新部署测试网代币。

实际执行并成功：

```bash
bash gradlew generateContractWrappers -PcompileContracts=true \
  -PsolcExecutable=/home/spike/.cache/hardhat-nodejs/compilers-v2/linux-amd64/solc-linux-amd64-v0.8.20+commit.a1b79de6 --console=plain
```

输出三份 Solidity 源文件编译成功，生成 SimpleStorage、IERC20、YZHToken、Escrow 四个 Java 封装。生成器关于“非 view 函数带返回值”的提示不是编译失败：写交易返回回执，不能通过普通交易直接取得 Solidity 返回值。本次未手改生成类。

## 可复现脚本实际运行结果

实际使用已有 Hardhat 2.22.19 工具依赖，独立配置启动本机 127.0.0.1:18545，核对 chainId=31337。执行：

```bash
HARDHAT_PROJECT=/home/spike/blockchain-course/my-first-dapp/packages/hardhat \
  bash scripts/dht/run-local-tests.sh \
  -PsolcExecutable=/home/spike/.cache/hardhat-nodejs/compilers-v2/linux-amd64/solc-linux-amd64-v0.8.20+commit.a1b79de6
```

脚本显式使用 `-PcompileContracts=true --rerun-tasks`；本轮九项任务全部实际执行，最后输出 `BUILD SUCCESSFUL in 1m 36s`。脚本已结束，自己启动的本地节点已关闭，节点日志已自动删除，没有保留 Hardhat 打印的公开开发密钥。本地测试账户通过随机生成私钥及本地供资方法创建，仅在内存中使用，未读取三人的钱包私钥。

根据本轮 `build/test-results/test/TEST-*.xml` 核对：

| 测试类 | tests | failures | errors | skipped |
| --- | --- | --- | --- | --- |
| YZH EscrowEventQueryServiceTest | 2 | 0 | 0 | 0 |
| DHT EscrowServiceTest | 2 | 0 | 0 | 0 |
| DHT EscrowServiceLocalIntegrationTest | 1 | 0 | 0 | 0 |
| 合计 | 5 | 0 | 0 | 0 |

其中本地 EVM 测试是一个完整场景，实际执行并断言：

1. 部署新 Escrow，取得真实本地链回执，arbiter 为临时后端账户。
2. 新 Service 实例按原地址和区块 load，共享上下文与原实例一致。
3. 本地代币部署和供资、创建三笔订单、approve/deposit，验证 Created 和 Paid。
4. 不存在订单查询失败；Paid 订单仲裁失败，订单状态未被错误改变。
5. 争议后 resolve(false) 返回成功回执和 Refunded，买家余额增加对应金额。
6. 正常 confirm 后 Completed；另一争议订单 resolve(true) 后 Completed，卖家余额符合两笔收款总额。
7. 使用 YZH 原有事件服务查询同一合约，取得三笔创建记录、退款争议单四条真实事件；没有虚构额外 OrderRefunded 日志。
8. 无代码地址 load 失败且原状态不被覆盖；错误后端账户 load 因 arbiter 不匹配失败。

Spring 上下文测试从真实 Application 配置启动，核对只有一个 EscrowGateway，类型为 EscrowService，未选择 MissingEscrowGateway。另有未加载状态和非法参数检查。

## 打包

实际运行 `bash gradlew bootJar --console=plain`，输出 `BUILD SUCCESSFUL in 28s`，生成 `build/libs/*.jar`。此次打包复用刚完成的生成类，因此默认跳过 Solidity；它是打包检查，不能代替前述已显式开启的编译与生成验证。

## 实际遇到并解决的问题

- 基线测试依赖下载出现 TLS `bad_record_mac / Tag mismatch`。从 Maven Central 官方地址补下载失败的 byte-buddy 1.14.12 JAR，校验官方 SHA-1 后填入本机 Gradle 缓存，再次构建成功。未改变共享依赖版本。
- 新增测试初稿误把 HttpService 当作 AutoCloseable，编译失败。改用 finally 中显式 close，后续编译及测试通过，已如实记入 AI 使用说明。
- Gradle 有未来 Gradle 9 兼容性提示，当前 Wrapper 8.13 构建成功；没有为消除提示而擅自升级构建工具。

## 尚未完成的测试网验证

- 未注入或取得 YZH 后端私钥，未发送 POTOS 部署/仲裁交易。
- 未生成本次测试网 Escrow 地址、部署区块或交易哈希。
- 未在此次工作中重新核对公开配置里的历史余额；不能把 2026-10-19 的余额描述为当前余额。
- DHT 的 MetaMask 买家操作、YZH 卖家争议、后端真实仲裁与失败回执、测试网时间线和浏览器截图仍需联合验收。
- 该次验证时代码、生成日志已就绪，但指导书要求的本人截图尚未取得；当前截图编号和责任人见 [截图指南](evidence-capture-guide.md)。

本地测试报告位于 `build/reports/tests/test/index.html`，原始 XML 位于 `build/test-results/test/`；这些属于本机构建产物，不纳入源码交接包。不要从本地测试链复制交易哈希到正式报告的测试网交易记录表。
