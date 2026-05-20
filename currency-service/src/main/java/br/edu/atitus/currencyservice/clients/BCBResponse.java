package br.edu.atitus.currencyservice.clients;

import java.util.List;

public record BCBResponse(
        List<BCBCotacao> value
) {
    public record BCBCotacao(
            Double cotacaoCompra,
            Double cotacaoVenda,
            String dataHoraCotacao
    ) {}
}