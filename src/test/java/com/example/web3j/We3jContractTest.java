package com.example.web3j;

import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class We3jContractTest {

    public static String node = "https://rinkeby.infura.io/v3/7d00bf84530c4264969a4f0f231de8b6";
    Web3j web3j;
    Credentials credentials;
    {

        web3j = Web3j.build(new HttpService(node));
        credentials = Credentials.create("3a53f00b9e798de7e146e36b7c8bb2dfd609a5b35111d4823f089ed1e6b61b27");
    }

    public static final String contractAddress = "0xb19c13d0A37cDDE5c1F969a0d9BD6a50B3A11B4E";


    /**
     * 调用合约的只读方法，无需gas
     * @throws Exception
     */
    @Test
    public void getName() throws Exception {

        Function function = new Function(
                "getName",
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Utf8String>(){
                }));

        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(
                        Transaction.createEthCallTransaction(null, contractAddress, encodedFunction),
                        DefaultBlockParameterName.LATEST)
                .sendAsync().get();

        List<Type> results = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        Utf8String preValue = (Utf8String)results.get(0);
        System.out.println(preValue.getValue());
    }

    /**
     * 需要支付gas的方法
     * @throws Exception
     */
    @Test
    public void setName() throws Exception {

        Function function = new Function(
                "setName",
                Arrays.asList(new Utf8String("test")),
                Collections.emptyList());
        BigInteger nonce = getNonce(credentials.getAddress());
        System.out.println(nonce);
        String encodedFunction = FunctionEncoder.encode(function);

        BigInteger gasLimit = new BigInteger("30000");
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, DefaultGasProvider.GAS_PRICE, gasLimit, contractAddress, encodedFunction);

        org.web3j.protocol.core.methods.response.EthSendTransaction response =
                web3j.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
                        .sendAsync()
                        .get();

        String transactionHash = response.getTransactionHash();
        System.out.println(transactionHash);
    }

    /**
     * 需要支付gas和value的合约方法调用
     * @throws Exception
     */
    @Test
    public void payETH() throws Exception {

        BigInteger nonce = getNonce(credentials.getAddress());
        Function function = new Function("payETH",
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST);

        String functionEncode = FunctionEncoder.encode(function);
        BigInteger value = new BigInteger("9");
        // 与不需要支付的value的方法调用，差别就在于多传一个eth数量的value参数
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT, contractAddress, functionEncode);
        org.web3j.protocol.core.methods.response.EthSendTransaction response =
                web3j.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
                        .sendAsync()
                        .get();
        String transactionHash = response.getTransactionHash();
        System.out.println(transactionHash);
    }


    private BigInteger getNonce(String address) throws Exception {

        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();
        return ethGetTransactionCount.getTransactionCount();
    }

    @Test
    public void getBalance() throws Exception {

        Function function = new Function(
                "getBalance",
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Uint>(){
                }));

        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(
                        Transaction.createEthCallTransaction(null, contractAddress, encodedFunction),
                        DefaultBlockParameterName.LATEST)
                .sendAsync().get();

        List<Type> results = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        Uint preValue = (Uint)results.get(0);
        System.out.println(preValue.getValue());
    }

}
