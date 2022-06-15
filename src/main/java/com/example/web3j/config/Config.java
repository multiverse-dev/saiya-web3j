package com.example.web3j.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
@Data
public class Config {

    @Value("${rpc.url}")
    private String url;

    @Value("${wallet.privkey}")
    private String privKey;

    @Value("${contract.hash}")
    private String contract;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(url));
    }

}
