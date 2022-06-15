package com.example.web3j.ContractUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;

@Component
public class GetOwnerShipInfo {

    @Autowired
    ServiceUtil serviceUtil;

    public String getOwnerOf(int tokenId) {
        Function function = new Function(
                "ownerOf",
                Arrays.asList(new Uint256(tokenId)),
                Arrays.asList(new TypeReference<Address>(){
                }));

        Optional<Type> res = Optional.ofNullable(serviceUtil.createCallTx(function));
        Address address = (Address) res.orElse(Address.DEFAULT);
        return address.getValue();
    }

    public BigInteger getBalanceOf(String address) {
        Function function = new Function(
                "balanceOf",
                Arrays.asList(new Address(address)),
                Arrays.asList(new TypeReference<Uint256>(){
                }));

        Optional<Type> res = Optional.ofNullable(serviceUtil.createCallTx(function));
        Uint256 amount = (Uint256) res.orElse(Address.DEFAULT);
        return amount.getValue();
    }
}
