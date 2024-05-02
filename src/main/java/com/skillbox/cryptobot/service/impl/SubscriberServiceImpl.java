package com.skillbox.cryptobot.service.impl;

import com.skillbox.cryptobot.dao.SubscriberDao;
import com.skillbox.cryptobot.entity.Subscriber;
import com.skillbox.cryptobot.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberServiceImpl implements SubscriberService {

    private final SubscriberDao subscriberDao;
    private final CryptoCurrencyService currencyService;

    @Override
    public Subscriber save(Long telegramId) {
        Subscriber storedSubscriber = findByTelegramId(telegramId);
        if (storedSubscriber != null) {
            return storedSubscriber;
        }
        Subscriber subscriber = new Subscriber();
        subscriber.setTelegramId(telegramId);
        subscriber = subscriberDao.save(subscriber);
        return subscriber;
    }

    @Override
    public Subscriber findByTelegramId(Long telegramId) {
        return subscriberDao.findByTelegramId(telegramId).orElse(null);
    }

    @Override
    public void update(Subscriber subscriber) {
        subscriberDao.save(subscriber);
    }
}
