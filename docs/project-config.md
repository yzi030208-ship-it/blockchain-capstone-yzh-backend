# 本组公开配置

本表只保存可公开的链上地址和运行参数。2026-10-19 已用课程 RPC 直接读取下列代币信息；余额会随交易变化，正式验收前须重新读取。

| 字段 | 当前值 | 核对方式或状态 |
|---|---|---|
| GROUP_NUMBER / 正式姓名及学号 | 待三人填写 | 不由 AI 猜测 |
| 路线 | A 平台后端 | Java + Spring Boot + Web3j |
| RPC_URL | `https://rpc-testnet.potos.hk` | 已成功返回 JSON-RPC |
| CHAIN_ID | `60600` (`0xecb8`) | `eth_chainId` 实测 |
| EXPLORER_BASE_URL | `https://scan-testnet.potos.hk` | 提交前由本人打开链接核对 |
| TOKEN_ADDRESS | `0xE48d6b8037972C2D056ef4aEd4352C6027dE88ee` | `eth_getCode` 非空，非 DHT 旧代币 |
| TOKEN_NAME | `YZHToken` | ERC20 `name()` 实测 |
| TOKEN_SYMBOL | `YZH202466332566` | ERC20 `symbol()` 实测 |
| TOKEN_DECIMALS | `18` | ERC20 `decimals()` 实测 |
| TOKEN_OWNER / SELLER_ADDRESS | `0xa508c9Ff372C40F1f109EdA1F8DB532fB556c30A` | `owner()` 实测；YZH 钱包 |
| BUYER_ADDRESS | `0x5FeDaeB4369432aD2A9929EaFFA9762C46050d4C` | DHT 需在本次 MetaMask 再确认 |
| TOKEN_SOURCE | `packages/hardhat/contracts/YZHToken.sol` | YZH 实验 3 仓库 `blockchain-lab3-yzh`；已核对 ABI 包含 `balanceOf`、`transfer`、`approve`、`transferFrom`、`mint` |
| DHT 代币余额 | `0`（2026-10-19 查询时） | 两单前至少补足 60 个 |
| YZH 代币余额 | `70`（2026-10-19 查询时） | owner 可本人转账或 mint；交易需本人签名 |
| BACKEND_ADDRESS | `0x6717496Fe22477B41e6Ca07353C308fF38A61797` | YZH 新建的 Backend Arbiter；2026-10-19 在 POTOS Testnet 查询余额为 0 POT，部署前需充值少量测试币 |
| ESCROW_ADDRESS | 待后端新部署 | 不沿用实验 3 地址 |
| DEPLOY_TX_HASH / DEPLOY_BLOCK | 待真实部署回执 | 不预填 |
| 后端端口 | `127.0.0.1:8080` | 本地监听，暂未提供认证 |

后端私钥仅从本机 `WEB3J_PRIVATE_KEY` 环境变量读取；本表、Git 和报告中均不保存其值。
