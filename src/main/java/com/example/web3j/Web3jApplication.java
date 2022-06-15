package com.example.web3j;

import com.example.web3j.ContractUtil.GetContractInfo;
import com.example.web3j.ContractUtil.GetOwnerShipInfo;
import com.example.web3j.ContractUtil.HandleToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.web3j.crypto.CipherException;

import java.io.IOException;

@SpringBootApplication
public class Web3jApplication implements ApplicationRunner {

	@Autowired
	GetContractInfo getContractInfo;
	@Autowired
	HandleToken handleToken;
	@Autowired
	GetOwnerShipInfo getOwnerShipInfo;

	public static void main(String[] args) {
		SpringApplication.run(Web3jApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws CipherException, IOException {
//		String name = getContractInfo.getTokenName();
//		String symbol = getContractInfo.getSymbol();
//		System.out.println(name);
//		System.out.println(symbol);
//		System.out.println(totalSupply);
//		System.out.println(maxSupply);
		String privkey = "3a53f00b9e798de7e146e36b7c8bb2dfd609a5b35111d4823f089ed1e6b61b27";
		String toAddress = "";
		System.out.println(getOwnerShipInfo.getOwnerOf(0));
//		getOwnerShipInfo.getBalanceOf("0x43Eae5C64EfAf074817F5160D4f224E2E12aB4d1");
//		getOwnerShipInfo.getOwnerOf(0);
//		System.out.println(handleToken.mintToken("0xF016CA9D915577F34488c7d5aED864bAcc107808", 0));
//		System.out.println(handleToken.checkIfSucceed("0x9feb3cd5aa5ace376edc2b99462a3e0ad8421a528e86430a4103faa9ed00d765"));
		System.out.println(handleToken.transferToken("57eb13ffbe672c224bf3e11378fc5a80801ba8d188587fa1fd947df12041c7e3", "0x43Eae5C64EfAf074817F5160D4f224E2E12aB4d1", 0));
	}

}
