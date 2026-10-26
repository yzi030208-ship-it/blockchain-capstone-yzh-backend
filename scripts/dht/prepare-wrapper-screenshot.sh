#!/usr/bin/env bash
# Produce actual compiler output for DHT to capture in their own terminal.
set -euo pipefail
projectRoot=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)
cd "$projectRoot"
mkdir -p build
compilerArgs=()
cachedCompiler="$HOME/.cache/hardhat-nodejs/compilers-v2/linux-amd64/solc-linux-amd64-v0.8.20+commit.a1b79de6"
if [[ -n "${SOLC_EXECUTABLE:-}" ]]; then
    test -x "$SOLC_EXECUTABLE"
    compilerArgs+=("-PsolcExecutable=$SOLC_EXECUTABLE")
    "$SOLC_EXECUTABLE" --version
elif [[ -x "$cachedCompiler" ]]; then
    compilerArgs+=("-PsolcExecutable=$cachedCompiler")
    "$cachedCompiler" --version
fi
java -version
printf '\nRunning actual Solidity compilation and Web3j generation\n'
bash gradlew generateContractWrappers -PcompileContracts=true \
    --rerun-tasks --console=plain "${compilerArgs[@]}" "$@" 2>&1 \
    | tee build/dht-wrapper-screenshot.log
printf '\nGenerated Java files\n'
find src/main/java/com/wetech/demo/web3j/contracts -type f -name '*.java' -print | sort
