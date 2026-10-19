# 给 DHT 的 Escrow Service 接口交接

YZH 已建立 Java 21 / Spring Boot 3.2.3 / Web3j 4.14.0 基线。包名为 `com.wetech.demo.web3j`。DHT 请在**同一项目**补入实验 3 最终 `Escrow.sol`、所需代币接口/ABI，生成 Java 封装，并创建 `@Service` 类实现 `com.wetech.demo.web3j.escrow.EscrowGateway`。不要再建第二套 Controller 或事件扫描 Service。

必需方法签名：

```java
DeploymentResult deploy();
EscrowContext load(String address, BigInteger fromBlock);
EscrowContext current();
OrderDetail getOrder(BigInteger id);
ResolveResult resolve(BigInteger id, boolean sellerWins);
```

DTO 已在同包定义。`deploy` 必须取得真实交易回执，`deployBlock` 使用十进制字符串；`load` 必须保留用户提供的**原部署区块**，并核对链上代码和 `arbiter`。`current()` 返回唯一共享的合约地址、链上仲裁人和部署区块；YZH 的事件查询从这里取数据。服务重启后可用同一地址和区块重新 `load`，不要自动部署新合约。

`getOrder` 要将 enum 映射为 `Created`、`Paid`、`Completed`、`Refunded`、`Disputed`，链上整数用十进制字符串。`resolve` 由本机 `Credentials` 对应的后端账户签名；成功后等待实际回执再取订单状态。`receiptStatus` 记录 `0x1` 等回执状态。非争议订单失败需保留真实错误/回执，不能返回假成功哈希。

`MissingEscrowGateway` 只在核心 Service 缺失时给出 503；你的实现被 Spring 扫描后应替代它。若出现两个 Gateway bean，请优先检查扫描和条件注解，不要删掉接口边界。

当前已核对 YZH 代币地址 `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee`，精度 18，owner 是 YZH，DHT 买家钱包余额为 0。创建订单前请和 YZH 完成至少 60 个代币的本人签名转账或铸币。**本次必须由后端专用账户部署新的 Escrow**，不使用实验 3 的旧地址。

构建现状：Java 编译和 YZH 的事件查询测试通过；完整 Gradle 任务链停在下载 Solidity 0.8.35。你接入合约源码后需核实 Web3j 插件的 Solidity 版本及下载方式，再跑完整 `generateContractWrappers` 和 `test`。不要修改生成的 Java 文件代替生成步骤。
