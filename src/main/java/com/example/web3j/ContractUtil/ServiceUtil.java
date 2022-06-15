package com.example.web3j.ContractUtil;

import com.example.web3j.config.Config;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.List;

@Component
@Log4j2
public class ServiceUtil {

    @Autowired
    Web3j web3j;
    @Autowired
    Config config;

    public BigInteger getNonce(String address) throws Exception {

        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();
        return ethGetTransactionCount.getTransactionCount();
    }

    public Type createCallTx(Function function) {
        String encodedFunction = FunctionEncoder.encode(function);
        try {
            EthCall response = web3j.ethCall(
                            Transaction.createEthCallTransaction(null, config.getContract(), encodedFunction),
                            DefaultBlockParameterName.LATEST)
                    .sendAsync().get();

            List<Type> results = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
            return results.get(0);
        } catch (Exception e) {
            log.error("get info error", e);
            return null;
        }
    }

    public String createFunctionTx(Credentials credentials, Function function, BigInteger gasLimit) {

        try {
            BigInteger nonce = getNonce(credentials.getAddress());
            String encodedFunction = FunctionEncoder.encode(function);

            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, DefaultGasProvider.GAS_PRICE, gasLimit, config.getContract(), encodedFunction);

            EthSendTransaction response =
                    web3j.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
                            .sendAsync()
                            .get();
            if (response.hasError()) {
                log.error(response.getError().getMessage());
                return null;
            }
            String transactionHash = response.getTransactionHash();
            return transactionHash;
        } catch (Exception e) {
            log.error("createFunctionTx error", e);
            return null;
        }
    }
}
