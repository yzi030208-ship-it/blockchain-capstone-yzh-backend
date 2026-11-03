"""Capture the running shipping API and current testnet time without signing.

Run after the backend has loaded/deployed ShippingEscrow; each invocation archives
actual JSON in evidence/shipping/checkpoints. This does not send transactions.
"""
import argparse
from datetime import datetime, timezone, timedelta
import json
from pathlib import Path
import urllib.request

ROOT = Path(__file__).resolve().parents[2]


def request(url, data=None):
    encoded = None if data is None else json.dumps(data).encode('utf-8')
    req = urllib.request.Request(url, data=encoded, headers={
        'Content-Type': 'application/json', 'User-Agent': 'ZZL-Shipping-Lab/1.0'})
    with urllib.request.urlopen(req, timeout=180 if data is None else 30) as resp:
        result = json.load(resp)
    if data is None and isinstance(result, dict) and 'error' in result:
        raise ValueError(f'{url}: {result["error"]}')
    return result


def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument('--api', default='http://127.0.0.1:8081')
    p.add_argument('--rpc', default='https://rpc-testnet.potos.hk')
    p.add_argument('--order', type=int, action='append', default=[])
    p.add_argument('--final', action='store_true', help='Check the completed A/refunded B experiment')
    args = p.parse_args()
    if any(i < 0 or i.bit_length() > 256 for i in args.order): p.error('order must be uint256')
    api = args.api.rstrip('/') + '/api/shipping/escrow'

    def rpc(method, params):
        r = request(args.rpc, {'jsonrpc': '2.0', 'id': 1, 'method': method, 'params': params})
        if 'error' in r: raise ValueError(f'{method}: {r["error"]["message"]}')
        return r['result']

    if int(rpc('eth_chainId', []), 16) != 60600: raise ValueError('Expected POTOS Testnet 60600')
    context = request(api + '/address')
    account = request(api + '/account')
    ids = args.order or [int(o['id']) for o in request(api + '/orders')]
    details = [request(api + f'/orders/{i}') for i in ids]
    events = {str(i): request(api + f'/orders/{i}/events') for i in ids}
    head = rpc('eth_getBlockByNumber', ['latest', False])
    timestamp = int(head['timestamp'], 16)
    balances = {}
    for order in details:
        token = order['token']
        for who in [order['buyer'], order['seller'], context['contractAddress']]:
            key = token.lower() + ':' + who.lower()
            if key not in balances:
                balances[key] = str(int(rpc('eth_call', [{'to': token,
                    'data': '0x70a08231' + who[2:].lower().rjust(64, '0')}, head['number']]), 16))
    now = datetime.now(timezone.utc)
    out = ROOT/'evidence/shipping/checkpoints'/now.strftime('%Y%m%dT%H%M%S%fZ')
    out.mkdir(parents=True)
    snapshot = {'scope': 'Actual shipping API and read-only POTOS RPC; no transaction sent',
        'capturedAtUtc': now.isoformat(), 'chainId': '60600', 'context': context,
        'account': account, 'latestBlock': {'number': str(int(head['number'], 16)),
        'timestamp': str(timestamp)}, 'orders': details, 'events': events, 'tokenBalancesRaw': balances}
    (out/'snapshot.json').write_text(json.dumps(snapshot, ensure_ascii=False, indent=2)+'\n', encoding='utf-8')
    if args.final:
        config = json.loads((ROOT/'docs/shipping-public-config.json').read_text(encoding='utf-8'))
        if context['contractAddress'].lower() != config['shippingContractAddress'].lower():
            raise ValueError('Wrong shipping contract')
        if context['arbiter'].lower() != config['roles']['arbiter'].lower():
            raise ValueError('Wrong arbiter')
        expected = {'A': ('Completed', ['OrderCreated', 'OrderPaid', 'OrderShipped', 'OrderCompleted']),
                    'B': ('Refunded', ['OrderCreated', 'OrderPaid', 'OrderRefunded'])}
        by_id = {o['id']: o for o in details}
        for label, (status, names) in expected.items():
            planned = config['experimentOrders'][label]
            order = by_id[planned['id']]
            if order['status'] != status or [e['eventName'] for e in events[order['id']]] != names:
                raise ValueError(f'{label}: unexpected final state or timeline')
            for role in ['buyer', 'seller']:
                if order[role].lower() != config['roles'][role].lower():
                    raise ValueError(f'{label}: wrong {role}')
            if order['token'].lower() != config['tokenAddress'].lower() or order['amount'] != planned['amount']:
                raise ValueError(f'{label}: wrong token or amount')
            for event, field in [('OrderCreated', 'creationTransactionHash'), ('OrderPaid', 'depositTransactionHash'),
                                 ('OrderShipped', 'shippingTransactionHash'), ('OrderCompleted', 'confirmTransactionHash'),
                                 ('OrderRefunded', 'refundTransactionHash')]:
                if field in planned:
                    actual = next(e['transactionHash'] for e in events[order['id']] if e['eventName'] == event)
                    if actual.lower() != planned[field].lower():
                        raise ValueError(f'{label}: {event} transaction mismatch')
        expected_balances = {'buyer': 29 * 10**18, 'seller': 10**18, 'escrow': 0}
        for role, expected_balance in expected_balances.items():
            who = context['contractAddress'] if role == 'escrow' else config['roles'][role]
            key = config['tokenAddress'].lower() + ':' + who.lower()
            if int(balances[key]) != expected_balance:
                raise ValueError(f'Unexpected {role} balance; inspect actual snapshot')
        snapshot['result'] = 'FINAL_PASS'
        (out/'snapshot.json').write_text(json.dumps(snapshot, ensure_ascii=False, indent=2)+'\n', encoding='utf-8')
    print('CHAIN_ID: 60600')
    print('CONTRACT:', context['contractAddress'])
    print('ARBITER:', context['arbiter'])
    print('LATEST_BLOCK_TIME:', timestamp,
        datetime.fromtimestamp(timestamp, timezone(timedelta(hours=8))).isoformat())
    for order in details:
        print(f"ORDER {order['id']}: {order['status']}, deadline={order['deadline']}, expired={timestamp > int(order['deadline'])}")
        print('  EVENTS:', ' -> '.join(e['eventName'] for e in events[order['id']]))
    if args.final:
        print('TOKEN_BALANCES: buyer=29, seller=1, escrow=0 (18 decimals; actual raw values archived)')
        print('RESULT: FINAL_PASS')
    else:
        print('BALANCES (token:account -> raw amount):')
        for key, value in balances.items(): print(' ', key, '->', value)
    print('EVIDENCE:', out)


if __name__ == '__main__':
    main()
