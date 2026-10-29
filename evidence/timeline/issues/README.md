# CMD 调用语法问题

ZZL 提供原始截图，当前终端为命令提示符 CMD，项目目录已切换成功。在 CMD 中执行 PowerShell 的 `& "python.exe" ...` 调用语法，出现“此时不应有 &”。Python 核验脚本尚未启动，不能把此图记为 RPC 失败或核验失败。

修正：当前 CMD 直接执行带引号的解释器路径与脚本参数，去掉开头的 `&`。PowerShell 才需要该调用符。

本图用于报告 4.6 问题与解决的过程材料，不能替代后续 RESULT: PASS 的核验截图。成功复测原图已收到，见 ../T-4-01_ZZL_本人链上时间线核验PASS.png；对应 verification/20261029T152047601888Z/summary.json 为 PASS。
