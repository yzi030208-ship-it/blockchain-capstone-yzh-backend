# DHT（成员 1）任务与验收

先阅读 [项目目标与协作](team-workflow.md)、[核心接口交接](handoff-dht.md)、[公开配置](project-config.md) 和仓库根目录 README。你负责**核心 Escrow Service 与买家钱包**；YZH 已负责 Controller、事件列表/时间线、统一错误处理。主业务只用本组新部署的 Escrow 和 YZH 代币。

## 代码交付

- [ ] 在本人分支接入本次使用的 `Escrow.sol`、所需 IERC20/ABI；先对照实验 3 已验证的源码和事件签名，不扩展发货、多签或路线 B。不要重新部署 ERC20。
- [ ] 用项目构建流程编译 Solidity 并生成 Web3j 封装，核对生成的二进制、Order 结构、函数和六类事件。生成文件不手改；记录真实编译器版本和命令。基线为 Java 21、Gradle Wrapper、Spring Boot 3.2.3、Web3j 4.14.0，包名 `com.wetech.demo.web3j`。
- [ ] 实现 `@Service` 类 `EscrowService implements EscrowGateway`，完成 `deploy`、`load`、`current`、`getOrder`、`resolve`。返回对象沿用现有 DTO。`deploy` 构造参数中的 arbiter 必须来自 YZH 本机注入的后端 Credentials，而非 DHT 钱包或旧实验 3 地址。
- [ ] `deploy` 返回真实部署交易哈希和区块；`load` 检查地址有代码、核对链上 arbiter、保留原部署区块。重启后同地址同区块可重新加载；不自动部署新合约。`current` 是 YZH 事件查询使用的**唯一共享状态来源**。
- [ ] `getOrder` 返回真实字段及文字状态；`resolve` 等待实际回执并区分 `receiptStatus` 与业务状态。非争议订单失败保留实际错误/回执，不编造哈希。
- [ ] 如需改 `Web3jConfig`、`application.properties` 或构建脚本，与 YZH 在 PR 中说明原因；不覆盖已修好的环境变量私钥读取，不写入任何真实密钥。不要修改 `EscrowController` 或复制事件扫描 Service。

## 可以自己先验证什么

1. 克隆后运行 `bash gradlew test`，记录基线结果；完成合约接入后运行 `bash gradlew generateContractWrappers -PcompileContracts=true` 和 `bash gradlew test -PcompileContracts=true`。默认测试跳过 Solidity 编译，不能作为最终生成成功证据。
2. 用可控的本地/测试环境验证封装、状态映射、非法订单及错误处理。真实测试网部署由 YZH 的后端账户完成；DHT 不需要获取后端私钥。
3. PR 中给 YZH：源码/生成命令、Service 实现、测试结果、仍需核对的问题。提交 `docs/report-parts/DHT.md`，解释 4A-1（明文私钥风险）、4A-2（封装结构和事件解码）、4A-5（后端签名调用链及资金流），逐项注明真实证据和待补证据。

## 主业务中 DHT 本人操作

待 YZH 公布**新 Escrow 地址和部署区块**后，用本人 MetaMask 买家地址 `0x5FeDaeB4369432aD2A9929EaFFA9762C46050d4C`（先在钱包再次核对）及 POTOS Testnet 操作。开始前核对 YZH 代币余额至少 60 个、测试网 POT 足够 gas；余额不足由持币账户本人签名转入或铸入，DHT 不索取他人私钥。

1. 在 Remix 附加 YZH 新部署的 Escrow，创建两笔各 30 个代币订单，seller 填 YZH 公开地址 `0xa508c9Ff372C40F1f109EdA1F8DB532fB556c30A`，token 填 `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee`，deadline 为未来的 **Unix 秒**。18 位精度下每单原始整数为 `30000000000000000000`。记录真实订单 id 和创建交易。
2. 对新 Escrow 地址 `approve` 共 60 个代币（原始整数 `60000000000000000000`），确认 `allowance`，再依次 `deposit` 两单。不要授权旧 Escrow。
3. 等 YZH 完成订单 1 争议和退款后，对正常订单执行 `confirm`；核对订单 Completed、YZH 代币余额变化，记录买家退款余额。

本人保存 4A-1（安全配置）、4A-2（封装生成）、4A-4 的 Service 代码、4A-5 买家创建/approve/deposit/confirm 画面。每张至少可辨账户/合约、操作、结果；发给 ZZL 登记交易索引。代币转账、批准、订单创建、付款和确认的真实哈希逐笔交给 ZZL。尚未发生的链上结果一律标待实测。

## 完成定义

源码和 PR 可复现构建；`EscrowGateway` 被 Spring 正确注入；与 YZH 联调时能部署、读取、加载、仲裁，事件服务读取同一合约与区块；报告片段、截图和交易索引已交接。真实链上验证若因账户/gas/其他成员动作未完成，列出阻塞和实际错误，不写“全部完成”。
