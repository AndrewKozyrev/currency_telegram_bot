package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.entity.Subscriber;

public interface SubscriberService {

    Subscriber save(Long telegramId);

    Subscriber findByTelegramId(Long id);

    void update(Subscriber subscriber);
}
