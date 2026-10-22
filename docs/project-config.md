# 本组公开配置

本表只保存可公开的链上地址和运行参数。2026-10-22 已核对本次新 Escrow 部署；余额会随交易变化，正式验收前须重新读取。

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
| DHT 代币余额 | `60`（2026-10-22 转账后） | 指定合约 `balanceOf` 已读；两单前仍须复查 |
| YZH 代币余额 | `10`（2026-10-22 转账后） | 指定合约 `balanceOf` 已读；owner 可本人转账或 mint |
| BACKEND_ADDRESS | `0x6717496Fe22477B41e6Ca07353C308fF38A61797` | YZH 新建的 Backend Arbiter；2026-10-22 已收到 0.0001 POT 测试币，充值交易见下文 |
| ESCROW_ADDRESS | `0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5` | 本次后端新部署；不得使用实验 3 旧地址 |
| ESCROW_ARBITER | `0x6717496Fe22477B41e6Ca07353C308fF38A61797` | `GET /api/escrow/address` 与链上返回一致 |
| DEPLOY_TX_HASH | `0x4f61670afea0a4714f89c02e177f14b7d2737fa1cad9cac797727344ec187372` | RPC 回执 `status=0x1`，`contractAddress` 与上方一致 |
| DEPLOY_BLOCK | `2672683` | 后端响应与 RPC 回执一致；重启 `load` 时使用 |
| 后端端口 | `127.0.0.1:8080` | 本地监听，暂未提供认证 |

后端私钥仅从本机 `WEB3J_PRIVATE_KEY` 环境变量读取；本表、Git 和报告中均不保存其值。

2026-10-22 充值核对：YZH 钱包 `0xa508c9Ff372C40F1f109EdA1F8DB532fB556c30A` 向后端账户转入 `0.0001 POT`，交易哈希 `0xda990292aedba2361a4679c950f22510f511d02c7a673d2dec4bf857df681cfb`，区块 `2672654`，回执状态 `0x1`。

同日 YZH 从指定 YZHToken 合约向 DHT 买家转入 60 枚，交易哈希 `0x698aa6302106bfe33a9a71ed0e421fead150ccf84b6519883cbfda2c20935f30`，区块 `2672655`，回执 `0x1`。转后 `balanceOf(DHT)=60`、`balanceOf(YZH)=10`；钱包中另一个同名合约 `0x7640f3dee667Ea05CA6c9450cF35b596577025A6` 的余额不能算入本项目。

新 Escrow 的部署截图见 `evidence/4A/4A-5-01_YZH_后端部署新Escrow_地址哈希区块.png`，链上 arbiter 核对截图见 `evidence/4A/4A-5-01b_YZH_新Escrow仲裁人核对.png`。后续仅使用该地址；后端重启应 `load` 原地址与部署区块，不要再次 `deploy`。
