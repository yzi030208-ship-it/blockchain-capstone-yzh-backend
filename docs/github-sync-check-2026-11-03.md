# 两项加分内容 GitHub 同步核查

本文件保留本次只读检查时的状态。随后ZZL明确要求单独同步卖家发货项，发布结果以[发货交付说明](shipping-delivery.md)的最新记录为准。

核查日期：2026-11-03，Asia/Shanghai。通过 `git ls-remote`、`git fetch origin --prune`、远端提交树、证据清单SHA256及GitHub PR API核对，未推送任何本地修改，也未合并远端main到当前有未提交内容的工作目录。

仓库：[blockchain-capstone-yzh-backend](https://github.com/yzi030208-ship-it/blockchain-capstone-yzh-backend)。本次实际远端main为 `cfaf0cefa62b8b966e82132ac08f12d19eb5a972`。

| 项目 | 代码 | 证据与文档 | GitHub状态 |
| --- | --- | --- | --- |
| 订单时间线 | 远端main含EscrowController、EscrowEventQueryService、时间线测试与核验脚本 | main含evidence/timeline/下53个文件、交付说明和报告素材；清单涉及68个文件（含复用证据）SHA256全部匹配 | 已上传并合并main |
| 卖家发货状态 | ShippingEscrow.sol、独立Java封装和shipping服务/接口/测试仅在本机未提交工作目录 | 27张分类原图及本地测试、链上回执、接口快照、说明和报告素材在本机；27张原图全部与登记哈希一致 | 未提交、未上传、无对应PR |

## 订单时间线

[PR #4：完善 ZZL 订单时间线验收与实验截图归档](https://github.com/yzi030208-ship-it/blockchain-capstone-yzh-backend/pull/4)，GitHub状态closed、merged=true，合并时间2026-11-02T09:47:04Z，即北京时间2026-11-02 17:47:04。源分支 `feat/zzl-timeline-evidence` 提交 `56f91b9b699b67f2665104f38a41d0240f90ae0c` 已进入main。

远端已核对 `docs/timeline-delivery.md`、`docs/report-parts/timeline.md`、`scripts/verify-timeline.py`、时间线接口及证据目录。远端main与该源分支的时间线证据清单均通过68项文件哈希核对，没有缺失或不匹配。

## 卖家发货状态

本机当前分支为 `feat/zzl-shipping-status`，HEAD仍为上述时间线提交。发货合约、封装、服务/接口、测试、scripts/shipping/、evidence/shipping/及发货说明尚未产生Git提交；远端所有分支列表无该发货分支，main无ShippingEscrow、evidence/shipping/或发货交付文档，GitHub也无发货PR。

此前发货实验及关键截图已完成，不代表发布完成。[本地交付说明](shipping-delivery.md)、[截图分类](../evidence/shipping/screenshots/README.md)和[报告素材](report-parts/shipping.md)均仍保存在本机，原图按哈希检查无损。

## 后续发布安排

按ZZL最新指示，后续多签等实验结束、资料整理并检查后，再统一上传GitHub。本次仅核查并更新本地状态说明，没有提前提交、推送或创建PR。独立重入挑战及4C资料不因本次检查改变状态。
