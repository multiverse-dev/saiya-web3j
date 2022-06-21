package com.example.web3j.ContractUtil;

import com.example.web3j.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Component
public class AccountUtil {

    @Autowired
    Web3j web3j;
    @Autowired
    Config config;
    @Autowired
    ServiceUtil serviceUtil;

    public String createAccount() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        return Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey());
    }

    public String getAddress(String privateKey) {
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
        return Numeric.prependHexPrefix(Keys.getAddress(ecKeyPair));
    }

    public String transferAndGetReceipt(String toAddress, BigDecimal amount) throws Exception {
        Credentials credentials = Credentials.create(config.getPrivKey());
        TransactionReceipt transactionReceipt = Transfer.sendFunds(
                web3j, credentials, toAddress,
                amount, Convert.Unit.ETHER).send();
        return transactionReceipt.getTransactionHash();
    }

    public String transferWithoutReceipt(String toAddress, BigDecimal amount) throws Exception {
        BigInteger value = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
        Credentials credentials = Credentials.create(config.getPrivKey());
        BigInteger nonce = serviceUtil.getNonce(credentials.getAddress());

        BigInteger gasPrice = BigInteger.valueOf(10_000_000_000L);
        BigInteger gasLimit = BigInteger.valueOf(100_000L);
        RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
                nonce, gasPrice, gasLimit, toAddress, value);
        EthSendTransaction response =
                web3j.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
                        .sendAsync()
                        .get();
        if (response.hasError()) {
            return null;
        }
        String transactionHash = response.getTransactionHash();
        return transactionHash;
    }
}
