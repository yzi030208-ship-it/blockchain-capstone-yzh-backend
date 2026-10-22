package com.wetech.demo.web3j.service;

import com.wetech.demo.web3j.contracts.escrow.Escrow;
import com.wetech.demo.web3j.escrow.*;
import java.math.BigInteger;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/** Core gateway only; Controller and event scanning remain owned by YZH. */
@Service
public class EscrowService implements EscrowGateway {
    private static final String[] STATUS = {"Created", "Paid", "Completed", "Refunded", "Disputed"};
    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;
    private State state;

    // A single immutable snapshot prevents address/fromBlock from different loads
    // being exposed together. Synchronization also serializes gateway writes.
    private record State(Escrow contract, BigInteger deployBlock) {}

    public EscrowService(Web3j web3j, Credentials credentials, ContractGasProvider gasProvider) {
        this.web3j = web3j;
        this.credentials = credentials;
        this.gasProvider = gasProvider;
    }

    @Override
    public synchronized DeploymentResult deploy() {
        return rpc("deploy", () -> {
            Escrow deployed = Escrow.deploy(web3j, transactionManager(), gasProvider,
                    credentials.getAddress()).send();
            TransactionReceipt receipt = deployed.getTransactionReceipt()
                    .orElseThrow(() -> new EscrowRpcException("Deployment returned no receipt"));
            requireSuccessfulReceipt(receipt);
            String arbiter = deployed.arbiter().send();
            requireBackendArbiter(arbiter);
            state = new State(deployed, receipt.getBlockNumber());
            return new DeploymentResult(deployed.getContractAddress(), receipt.getTransactionHash(),
                    receipt.getBlockNumber().toString());
        });
    }

    @Override
    public synchronized EscrowContext load(String address, BigInteger fromBlock) {
        if (address == null || !address.matches("(?i)^0x[0-9a-f]{40}$")) {
            throw new IllegalArgumentException("address must be a 20-byte hex address");
        }
        uint256(fromBlock, "fromBlock");
        return rpc("load", () -> {
            var head = web3j.ethBlockNumber().send();
            if (head.hasError()) throw new EscrowRpcException(head.getError().getMessage());
            if (fromBlock.compareTo(head.getBlockNumber()) > 0) {
                throw new IllegalArgumentException("fromBlock is beyond the current chain head");
            }
            var code = web3j.ethGetCode(address, DefaultBlockParameterName.LATEST).send();
            if (code.hasError()) throw new EscrowRpcException(code.getError().getMessage());
            if (code.getCode() == null || code.getCode().matches("(?i)0x0*")) {
                throw new IllegalArgumentException("No contract code at address on the selected chain");
            }
            // Detect an obviously wrong start block rather than silently losing history.
            var originalCode = web3j.ethGetCode(address, DefaultBlockParameter.valueOf(fromBlock)).send();
            if (originalCode.hasError()) throw new EscrowRpcException(originalCode.getError().getMessage());
            if (originalCode.getCode() == null || originalCode.getCode().matches("(?i)0x0*")) {
                throw new IllegalArgumentException("Contract did not exist at fromBlock; use its deployment block");
            }
            Escrow loaded = Escrow.load(address, web3j, transactionManager(), gasProvider);
            String arbiter = loaded.arbiter().send();
            requireBackendArbiter(arbiter);
            // Probe the expected ABI before publishing the new state.
            loaded.orderCount().send();
            state = new State(loaded, fromBlock);
            return context(state, arbiter);
        });
    }

    @Override
    public synchronized EscrowContext current() {
        State snapshot = requireState();
        return rpc("current", () -> context(snapshot, snapshot.contract().arbiter().send()));
    }

    /** Read-only helpers for integrations that need typed values. */
    public synchronized String getContractAddress() { return requireState().contract().getContractAddress(); }
    public synchronized BigInteger getDeployBlock() { return requireState().deployBlock(); }

    @Override
    public synchronized OrderDetail getOrder(BigInteger id) {
        uint256(id, "id");
        State snapshot = requireState();
        return rpc("getOrder", () -> detail(id, snapshot.contract().getOrder(id).send()));
    }

    @Override
    public synchronized ResolveResult resolve(BigInteger id, boolean sellerWins) {
        uint256(id, "id");
        State snapshot = requireState();
        return rpc("resolve", () -> {
            // Let the actual contract enforce role/status. A revert is real evidence,
            // not a fabricated success or a locally manufactured failure transaction.
            TransactionReceipt receipt = snapshot.contract().resolve(id, sellerWins).send();
            requireSuccessfulReceipt(receipt);
            OrderDetail order = detail(id, snapshot.contract().getOrder(id).send());
            return new ResolveResult(receipt.getTransactionHash(), receipt.getBlockNumber().toString(),
                    receipt.getStatus(), order.status());
        });
    }

    private RawTransactionManager transactionManager() throws Exception {
        var chain = web3j.ethChainId().send();
        if (chain.hasError()) throw new EscrowRpcException(chain.getError().getMessage());
        return new RawTransactionManager(web3j, credentials, chain.getChainId().longValueExact());
    }

    private void requireBackendArbiter(String arbiter) {
        if (!credentials.getAddress().equalsIgnoreCase(arbiter)) {
            throw new IllegalArgumentException("Contract arbiter " + arbiter
                    + " does not match backend account " + credentials.getAddress());
        }
    }

    private State requireState() {
        if (state == null) throw new IllegalStateException("Escrow not deployed or loaded; call deploy or load first");
        return state;
    }

    private static EscrowContext context(State snapshot, String arbiter) {
        return new EscrowContext(snapshot.contract().getContractAddress(), arbiter,
                snapshot.deployBlock().toString());
    }

    private static OrderDetail detail(BigInteger id, Escrow.Order order) {
        int status = order.status.intValueExact();
        if (status < 0 || status >= STATUS.length) {
            throw new EscrowRpcException("Unknown Escrow status: " + status);
        }
        return new OrderDetail(id.toString(), order.buyer, order.seller, order.token,
                order.amount.toString(), order.deadline.toString(), STATUS[status], order.description);
    }

    private static void uint256(BigInteger value, String name) {
        if (value == null || value.signum() < 0 || value.bitLength() > 256) {
            throw new IllegalArgumentException(name + " must be a uint256 integer");
        }
    }

    private static void requireSuccessfulReceipt(TransactionReceipt receipt) {
        if (receipt.getTransactionHash() == null || receipt.getBlockNumber() == null
                || !receipt.isStatusOK()) {
            throw new EscrowRpcException("Unsuccessful/incomplete receipt: tx=" + receipt.getTransactionHash()
                    + ", receiptStatus=" + receipt.getStatus());
        }
    }

    @FunctionalInterface
    private interface RpcCall<T> { T call() throws Exception; }

    private static <T> T rpc(String operation, RpcCall<T> call) {
        try {
            return call.call();
        } catch (IllegalArgumentException | EscrowRpcException error) {
            throw error;
        } catch (Exception error) {
            String evidence = "";
            if (error instanceof TransactionException tx) {
                evidence = tx.getTransactionReceipt().map(r -> "; transactionHash=" + r.getTransactionHash()
                        + "; blockNumber=" + r.getBlockNumber() + "; receiptStatus=" + r.getStatus()
                        + "; gasUsed=" + r.getGasUsed()).orElse("");
            }
            throw new EscrowRpcException(operation + " failed: " + error.getMessage() + evidence, error);
        }
    }
}
