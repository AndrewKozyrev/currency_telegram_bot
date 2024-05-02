package com.skillbox.cryptobot.service.impl;

import com.skillbox.cryptobot.client.BinanceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class CryptoCurrencyService {
    private final AtomicReference<Double> price = new AtomicReference<>();
    private final BinanceClient client;

    public double getBitcoinPrice() throws IOException {
        if (price.get() == null) {
            price.set(client.getBitcoinPrice());
        }
        return price.get();
    }

    @Scheduled(fixedRateString = "#{${telegram.bot.update-rate.value} * T(java.util.concurrent.TimeUnit).valueOf('${telegram.bot.update-rate.unit}').toMillis(1)}")
    public void updatePrice() throws IOException {
        price.set(client.getBitcoinPrice());
    }
}
