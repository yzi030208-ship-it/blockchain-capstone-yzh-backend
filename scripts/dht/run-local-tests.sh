#!/usr/bin/env bash
set -euo pipefail
projectRoot=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)
cd "$projectRoot"
: "${HARDHAT_PROJECT:?Set HARDHAT_PROJECT to an existing Hardhat project with installed dependencies}"
hardhatRoot=$(cd -- "$HARDHAT_PROJECT" && pwd)
hardhatCli="$hardhatRoot/node_modules/hardhat/internal/cli/cli.js"
test -f "$hardhatCli"
mkdir -p build
nodePort=18545
# Refuse to attach tests to another process's node; never reset an existing chain.
node -e 'const s=require("net").connect(18545,"127.0.0.1");s.on("connect",()=>{s.end();process.exit(1)});s.on("error",()=>process.exit(0));'
(
    cd "$hardhatRoot"
    exec node "$hardhatCli" --config "$projectRoot/scripts/dht/hardhat.config.cjs" \
        node --hostname 127.0.0.1 --port "$nodePort"
) > build/dht-local-node.log 2>&1 &
nodePid=$!
cleanup() {
    kill "$nodePid" 2>/dev/null || true
    wait "$nodePid" 2>/dev/null || true
    # Hardhat prints public development keys. Keep even those out of deliverables.
    rm -f -- build/dht-local-node.log
}
trap cleanup EXIT INT TERM
export DHT_LOCAL_RPC="http://127.0.0.1:$nodePort"
ready=false
for attempt in {1..30}; do
    if curl -fsS -H 'Content-Type: application/json' \
        --data '{"jsonrpc":"2.0","id":1,"method":"eth_chainId","params":[]}' \
        "$DHT_LOCAL_RPC" 2>/dev/null | node -e \
        'let s="";process.stdin.on("data",d=>s+=d);process.stdin.on("end",()=>{try{process.exit(JSON.parse(s).result==="0x7a69"?0:1)}catch{process.exit(1)}})'; then
        ready=true
        break
    fi
    kill -0 "$nodePid" 2>/dev/null || { printf 'Local test node failed to start\n' >&2; exit 1; }
    sleep 1
done
test "$ready" = true
# Add -PsolcExecutable=/absolute/path/to/solc as an argument if already installed.
bash gradlew test -PcompileContracts=true --rerun-tasks --console=plain "$@" | tee build/dht-local-tests.log
