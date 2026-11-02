"""Read-only timeline evidence check. Uses only Python's standard library.

python scripts/verify-timeline.py
python scripts/verify-timeline.py --api http://127.0.0.1:8080
No wallet, private key, deploy, resolve or other transaction is used.
"""
import argparse
from datetime import datetime, timezone
import json
from pathlib import Path
import urllib.request

ROOT = Path(__file__).resolve().parents[1]
ESCROW = "0xDb666Df58291D7d49cea35eE36929dCcD37e2ea5"
DEPLOY_BLOCK = "2672683"
SOURCES = {
    "0": "T-1_YZH_正常单完整时间线.json",
    "1": "T-2_YZH_争议单时间线.json",
}
SEQUENCES = {
    "0": ["OrderCreated", "OrderPaid", "OrderCompleted"],
    "1": ["OrderCreated", "OrderPaid", "OrderDisputed", "OrderResolved"],
}
SIGNATURES = {
    "OrderCreated": "OrderCreated(uint256,address,address,address,uint256,uint256)",
    "OrderPaid": "OrderPaid(uint256)",
    "OrderCompleted": "OrderCompleted(uint256)",
    "OrderRefunded": "OrderRefunded(uint256)",
    "OrderDisputed": "OrderDisputed(uint256,address)",
    "OrderResolved": "OrderResolved(uint256,bool)",
}


def request(url, body=None):
    data = None if body is None else json.dumps(body).encode("utf-8")
    req = urllib.request.Request(url, data=data, headers={
        "Content-Type": "application/json", "User-Agent": "YZH-Course-Lab/1.0"})
    with urllib.request.urlopen(req, timeout=180 if body is None else 30) as response:
        return json.load(response)


def save(path, value):
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def require(condition, message):
    if not condition:
        raise ValueError(message)


def word(value):
    return f"{int(value):064x}"


