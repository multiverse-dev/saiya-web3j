package com.example.web3j.ContractUtil;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Log4j2
@Component
public class GetContractInfo {

    @Autowired
    ServiceUtil serviceUtil;

    public String getTokenName() {
        Function function = new Function(
                "name",
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Utf8String>(){
                }));

        Optional<Type> res = Optional.ofNullable(serviceUtil.createCallTx(function));
        Utf8String name = (Utf8String) res.orElse(Utf8String.DEFAULT);
        return name.getValue();
    }

    public String getSymbol() {
        Function function = new Function(
                "symbol",
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Utf8String>(){
                }));
        Optional<Type> res = Optional.ofNullable(serviceUtil.createCallTx(function));
        Utf8String symbol = (Utf8String) res.orElse(Utf8String.DEFAULT);
        return symbol.getValue();
    }

    public String getOwner() {
        Function function = new Function(
                "owner",
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Address>(){
                }));
        Optional<Type> res = Optional.ofNullable(serviceUtil.createCallTx(function));
        Address owner = (Address) res.orElse(Address.DEFAULT);
        return owner.getValue();
    }
}
