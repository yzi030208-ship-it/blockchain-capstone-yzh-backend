# DHT 核心 Service 实施交接

## 当前交付范围

本地分支 `feat/dht-service`，基于 YZH 仓库基线提交 `66856826e51160ae0ed1d67983525d1b8949b232`。本次仅做 DHT 的合约、封装、核心 Service、必要构建调整、测试和本人报告材料。未推送、未创建 PR、未提交最终报告。测试网部署、买卖家操作与真实截图仍需三人配合。

## 源码和构建

- `src/main/resources/contracts/Escrow.sol`：原样复制 DHT 实验 3 已有源码，不改变状态机和事件。原文件中的 TODO 是历史章节注释，函数体已实现，并非当前待实现桩。
- `src/main/resources/contracts/YZHToken.sol`：复制本机组长实验 3 项目 `blockchain-lab3-yzh/packages/hardhat/contracts/YZHToken.sol`。该源码用于生成真实代币 ABI/封装及本地测试，不重新部署测试网代币。测试网源码一致性仍须组长按其部署产物核对。
- `src/main/java/com/wetech/demo/web3j/contracts/`：由 Gradle Web3j 4.14.0 工具生成 Escrow、IERC20、YZHToken；SimpleStorage 也由同一生成任务更新，未手改生成代码。
- `build.gradle`：固定 Solidity 0.8.20、EVM Paris，防止宽范围 pragma 自动选择更新的编译器；支持 `-PsolcExecutable=/absolute/path/to/solc`，不把本机绝对路径写死进共享构建。Paris 是保守的字节码目标，不代表已证明 POTOS 部署成功。
- 保留 YZH 基线的 `-PcompileContracts=true` 开关；最终生成验证必须显式打开，不能把跳过生成的默认测试当作生成证据。
- 未修改 YZH 的 Controller、事件扫描、DTO 和统一异常处理。未修改已有私钥环境变量读取规则。

在项目根目录、WSL/Ubuntu 中执行：

```bash
java -version
bash gradlew generateContractWrappers -PcompileContracts=true
bash gradlew test -PcompileContracts=true
```

若已有原生 solc 0.8.20，可给两条命令附加 `-PsolcExecutable=/absolute/path/to/solc`，用 `solc --version` 确认版本。不要把 soljson.js 当原生可执行文件。此次使用现有 Hardhat 缓存中的原生 0.8.20，避免重新下载 0.8.35；具体环境与实测结果见 `dht-verification.md`。

## Service 接口保持 YZH 已定义的签名

实现类 `com.wetech.demo.web3j.service.EscrowService implements EscrowGateway`，标注 `@Service`。

```java
DeploymentResult deploy();
EscrowContext load(String address, BigInteger fromBlock);
EscrowContext current();
OrderDetail getOrder(BigInteger id);
ResolveResult resolve(BigInteger id, boolean sellerWins);
String getContractAddress();
BigInteger getDeployBlock();
```

- `deploy`：从当前 RPC 读取 chainId，使用 Credentials 创建带 chainId 的 RawTransactionManager，本机签名；constructor arbiter 使用后端账户。必须取得成功回执、交易哈希和区块后才发布共享状态，不使用零区块兜底。
- `load`：检查地址、uint256 区块、链头、当前链上代码及给定区块时的代码；读取 arbiter 和 orderCount 核对预期接口。arbiter 与当前后端账户不符时拒绝加载，并保留原先有效状态。传入的区块必须由真实部署回执提供；服务不自动发现部署区块，也不保证能识别所有“过晚但已有代码”的错误区块。
- `current`：从唯一状态取得地址和部署区块，重新读取链上 arbiter；事件服务继续只用 `EscrowGateway.current()`，不另维护地址。
- `getOrder`：实际调用链上 getOrder，将 uint256 转成十进制字符串，将五个枚举值映射为文字。
- `resolve`：由后端账户签名，等待真实回执后读取业务状态；不在本地伪造状态检查失败，不伪造失败交易。返回原 DTO 的 `receiptStatus` 与 `orderStatus`。
- 对未部署/加载返回 `IllegalStateException`，参数或账户不匹配返回 `IllegalArgumentException`；链上/RPC 失败包装成现有 `EscrowRpcException`，交给 YZH 已有处理器。若异常携带失败回执，在错误文本保留交易哈希、区块、回执状态和 gasUsed。
- 网关内同步处理部署、加载、仲裁，防止地址与区块混用及网关自身并发发交易。SimpleStorage 与网关共用账户时仍应串行验收；这不是跨进程 nonce 管理或生产级并发队列。
- 重启后调用 load 恢复，同步状态仅存内存；无自动部署或磁盘存储私钥。

## 本地集成验证怎样重跑

测试只接受显式指定的 localhost HTTP RPC，进一步校验 chainId=31337。每次生成新的临时测试密钥，仅驻留内存，通过 Hardhat 的本地方法提供测试余额；不读取组长私钥。

```bash
HARDHAT_PROJECT=/path/to/existing/hardhat-project bash scripts/dht/run-local-tests.sh \
  -PsolcExecutable=/absolute/path/to/solc
```

该已有 Hardhat 项目只提供工具依赖，不读取其原始网络配置；使用本仓库独立配置，不使用 `.env`。脚本监听 127.0.0.1:18545，端口已占用时拒绝运行；结束后关闭自己启动的节点并删除可能含公开开发密钥的节点日志。构建日志在忽略的 `build/`，测试 XML/HTML 也在 `build/`。

如果不运行脚本，普通 `test` 会跳过本地 EVM 集成测试；必须如实区分单元测试和集成测试。示例测试中的本地 YZHToken 部署不能当作“测试网重新部署代币”。

## YZH 接收后要做的事情

1. 合并本分支文件，核对组长代币部署源码；重新生成封装并运行测试，不手动改生成类。
2. 用自己的后端专用账户 `0x6717496Fe22477B41e6Ca07353C308fF38A61797` 在本机注入环境变量，核对 chainId=60600 和当前 gas 余额；私钥不交接给 DHT。
3. 核对/补足 DHT 买家至少 60 个 YZH 代币及 gas；交接文档的 2026-10-19 余额仅是历史记录，不代表现在余额。
4. 按原 Controller 的 deploy 接口新部署；把地址、部署区块、部署哈希、链上 arbiter 及公开网络配置发给 DHT/ZZL。不要发私钥。
5. 后端持有账户在自己的机器运行，DHT 不需要在自己的机器使用组长私钥启动服务。DHT 在自己的浏览器操作同一测试网合约即可。
6. 联合验收两单、失败仲裁、退款、确认收货、时间线、重启 load，并将真实回执和截图交给 ZZL。

## 仍需本组真人完成

所有测试网发送与截图均为待完成。DHT 的买家操作步骤和截图清单见 `dht-buyer-checklist.md`，报告片段见 `report-parts/DHT.md`。本地构建及自动测试输出只能证明本地实现，不代替 MetaMask、Remix 和测试网浏览器证据。
