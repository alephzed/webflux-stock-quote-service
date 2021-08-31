package com.herringbone.webfluxstockquoteservice.service;

import com.herringbone.webfluxstockquoteservice.model.Quote;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class QuoteGeneratorServiceTest {

    QuoteGeneratorService quoteGeneratorService = new QuoteGeneratorService();

    @Test
    public void fetchQuoteStream() throws Exception{
        Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(100L));
        quoteFlux.take(10).subscribe(System.out::println);

        Thread.sleep(1000);
        Thread.sleep(1000);
        Thread.sleep(1000);
        Thread.sleep(1000);
        Thread.sleep(1000);
        Thread.sleep(1000);
        Thread.sleep(1000);
    }

    @Test
    public void fetchQuoteStreamCountdown() throws Exception {
        Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(100L));

        Consumer<Quote> println = System.out::println;

        Consumer<Throwable> errorHandler = e -> System.out.println("Some Error Occurred");

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Runnable allDone = () -> countDownLatch.countDown();

        quoteFlux.take( 20).subscribe(println, errorHandler, allDone);
        countDownLatch.await();
    }
}
