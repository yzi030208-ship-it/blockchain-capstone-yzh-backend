# 截图证据索引

本表登记截图编号、截图人、原图与仓库副本、以及 ZZL 的核对结果。原图必须由实际操作者保存在本机，仓库副本放在对应 `evidence/` 目录；未取得的截图保留“待提供”，不以文件名代替证据。

| 编号 | 截图人 | 仓库路径 | 合约 / 订单 | 核对结果 |
| --- | --- | --- | --- | --- |
| 4A-3-01 | YZH | `evidence/4A/4A-3-01_YZH_SimpleStorage部署写入读取42.png` | SimpleStorage `0xD18e…50b3A` | 文件已存在；需提交前复核原图可读、无密钥 |
| 4A-3-02 | YZH | `evidence/4A/4A-3-02_YZH_SimpleStorage写入42链上成功.png` | SimpleStorage 写入 42，tx `0x0563…5584` | 文件已存在；交易哈希与实验记录一致 |
| 4A-5-01 | YZH | `evidence/4A/4A-5-01_YZH_后端部署新Escrow_地址哈希区块.png` | Escrow `0xDb66…e2ea5`，区块 2672683 | 文件已存在；地址、部署哈希、区块待按原图复核 |
| 4A-5-01b | YZH | `evidence/4A/4A-5-01b_YZH_新Escrow仲裁人核对.png` | Escrow arbiter `0x6717…1797` | 文件已存在；应与公开配置和部署账户一致 |
| 4A-5-02-1 | DHT | `evidence/4A/4A-5-02_DHT_两单创建_1.png` | 新 Escrow，订单 0 创建参数 | 文件已同步；交易 `0x782a…f6524b6`，RPC 回执成功 |
| 4A-5-02-2 | DHT | `evidence/4A/4A-5-02_DHT_两单创建_2.png` | 新 Escrow，订单 0 创建回执 | 文件已同步；与订单 0 创建交易对应 |
| 4A-5-02-3 | DHT | `evidence/4A/4A-5-02_DHT_两单创建_3.png` | 新 Escrow，订单 1 创建参数 | 文件已同步；交易 `0xd224…55a333`，RPC 回执成功 |
| 4A-5-02-4 | DHT | `evidence/4A/4A-5-02_DHT_两单创建_4.png` | 新 Escrow，订单 1 创建回执 | 文件已同步；与订单 1 创建交易对应 |
| 4A-5-03-1 | DHT | `evidence/4A/4A-5-03_DHT_approve新Escrow_1.png` | 指定 YZHToken → 新 Escrow，60 枚 | 文件已同步；approve 回执成功 |
| 4A-5-03-2 | DHT | `evidence/4A/4A-5-03_DHT_approve新Escrow_2.png` | allowance=60 枚 | 文件已同步；对应 approve 交易 |
| 4A-5-04-1 | DHT | `evidence/4A/4A-5-04_DHT_两单deposit_1.png` | 订单 0 deposit 回执和日志 | 文件已同步；Paid，交易 `0xe4e6…247b46` |
| 4A-5-04-2 | DHT | `evidence/4A/4A-5-04_DHT_两单deposit_2.png` | 订单 1 deposit 回执和日志 | 文件已同步；Paid，交易 `0xdcfa…287db7` |
| 4A-5-04-3a | DHT | `evidence/4A/4A-5-04_DHT_两单deposit_3_1.png` | `getOrder(0)` 为 Paid | 文件已同步；与订单 0 付款对应 |
| 4A-5-04-3b | DHT | `evidence/4A/4A-5-04_DHT_两单deposit_3_2.png` | `getOrder(1)` 为 Paid | 文件已同步；与订单 1 付款对应 |
| 4A-5-05 | YZH | `evidence/4A/4A-5-05_YZH_卖家dispute成功_订单1Disputed.png` | 新 Escrow，订单 1 | 文件已同步；交易 `0xe394…e4db91`，Disputed |
| 4A-6-01-json | YZH | `evidence/4A/4A-6-01_YZH_订单列表.json` | 新 Escrow，两单列表 | JSON 已同步；PNG 截图仍待补 |
| 4A-6-02a-json | YZH | `evidence/4A/4A-6-02a_YZH_订单0_Paid.json` | 订单 0 | JSON 已同步；确认 Paid，PNG 截图仍待补 |
| 4A-6-02b-json | YZH | `evidence/4A/4A-6-02b_YZH_订单1_Disputed.json` | 订单 1 | JSON 已同步；确认 Disputed，PNG 截图仍待补 |
| 4A-7-01-json | YZH | `evidence/4A/4A-7-01_YZH_未争议resolve失败回执.json` | 订单 0 | JSON 已同步；链上失败 `status=0x0`，PNG 截图仍待补 |
| 4A-7-02-json | YZH | `evidence/4A/4A-7-02_YZH_争议订单退款成功回执.json` | 订单 1 | JSON 已同步；成功退款 30 枚，PNG 截图仍待补 |
| 4A-7-03-json | YZH | `evidence/4A/4A-7-03_YZH_订单1_Refunded.json` | 订单 1 | JSON 已同步；业务状态 Refunded，PNG 截图仍待补 |
| 4A-8-01 | ZZL | `evidence/4A/4A-8-01_ZZL_resolve浏览器详情.png` | 新 Escrow，订单 1 | 已核对；显示成功哈希、Success、区块 2689270、后端 From、新 Escrow 合约和 30 枚转给 DHT；原图与仓库副本已保存 |
| 4A-8-02 | ZZL | `evidence/4A/4A-8-02_ZZL_resolve日志.png` | 新 Escrow，订单 1 | 已核对；显示 YZHToken Transfer，from=Escrow、to=DHT、value=30000000000000000000；原图与仓库副本 SHA-256 一致 |
| 4A-S1 | DHT | `evidence/4A/4A-S1_DHT_正常单Completed.png` | 新 Escrow，正常单 | 待提供；需核对 confirm 回执和 Completed |
| 4A-S2-01 | YZH | `evidence/4A/4A-S2-01_YZH_卖家收款.png` | 指定 YZHToken，卖家 | 待提供；需核对收到 30 枚 |
| 4A-S2-02 | DHT | `evidence/4A/4A-S2-02_DHT_买家退款.png` | 指定 YZHToken，买家 | 待提供；需核对退款 30 枚 |
| 4C-1-01 | ZZL | `evidence/4C/4C-1-01_ZZL_受害者存款.png` | 独立 VulnerableVault | 待完成；需保存本机原图和仓库副本 |
| 4C-1-02 | ZZL | `evidence/4C/4C-1-02_ZZL_漏洞攻击结果.png` | 独立 VulnerableVault / Attacker | 待完成；需记录真实攻击回执和余额变化 |
| 4C-2-01 | ZZL | `evidence/4C/4C-2-01_ZZL_攻击内部交易.png` | attack 交易 | 待完成；真实无索引时如实保存空列表 |
| 4C-3-01 | ZZL | `evidence/4C/4C-3-01_ZZL_修复代码及攻击失败.png` | 独立 FixedVault | 待完成；需核对修复复测结果 |

## 核对口径

ZZL 只把画面清晰、链为 POTOS Testnet、合约地址与本组配置一致、订单号能与真实交易对应且无私密信息的截图标为已核对。DHT 提供买家画面，YZH 提供后端接口和卖家画面；若原图缺少关键字段，索引保留问题描述并要求补图。
