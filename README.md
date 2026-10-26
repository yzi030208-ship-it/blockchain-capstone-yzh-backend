# 区块链大作业 路线 A 后端

本组使用 Spring Boot 3.2.3、Web3j 4.14.0、Java 21，在 POTOS Testnet 上演示两笔 ERC20 托管订单。该项目以课程 [spring-boot-web3j 示例](https://github.com/kyonRay/spring-boot-web3j) 为起点。YZH 已建立 HTTP/事件查询基线，DHT 交接包已接入 Escrow 合约封装与核心 Service；本地测试和测试网验收状态须分别查看 [实验执行记录](docs/experiment-runbook.md) 与 [DHT 验证记录](docs/dht-verification.md)，不能把本地测试写成测试网交易完成。

## 开发分工

- DHT：`Escrow.sol`、代币接口或 ABI、Java 封装、`EscrowService`，实现 [`EscrowGateway`](src/main/java/com/wetech/demo/web3j/escrow/EscrowGateway.java)。
- YZH：Controller、订单创建事件列表、订单时间线、错误处理、公开配置、集成和报告。
- ZZL：独立重入挑战、主业务验收与交易证据索引。

三人共用的当前阶段、职责、下一位操作者和每阶段更新格式统一见 [`docs/team-handoff.md`](docs/team-handoff.md)。链上公开参数见 [`docs/project-config.md`](docs/project-config.md)。旧阶段的交接不覆盖当前状态。

开始真实测试网操作时请打开 [`docs/experiment-runbook.md`](docs/experiment-runbook.md)，逐步记录公开数据、交易回执及报告截图编号。
三人截图的编号、具体画面、责任人、建议文件名及 GitHub 归档目录见 [`docs/evidence-capture-guide.md`](docs/evidence-capture-guide.md)。

## 本地准备

在 WSL/Ubuntu 中使用 JDK 21、Node.js 20 和 Gradle Wrapper。主业务必须使用 **本组新部署的 Escrow**，仲裁人是由后端控制的独立测试账户；实验 3 的旧 Escrow 和旧仲裁钱包不能代替它。私钥仅保存在本机环境变量，不填入聊天、源码、`.env`、截图或报告。后端默认仅监听 `127.0.0.1:8080`；此设置不等于完整认证。

YZH 在仓库根目录可运行 `bash scripts/yzh/start-backend.sh`。脚本会隐藏输入、检查私钥格式并启动后端；输入内容不要粘贴到命令行或聊天。服务启动后在另一个 WSL 终端调用 `POST /api/escrow/load?address=0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5&fromBlock=2672683` 恢复本组 Escrow，不再次部署。

```bash
cd /path/to/blockchain-capstone-backend
java -version
node -v
read -rsp 'Backend test-wallet private key: ' WEB3J_PRIVATE_KEY
echo
export WEB3J_PRIVATE_KEY
bash gradlew bootRun
```

本机按网络情况可覆盖 `WEB3J_RPC_URL`、`WEB3J_GAS_PRICE`、`WEB3J_GAS_LIMIT`。默认 RPC 为 `https://rpc-testnet.potos.hk`，默认 gas price 为 `1000000` wei；链上发送前核对钱包 gas 和网络返回。使用完毕可运行 `unset WEB3J_PRIVATE_KEY`，关闭终端前不要在命令历史中直接输入私钥。禁止把后端账户私钥交给 DHT、ZZL 或 Codex 聊天。

## 构建与当前验证状态

已提交的 Java 源码和生成类可直接运行：

```bash
bash gradlew test
bash gradlew bootRun
```

Gradle 8.13 的 Wrapper 已运行。DHT 已接入 Escrow 与 YZHToken 源码，固定 Solidity 0.8.20、EVM Paris；2026-10-22 在 YZH 的 WSL 重新执行完整编译、封装生成及本地 EVM 测试，5 项测试通过。默认 `test` 仍跳过 Solidity 编译和本地 EVM 场景。完整生成使用：

```bash
bash gradlew generateContractWrappers -PcompileContracts=true
bash gradlew test -PcompileContracts=true
```

若本机已有原生 solc 0.8.20，给命令附加 `-PsolcExecutable=/absolute/path/to/solc`；不要提交个人绝对路径。本地 EVM 复测方法与真实执行结果见 [DHT 验证记录](docs/dht-verification.md) 和 `scripts/dht/run-local-tests.sh`。默认 `test` **不能替代最终 Solidity 编译与封装生成**，本地测试也不能替代 POTOS Testnet 部署截图。

## 路线 A 接口

所有链上整数在 JSON 中用十进制字符串表示。交易回执的 `receiptStatus` 与订单业务 `status` 分开。

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/escrow/deploy` | 新部署 Escrow，返回地址、部署哈希、部署区块 |
| POST | `/api/escrow/load?address=...&fromBlock=...` | 加载已部署合约和原部署区块 |
| GET | `/api/escrow/address` | 当前地址、链上仲裁人、部署区块 |
| GET | `/api/escrow/orders/{id}` | 单笔订单详情和文字状态 |
| GET | `/api/escrow/orders` | 从 OrderCreated 日志重建列表 |
| POST | `/api/escrow/orders/{id}/resolve?sellerWins=false` | 由后端账户签名仲裁 |
| GET | `/api/escrow/orders/{id}/events` | 六类 Escrow 事件组成的订单时间线 |

请求示例（WSL）：

```bash
curl -sS -X GET http://127.0.0.1:8080/api/escrow/address
curl -sS -X GET http://127.0.0.1:8080/api/escrow/orders
curl -sS -X GET http://127.0.0.1:8080/api/escrow/orders/1/events
```

以上调用在**运行后端的同一个 WSL 环境**执行。当前服务只绑定 WSL 的 `127.0.0.1`，Windows 浏览器或其他组员电脑未必能直接连接；需要由 YZH 在本机取得真实响应并交给 ZZL 汇总截图。若后续改变监听地址，先为无认证的仲裁接口加访问限制。PowerShell 如在其能访问的环境运行，请使用 `curl.exe`。在 Service 尚未接入、未部署或未加载时，接口不会返回伪造订单；错误响应应按实际状态核对。

## SimpleStorage 示例

课程示例保留在 `/api/storage`。它的地址和交易**独立于**本次 Escrow。2026-10-22 已按 `POST /api/storage/deploy`、`POST /api/storage/value/set?value=42`、`GET /api/storage/value/get` 顺序在测试网完成并留存 4A-3 截图；实际地址、哈希和区块见 [实验执行记录](docs/experiment-runbook.md)。

## 验收和报告

真实两单流程、交易索引及截图编号以[共用阶段交接](docs/team-handoff.md)、[实验执行记录](docs/experiment-runbook.md)和[截图指南](docs/evidence-capture-guide.md)为准。YZH 的报告片段见 [`docs/report-parts/YZH.md`](docs/report-parts/YZH.md)。不要把旧实验 3 交易哈希填进本次大作业。
