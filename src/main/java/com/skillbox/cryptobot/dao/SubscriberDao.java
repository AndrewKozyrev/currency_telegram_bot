package com.skillbox.cryptobot.dao;

import com.skillbox.cryptobot.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriberDao extends JpaRepository<Subscriber, UUID> {
    Optional<Subscriber> findByTelegramId(Long telegramId);
}
