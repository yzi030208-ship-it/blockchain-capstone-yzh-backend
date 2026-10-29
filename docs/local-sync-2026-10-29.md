# 2026年10月29日本地同步记录

**GitHub 同步状态：** 已推送至 GitHub 分支 `feat/zzl-timeline-evidence`，并创建 [PR #4](https://github.com/yzi030208-ship-it/blockchain-capstone-yzh-backend/pull/4)；等待 YZH 审查合并至 `main`。订单时间线材料已上传，独立重入挑战代码和 4C 数据仍仅保留本机。

GitHub main 基线：`a76515a1516b0ee354f7b9c996b102f42ce8494d`。

原目录 `../blockchain-capstone-yzh-backend-main/` 是没有 Git 元数据的下载副本，完整保留。当前目录 `blockchain-capstone-yzh-backend/` 为新克隆的 Git 工作副本。两者不同名，后续运行和提交请使用当前目录。以下是建立副本时的记录；当前发布状态见文首。

以 GitHub 最新代码为基础，额外复制旧副本尚未入库的独立挑战文件、ZZL 报告片段及两张 4A-8 原图。没有用旧文档覆盖新版正常单完成记录；旧副本 ZZL 的历史阶段记录追加到 team-handoff，均标明来源。旧副本其他差异仍可回查。挑战编译产物可能被现有 Git 忽略规则忽略，但已保留在本机。

## 复制文件及 SHA256

| 相对路径 | SHA256 |
| --- | --- |
| `bonus/reentrancy/Attacker.sol` | `61647ebc23b3f66ddb76856e8e2be95453fd424a194ef5095d3a18d34aa36611` |
| `bonus/reentrancy/check-account.cjs` | `0ec190bd56eea09c0d12814274671f3876ecf1eca316af3ed4fe18bd32d70071` |
| `bonus/reentrancy/compile.cjs` | `a1ebefb87d920fab5c4febfa230d1b8b1dd80cd11c6bf5f447e73d65b5516c88` |
| `bonus/reentrancy/deploy-vulnerable.cjs` | `d8a4e017127e58c0c7eef021660340baad0911882364b8b6b39a5f8a249e1634` |
| `bonus/reentrancy/FixedVault.so` | `e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855` |
| `bonus/reentrancy/FixedVault.sol` | `f371af2f4bbf008924d56ace16efeb9ba32b1615703c6c33fd182bb568fda749` |
| `bonus/reentrancy/rpc-provider.cjs` | `a60ed3f44d77954744112da9ecf1a886623e288e50fe1bd122bb79afb4e93d5a` |
| `bonus/reentrancy/VulnerableVault.sol` | `ceb346dd0d7df77b6bc184dca835c2fb22d20d7e5b65a86dbae7345a1e330dd3` |
| `bonus/reentrancy/build/bonus_reentrancy_Attacker_sol_Attacker.abi` | `db5709ef2fcf73fbc07d349b8d7a8e21f58274f8fa856eeb98f2d71a9d453cff` |
| `bonus/reentrancy/build/bonus_reentrancy_Attacker_sol_Attacker.bin` | `4f8de69267e185ae725f92bd585359975e6a5aa67647479f3e3d03c92ae3b158` |
| `bonus/reentrancy/build/bonus_reentrancy_Attacker_sol_IVulnerableVault.abi` | `37d88795ea9e9b77ed2ca1e015a63670e252653b287e70bae6dee2a4f36933bc` |
| `bonus/reentrancy/build/bonus_reentrancy_Attacker_sol_IVulnerableVault.bin` | `01ba4719c80b6fe911b091a7c05124b64eeece964e09c058ef8f9805daca546b` |
| `bonus/reentrancy/build/bonus_reentrancy_FixedVault_sol_FixedVault.abi` | `1ac261b981179690f491f693991934f095676622e063612c81ff558197548663` |
| `bonus/reentrancy/build/bonus_reentrancy_FixedVault_sol_FixedVault.bin` | `1894d1002d76832b74cff1c43c2601c47ff563f99dbdba8dfd8e54546cd7952d` |
| `bonus/reentrancy/build/bonus_reentrancy_VulnerableVault_sol_VulnerableVault.abi` | `1ac261b981179690f491f693991934f095676622e063612c81ff558197548663` |
| `bonus/reentrancy/build/bonus_reentrancy_VulnerableVault_sol_VulnerableVault.bin` | `3679b29c6db1a8613798fe7ca0a434a3cff9ad218674849e89985f47932e1b75` |
| `docs/report-parts/ZZL.md` | `7dfbfb77522220fba5f725346bb8a8645a9c44fde22bff7476b18ae8c157d445` |
| `evidence/4A/4A-8-01_ZZL_resolve浏览器详情.png` | `fff487922958ce69c3e3b5a75e78a269e05a14884416c81c75a0f06d23a82a2c` |
| `evidence/4A/4A-8-02_ZZL_resolve日志.png` | `e6170480026028df1588544588e9c919bafd87315b464c9a0def45dcca41a81c` |
| `evidence/4C/6.1-deploy-vulnerable.json` | `ca402be6c69a913a6a15224f710df79222b6c121544cdcc264bc0210078122c6` |


## 本次 GitHub 发布范围

本次仅提交订单时间线测试、核验脚本、报告说明、两张既有 4A-8 图及完整时间线证据。旧副本的 bonus/reentrancy/ 和 evidence/4C/6.1-deploy-vulnerable.json 只在本机保留，不混入此次时间线提交。ZZL 报告片段中的挑战部分是历史准备状态，不作为本次挑战代码交付。按组内协作约定使用 feat/zzl-timeline-evidence 分支向 main 提交 PR，由 YZH 审查合并；本机绝对路径和原附件文件名仅作为来源记录。
