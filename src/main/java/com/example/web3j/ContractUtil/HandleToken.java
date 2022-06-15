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

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

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

    public String mintToken(String to, int tokenId) {
        Function function = new Function(
                "safeMint",
                Arrays.asList(new Address(to), new Uint256(tokenId)),
                Collections.emptyList());

        BigInteger gasLimit = new BigInteger("400000");
        Credentials credentials = Credentials.create(config.getPrivKey());
        return serviceUtil.createFunctionTx(credentials, function, gasLimit);
    }

    public String transferToken(String fromPrivKey, String to, int tokenId) {
        Credentials credentials = Credentials.create(fromPrivKey);
        String from = credentials.getAddress();
        Function function = new Function(
                "safeTransferFrom",
                Arrays.asList(new Address(from), new Address(to), new Uint256(tokenId)),
                Collections.emptyList());

        BigInteger gasLimit = new BigInteger("4000000");
        return serviceUtil.createFunctionTx(credentials, function, gasLimit);

    }

    public Status checkIfSucceed(String txHash) throws IOException {
        Optional<TransactionReceipt> transactionReceipt =
                web3j.ethGetTransactionReceipt(txHash).send().getTransactionReceipt();
        if (transactionReceipt.isPresent()) {
            return "0x1".equals(transactionReceipt.get().getStatus()) ? Status.SUCCEED : Status.FAILED;
        } else {
            log.warn("tx not found with hash [{}]", txHash);
            return Status.UNKNOWN_TRANSACTION;
        }
    }
}
