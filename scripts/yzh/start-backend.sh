#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/../.."

# Read only from the terminal so the key is absent from shell history and screenshots.
unset WEB3J_PRIVATE_KEY
read -r -s -p 'Backend Arbiter 私钥（输入不显示，粘贴后按回车）: ' WEB3J_PRIVATE_KEY
printf '\n'
WEB3J_PRIVATE_KEY="${WEB3J_PRIVATE_KEY#0x}"
if [[ ! "$WEB3J_PRIVATE_KEY" =~ ^[0-9a-fA-F]{64}$ ]]; then
    unset WEB3J_PRIVATE_KEY
    printf '私钥格式错误：需要 64 位十六进制，可带 0x 前缀；后端未启动。\n' >&2
    exit 1
fi

export WEB3J_PRIVATE_KEY
trap 'unset WEB3J_PRIVATE_KEY' EXIT
printf '格式检查通过，正在启动后端。请保持此终端打开。\n'
bash gradlew bootRun
