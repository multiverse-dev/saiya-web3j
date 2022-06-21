package com.example.web3j.ContractUtil;

import com.example.web3j.config.Config;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Log4j2
@Component
public class HandleToken {

    enum Status
    {
        SUCCEED, FAILED, UNKNOWN_TRANSACTION
    }

    @Autowired
    Web3j web3j;
    @Autowired
    Config config;

    @Autowired
    ServiceUtil serviceUtil;

    private static ThreadPoolExecutor executor;

    @PostConstruct
    private void initExecutor(){
        executor = new java.util.concurrent.ThreadPoolExecutor(
                5,
                10,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(190),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public String mintToken(String to, BigInteger tokenId) {
        Function function = new Function(
                "safeMint",
                Arrays.asList(new Address(to), new Uint256(tokenId)),
                Collections.emptyList());

        BigInteger gasPrice = BigInteger.valueOf(10_000_000_000L);
        Credentials credentials = Credentials.create(config.getPrivKey());
        return serviceUtil.createFunctionTx(credentials, function, gasPrice);
    }

    public String transferToken(String fromPrivKey, String to, BigInteger tokenId) {
        Credentials credentials = Credentials.create(fromPrivKey);
        String from = credentials.getAddress();
        Function function = new Function(
                "safeTransferFrom",
                Arrays.asList(new Address(from), new Address(to), new Uint256(tokenId)),
                Collections.emptyList());

        BigInteger gasPrice = BigInteger.valueOf(10_000_000_000L);
        return serviceUtil.createFunctionTx(credentials, function, gasPrice);

    }

    public Status getTransactionStatus(String txHash) throws IOException {
        Optional<TransactionReceipt> transactionReceipt =
                web3j.ethGetTransactionReceipt(txHash).send().getTransactionReceipt();
        if (transactionReceipt.isPresent()) {
            return "0x1".equals(transactionReceipt.get().getStatus()) ? Status.SUCCEED : Status.FAILED;
        } else {
            log.warn("tx not found with hash [{}]", txHash);
            return Status.UNKNOWN_TRANSACTION;
        }
    }

    public Map<String, Status> getTransactionStatus(List<String> txHashes) {
        ConcurrentHashMap<String, Status> res = new ConcurrentHashMap<>();
        List<CompletableFuture> futures = txHashes.stream().map(txHash -> CompletableFuture.runAsync(() -> {
            try {
                Optional<TransactionReceipt> receipt = web3j.ethGetTransactionReceipt(txHash).send().getTransactionReceipt();
                Status status = receipt.isPresent() ? "0x1".equals(receipt.get().getStatus()) ? Status.SUCCEED : Status.FAILED : Status.UNKNOWN_TRANSACTION;
                res.put(txHash, status);
            } catch (IOException e) {
                res.put(txHash, Status.UNKNOWN_TRANSACTION);
            }
        }, executor)).collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
        return res;
    }
}
