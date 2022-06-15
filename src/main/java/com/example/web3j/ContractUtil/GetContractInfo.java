package com.example.web3j.ContractUtil;

import com.example.web3j.config.Config;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Log4j2
@Component
public class GetContractInfo {

    @Autowired
    Config config;
    @Autowired
    Web3j web3j;

    public String getTokenName() {
        Function function = new Function(
                "name",
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Utf8String>(){
                }));

        Optional<Type> res = Optional.ofNullable(createCallTx(function));
        Utf8String name = (Utf8String) res.orElse(Utf8String.DEFAULT);
        return name.getValue();
    }

    public String getSymbol() {
        Function function = new Function(
                "symbol",
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Utf8String>(){
                }));
        Optional<Type> res = Optional.ofNullable(createCallTx(function));
        Utf8String symbol = (Utf8String) res.orElse(Utf8String.DEFAULT);
        return symbol.getValue();
    }

    private Type createCallTx(Function function) {
        String encodedFunction = FunctionEncoder.encode(function);
        try {
            EthCall response = web3j.ethCall(
                            Transaction.createEthCallTransaction(null, config.getContract(), encodedFunction),
                            DefaultBlockParameterName.LATEST)
                    .sendAsync().get();

            List<Type> results = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
            return results.get(0);
        } catch (Exception e) {
            log.error("get contract info error", e);
            return null;
        }
    }
}
