package br.edu.atitus.currencyservice.controllers;

import br.edu.atitus.currencyservice.clients.BCBClient;
import br.edu.atitus.currencyservice.clients.BCBResponse;
import br.edu.atitus.currencyservice.dtos.CurrencyDTO;
import br.edu.atitus.currencyservice.entities.CurrencyEntity;
import br.edu.atitus.currencyservice.repositories.CurrencyRepository;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("currency")
public class CurrencyController {

    @Value("${server.port}")
    private String port;

    @Value("${convert.sleep:0}")
    private int sleep;

    private final CurrencyRepository repository;
    private final BCBClient bcbClient;

    public CurrencyController(CurrencyRepository repository, BCBClient bcbClient) {
        this.repository = repository;
        this.bcbClient = bcbClient;
    }

    @GetMapping("/convert")
    public ResponseEntity<CurrencyDTO> getConvert(
            @RequestParam String source,
            @RequestParam String target) throws Exception {
        Thread.sleep(sleep);
        source = source.toUpperCase();
        target = target.toUpperCase();
        CurrencyEntity currency = repository.findBySourceCurrencyAndTargetCurrency(source, target)
                .orElseThrow(() -> new Exception("Currency not found"));
        String environment = "Currency-service running on port: " + port;
        CurrencyDTO dto = new CurrencyDTO(currency.getSourceCurrency(),
                currency.getTargetCurrency(),
                currency.getConversionRate(),
                environment);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/cotacao")
    @Cacheable("cotacao")
    @Retry(name = "bcb-client")
    public ResponseEntity<BCBResponse> getCotacao(
            @RequestParam String moeda) {
        // Data fixa de um dia útil
        String dataCotacao = "05-12-2025";
        BCBResponse response = bcbClient.getCotacao(moeda.toUpperCase(), dataCotacao, "json");
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}