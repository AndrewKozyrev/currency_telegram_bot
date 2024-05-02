package com.skillbox.cryptobot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "telegram_id")
    private Long telegramId;

    private BigDecimal price;

    @Column(name = "last_notified")
    private LocalDateTime lastNotified;

    @Column(name = "chat_id")
    private Long chatId;
}
