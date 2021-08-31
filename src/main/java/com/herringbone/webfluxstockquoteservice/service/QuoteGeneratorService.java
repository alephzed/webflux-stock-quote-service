package com.herringbone.webfluxstockquoteservice.service;

import com.herringbone.webfluxstockquoteservice.model.Quote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

@Service
@Slf4j
public class QuoteGeneratorService {

    private final MathContext mathContext = new MathContext(2);
    private final Random random = new Random();
    private final List<Quote> prices = new ArrayList<>();

    public QuoteGeneratorService() {
        this.prices.add(new Quote("AAPL", 160.16));
        this.prices.add(new Quote("MSFT", 77.74));
        this.prices.add(new Quote("GOOG", 847.24));
        this.prices.add(new Quote("ORCL", 49.51));
        this.prices.add(new Quote("IBM", 159.34));
        this.prices.add(new Quote("INTC", 39.29));
        this.prices.add(new Quote("RHT", 84.29));
        this.prices.add(new Quote("VMW", 92.21));
    }

    public Flux<Quote> fetchQuoteStream(Duration period) {
        try {
            System.out.println("Before sleep");
            Thread.sleep(5000L);
            System.out.println("After sleep");
        } catch (Exception e) {
            e.printStackTrace();
        }
        final BiFunction<Integer, SynchronousSink<Quote>, Integer> integerSynchronousSinkIntegerBiFunction =
                (index, sink) -> {
            Quote updatedQuote = updateQuote(this.prices.get(index));
            sink.next(updatedQuote);
            return ++index % this.prices.size();
        };

        return Flux.generate(() -> 0,
                integerSynchronousSinkIntegerBiFunction)
                .zipWith(Flux.interval(period))
                .map(t -> t.getT1())
                .map(quote -> {
                    quote.setInstant(Instant.now());
                    return quote;
                })
                .log("com.davita.service.QuoteGenerator");
    }

    private Quote updateQuote(Quote quote) {
        System.out.println("update quote: " + quote);
        BigDecimal priceChange = quote.getPrice()
                .multiply(new BigDecimal(0.05 * this.random.nextDouble()), this.mathContext);
        return  new Quote(quote.getTicker(), quote.getPrice().add(priceChange));
    }
}
