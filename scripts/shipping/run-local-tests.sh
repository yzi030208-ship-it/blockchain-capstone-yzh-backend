#!/usr/bin/env bash
set -euo pipefail
projectRoot=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)
cd "$projectRoot"

# Reuse installed, pinned course tools. No wallet environment is needed.
if [[ -z ${HARDHAT_PROJECT:-} ]]; then
    for candidate in "$HOME/blockchain-course/my-first-dapp/packages/hardhat" \
            "$HOME/blockchain-course/blockchain-lab3-yzh/packages/hardhat"; do
        if [[ -f "$candidate/node_modules/hardhat/internal/cli/cli.js" ]]; then
            export HARDHAT_PROJECT="$candidate"
            break
        fi
    done
fi
: "${HARDHAT_PROJECT:?Set HARDHAT_PROJECT to a local Hardhat 2 course project}"
solcPath=${SOLC_EXECUTABLE:-}
if [[ -z $solcPath ]]; then
    for candidate in "$HOME"/.cache/hardhat-nodejs/compilers-v2/linux-amd64/solc-linux-amd64-v0.8.20+*; do
        if [[ -x "$candidate" ]]; then solcPath=$candidate; break; fi
    done
fi
[[ -n $solcPath && -x $solcPath ]] || { echo 'Install solc 0.8.20 or set SOLC_EXECUTABLE'; exit 1; }
"$solcPath" --version | tail -n 1
echo 'SHIPPING_VALIDATION: local disposable chain 31337; compile Solidity + generate wrappers + run Java/EVM tests'
bash scripts/dht/run-local-tests.sh "-PsolcExecutable=$solcPath" "$@"

# Archive actual results separately from testnet experiments and human screenshots.
python3 - <<'PY'
from pathlib import Path
from datetime import datetime, timezone
import hashlib, json, shutil, xml.etree.ElementTree as ET
r = Path.cwd()
run = datetime.now(timezone.utc).strftime('%Y%m%dT%H%M%SZ')
out = r / 'evidence/shipping/local-validation' / run
out.mkdir(parents=True)
suites = []
for p in sorted((r / 'build/test-results/test').glob('TEST-*.xml')):
    attr = ET.parse(p).getroot().attrib
    suites.append({k: attr[k] for k in ('name', 'tests', 'failures', 'errors', 'skipped')})
    shutil.copy2(p, out / p.name)
totals = {k: sum(int(s[k]) for s in suites) for k in ('tests', 'failures', 'errors', 'skipped')}
totals['passed'] = totals['tests'] - totals['failures'] - totals['errors'] - totals['skipped']
shipping = next(s for s in suites if s['name'].endswith('ShippingEscrowLocalIntegrationTest'))
assert int(shipping['tests']) >= 8 and shipping['skipped'] == '0', shipping
assert totals['failures'] == 0 and totals['errors'] == 0 and totals['skipped'] == 0, totals
summary = {'scope': 'Solidity 0.8.20 / Paris + generated wrappers + Java HTTP and real local EVM; NOT testnet',
           'chainId': '31337', 'result': 'PASS', 'suites': suites, 'totals': totals,
           'contractSourceSha256': hashlib.sha256((r/'src/main/resources/contracts/ShippingEscrow.sol').read_bytes()).hexdigest()}
(out / 'summary.json').write_text(json.dumps(summary, indent=2) + '\n', encoding='utf-8')
shutil.copy2(r / 'build/dht-local-tests.log', out / 'gradle.log')
print('SHIPPING_LOCAL_VALIDATION: PASS')
print(f"SHIPPING_EVM_TESTS: {shipping['tests']}, SKIPPED: {shipping['skipped']}")
print(f"TESTS: {totals['tests']}, PASSED: {totals['passed']}, SKIPPED: {totals['skipped']}, FAILURES: {totals['failures']}, ERRORS: {totals['errors']}")
print('EVIDENCE:', out)
PY
