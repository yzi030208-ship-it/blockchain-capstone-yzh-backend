"""Verify restrictions on the real testnet with eth_call, not signed transactions.

Requires A to be Shipped and expired, and B to be Paid. Both calls must revert
with the correct contract reason. Original RPC requests/responses are archived.
"""
from datetime import datetime, timezone, timedelta
import json
from pathlib import Path
import urllib.request

ROOT = Path(__file__).resolve().parents[2]


def require(condition, message):
    if not condition:
        raise ValueError(message)


def decode_reason(value):
    if isinstance(value, dict):
        for item in value.values():
            result = decode_reason(item)
            if result:
                return result
    if isinstance(value, str) and value.startswith('0x08c379a0'):
        try:
            raw = value[10:]
            offset = int(raw[:64], 16) * 2
            size = int(raw[offset:offset + 64], 16)
            return bytes.fromhex(raw[offset + 64:offset + 64 + size * 2]).decode('utf-8')
        except (ValueError, UnicodeDecodeError):
            return None
    return None


def main():
    config = json.loads((ROOT/'docs/shipping-public-config.json').read_text(encoding='utf-8'))
    address = config['shippingContractAddress']
    caller = config['roles']['buyer']
    a_id = int(config['experimentOrders']['A']['id'])
    b_id = int(config['experimentOrders']['B']['id'])
    started = datetime.now(timezone.utc)
    out = ROOT/'evidence/shipping/restriction-checks'/started.strftime('%Y%m%dT%H%M%S%fZ')
    out.mkdir(parents=True)
    trace = []
    summary = {'startedAtUtc': started.isoformat(), 'mode': 'LIVE_TESTNET_ETH_CALL_NO_TX',
        'scope': 'Caller-specific pre-execution simulation against real deployed code and storage. No wallet signature, transaction hash, broadcast or gas charge.',
        'contractAddress': address, 'caller': caller, 'checks': [], 'result': 'FAIL'}

    def rpc(method, params, allow_error=False):
        payload = {'jsonrpc': '2.0', 'id': len(trace) + 1, 'method': method, 'params': params}
        req = urllib.request.Request(config['rpcUrl'], data=json.dumps(payload).encode('utf-8'),
            headers={'Content-Type': 'application/json', 'User-Agent': 'ZZL-Shipping-Lab/1.0'})
        with urllib.request.urlopen(req, timeout=30) as response:
            result = json.load(response)
        trace.append({'request': payload, 'response': result})
        if allow_error:
            return result
        require('error' not in result, f'{method}: {result.get("error")}')
        require(result.get('result') is not None, f'{method}: missing result')
        return result['result']

    def selector(signature):
        return rpc('web3_sha3', ['0x' + signature.encode('ascii').hex()])[:10]

    try:
        require(int(rpc('eth_chainId', []), 16) == 60600, 'Expected POTOS Testnet 60600')
        head = rpc('eth_getBlockByNumber', ['latest', False])
        block = head['number']
        now = int(head['timestamp'], 16)
        get_order = selector('getOrder(uint256)')

        def order(order_id):
            raw = rpc('eth_call', [{'to': address, 'data': get_order + f'{order_id:064x}'}, block])[2:]
            base = int(raw[:64], 16) * 2
            words = [raw[base + i * 64:base + (i + 1) * 64] for i in range(7)]
            return {'id': str(order_id), 'buyer': '0x' + words[0][-40:],
                'seller': '0x' + words[1][-40:], 'token': '0x' + words[2][-40:],
                'amount': str(int(words[3], 16)), 'deadline': str(int(words[4], 16)),
                'statusCode': int(words[5], 16)}

        a, b = order(a_id), order(b_id)
        for item in [a, b]:
            require(item['buyer'].lower() == caller.lower(), 'Order buyer mismatch')
            require(item['seller'].lower() == config['roles']['seller'].lower(), 'Order seller mismatch')
            require(item['token'].lower() == config['tokenAddress'].lower(), 'Order token mismatch')
        require(a['statusCode'] == 5, 'A must remain Shipped; do not confirm it before this check')
        require(now > int(a['deadline']), 'A deadline not reached; wait for real chain time')
        require(b['statusCode'] == 1, 'B must remain Paid and unshipped')
        summary.update({'chainId': '60600', 'blockNumber': str(int(block, 16)),
            'blockHash': head['hash'], 'blockTimestamp': str(now), 'orders': {'A': a, 'B': b}})

        def expect_revert(label, signature, order_id, expected):
            data = selector(signature) + f'{order_id:064x}'
            result = rpc('eth_call', [{'from': caller, 'to': address, 'data': data,
                'gas': hex(6_000_000)}, block], allow_error=True)
            error = result.get('error')
            require(error is not None, f'{label}: call unexpectedly succeeded')
            decoded = decode_reason(error)
            reason = decoded or error.get('message', '')
            require(expected in reason or expected in error.get('message', ''),
                    f'{label}: unexpected RPC failure, not the required contract revert: {reason}')
            item = {'label': label, 'function': signature, 'orderId': str(order_id),
                'result': 'REVERTED', 'actualReason': reason, 'rawError': error,
                'callFrom': caller, 'blockNumber': summary['blockNumber']}
            summary['checks'].append(item)
            return item

        refund = expect_revert('EXPIRED_SHIPPED_A_REFUND', 'refund(uint256)', a_id, 'Escrow: wrong status')
        shipping = expect_revert('BUYER_SHIP_B', 'markShipped(uint256)', b_id, 'Escrow: caller is not the seller')
        balance = int(rpc('eth_call', [{'to': config['tokenAddress'],
            'data': '0x70a08231' + address[2:].lower().rjust(64, '0')}, block]), 16)
        summary['escrowTokenBalanceRaw'] = str(balance)
        summary['result'] = 'RESTRICTIONS_PASS'
        print('MODE: LIVE_TESTNET_ETH_CALL_NO_TX')
        print('CHAIN_ID: 60600  BLOCK:', summary['blockNumber'])
        print('CONTRACT:', address)
        print('CALLER (buyer):', caller)
        print('BLOCK_TIME:', now, datetime.fromtimestamp(now, timezone(timedelta(hours=8))).isoformat())
        print(f"ORDER A id={a_id}: Shipped, deadline={a['deadline']}, expired=True")
        print(f"ORDER B id={b_id}: Paid, deadline={b['deadline']}")
        print('REFUND_A:', refund['result'], '|', refund['actualReason'])
        print('BUYER_SHIP_B:', shipping['result'], '|', shipping['actualReason'])
        print('ESCROW_TOKEN_BALANCE_RAW:', balance)
        print('RESULT: RESTRICTIONS_PASS')
    finally:
        summary['finishedAtUtc'] = datetime.now(timezone.utc).isoformat()
        (out/'summary.json').write_text(json.dumps(summary, ensure_ascii=False, indent=2)+'\n', encoding='utf-8')
        (out/'rpc-trace.json').write_text(json.dumps(trace, ensure_ascii=False, indent=2)+'\n', encoding='utf-8')
        print('EVIDENCE:', out)


if __name__ == '__main__':
    main()