def address_word(value):
    return value.lower().removeprefix("0x").rjust(64, "0")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--rpc", default="https://rpc-testnet.potos.hk")
    parser.add_argument("--api", help="Optional running backend; must already load this Escrow")
    args = parser.parse_args()
    started = datetime.now(timezone.utc)
    out = ROOT / "evidence/timeline/verification" / started.strftime("%Y%m%dT%H%M%S%fZ")
    out.mkdir(parents=True)
    summary = {
        "startedAtUtc": started.isoformat(), "mode": "live-api-and-receipts" if args.api else "archived-api-and-live-receipts",
        "rpcUrl": args.rpc, "contractAddress": ESCROW, "deployBlock": DEPLOY_BLOCK,
        "scope": "Verify the seven recorded events against receipts and ten-block RPC windows; not a full history rescan or restart test.",
        "result": "RUNNING", "orders": [],
    }
    request_id = 0

    def rpc(method, params):
        nonlocal request_id
        request_id += 1
        response = request(args.rpc, {"jsonrpc": "2.0", "id": request_id, "method": method, "params": params})
        require("error" not in response, f"{method}: {response.get('error')}")
        require(response.get("result") is not None, f"{method}: missing result")
        return response["result"]

    try:
        require(int(rpc("eth_chainId", []), 16) == 60600, "Wrong chain; expected 60600")
        summary["chainId"] = "60600"
        summary["chainHead"] = str(int(rpc("eth_blockNumber", []), 16))
        summary["topic0"] = {
            name: rpc("web3_sha3", ["0x" + signature.encode("ascii").hex()])
            for name, signature in SIGNATURES.items()
        }
        selector = rpc("web3_sha3", ["0x" + b"getOrder(uint256)".hex()])[:10]
        if args.api:
            context = request(args.api.rstrip("/") + "/api/escrow/address")
            require(context["contractAddress"].lower() == ESCROW.lower(), "Backend loaded a different Escrow")
            require(context["deployBlock"] == DEPLOY_BLOCK, "Backend deployment block mismatch")
            save(out / "backend-context.json", context)
        for order_id, filename in SOURCES.items():
            events = json.loads((ROOT / "evidence/timeline" / filename).read_text(encoding="utf-8"))
            if args.api:
                events = request(args.api.rstrip("/") + f"/api/escrow/orders/{order_id}/events")
            save(out / f"order-{order_id}-events.json", events)
            require([event["eventName"] for event in events] == SEQUENCES[order_id], f"Order {order_id}: unexpected sequence")
            positions = [(int(e["blockNumber"]), int(e["transactionIndex"]), int(e["logIndex"])) for e in events]
            require(positions == sorted(positions), "Events are not ordered numerically")
            identities = {(e["transactionHash"].lower(), e["logIndex"]) for e in events}
            require(len(identities) == len(events), "Duplicate events")
            for event in events:
                require(event["orderId"] == order_id, "Timeline mixes order IDs")
                tx = event["transactionHash"]
                receipt = rpc("eth_getTransactionReceipt", [tx])
                save(out / f"{tx}-receipt.json", receipt)
                require(receipt["transactionHash"].lower() == tx.lower(), "Receipt hash mismatch")
                require(int(receipt["status"], 16) == 1, "Event transaction failed")
                require(receipt["to"].lower() == ESCROW.lower(), "Receipt belongs to another contract")
                require(int(receipt["blockNumber"], 16) == int(event["blockNumber"]), "Receipt block mismatch")
                require(int(receipt["transactionIndex"], 16) == int(event["transactionIndex"]), "Receipt transaction index mismatch")
                matches = [log for log in receipt["logs"] if log["address"].lower() == ESCROW.lower()
                           and int(log["logIndex"], 16) == int(event["logIndex"])]
                require(len(matches) == 1, "Missing or ambiguous Escrow log")
                log = matches[0]
                require(not log.get("removed", False), "Log was removed by a chain reorganization")
                expected_topics = [summary["topic0"][event["eventName"]], "0x" + word(order_id)]
                values = event["args"]
                expected_data = "0x"
                if event["eventName"] == "OrderCreated":
                    expected_topics.extend(["0x" + address_word(values["buyer"]), "0x" + address_word(values["seller"])])
                    expected_data += address_word(values["token"]) + word(values["amount"]) + word(values["deadline"])
                elif event["eventName"] == "OrderDisputed":
                    expected_data += address_word(values["by"])
                elif event["eventName"] == "OrderResolved":
                    require(values["sellerWins"] in ("true", "false"), "Invalid boolean argument")
                    expected_data += word(values["sellerWins"] == "true")
                require([t.lower() for t in log["topics"]] == [t.lower() for t in expected_topics], "Event signature or indexed arguments mismatch")
                require(log["data"].lower() == expected_data, "Event decoded arguments mismatch")
                block = int(event["blockNumber"])
                window_end = min(block + 9, int(summary["chainHead"]))
                window = rpc("eth_getLogs", [{"address": ESCROW, "fromBlock": hex(block),
                    "toBlock": hex(window_end), "topics": [expected_topics[0], expected_topics[1]]}])
                save(out / f"order-{order_id}-window-{block}.json", window)
                require(any(item["transactionHash"].lower() == tx.lower()
                    and int(item["logIndex"], 16) == int(event["logIndex"]) for item in window), "Event absent from ten-block query")
            encoded_order = rpc("eth_call", [{"to": ESCROW, "data": selector + word(order_id)}, "latest"])
            save(out / f"order-{order_id}-eth-call.json", {"result": encoded_order})
            raw = encoded_order.removeprefix("0x")
            offset = int(raw[:64], 16) * 2
            status = int(raw[offset + 5 * 64:offset + 6 * 64], 16)
            expected_status = 2 if order_id == "0" else 3
            require(status == expected_status, f"Order {order_id}: final status mismatch")
            summary["orders"].append({"id": order_id, "events": len(events),
                "sequence": SEQUENCES[order_id], "status": "Completed" if status == 2 else "Refunded"})
        summary["result"] = "PASS"
    except Exception as error:
        summary["result"] = "FAIL"
        summary["error"] = str(error)
        raise
    finally:
        summary["finishedAtUtc"] = datetime.now(timezone.utc).isoformat()
        save(out / "summary.json", summary)
        print(f"RESULT: {summary['result']}")
        print(f"MODE: {summary['mode']}")
        print(f"EVIDENCE: {out}")
        for order in summary["orders"]:
            print(f"ORDER {order['id']}: {order['status']}, {order['events']} events")


if __name__ == "__main__":
    main()
