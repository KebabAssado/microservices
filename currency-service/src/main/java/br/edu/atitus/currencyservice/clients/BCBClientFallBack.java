package br.edu.atitus.currencyservice.clients;

import org.springframework.stereotype.Component;

@Component
public class BCBClientFallback implements BCBClient {

    @Override
    public BCBResponse getCotacao(String moeda, String dataCotacao, String format) {
        return new BCBResponse(null);
    }
}