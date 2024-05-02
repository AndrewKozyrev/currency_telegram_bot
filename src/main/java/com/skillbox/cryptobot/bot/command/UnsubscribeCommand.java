package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.entity.Subscriber;
import com.skillbox.cryptobot.service.SubscriberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Обработка команды отмены подписки на курс валюты
 */
@Service
@Slf4j
@AllArgsConstructor
public class UnsubscribeCommand implements IBotCommand {

    private final SubscriberService subscriberService;

    @Override
    public String getCommandIdentifier() {
        return "unsubscribe";
    }

    @Override
    public String getDescription() {
        return "Отменяет подписку пользователя";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        try {
            SendMessage answer = new SendMessage();
            answer.setChatId(message.getChatId());
            if (arguments.length != 0) {
                log.error("Incorrect usage of unsubscribe command.");
                answer.setText("""
                        Неверный вызов команды /unsubscribe.
                        Правильное использование: /unsubscribe
                        """);
                absSender.execute(answer);
                return;
            }
            Subscriber subscriber = subscriberService.findByTelegramId(message.getFrom().getId());
            if (subscriber == null) {
                answer.setText("Выполните команду /start.");
                absSender.execute(answer);
                return;
            }
            if (subscriber.getPrice() != null) {
                subscriber.setPrice(null);
                subscriberService.update(subscriber);
                answer.setText("Подписка отменена");
            } else {
                answer.setText("Активные подписки отсутствуют.");
            }
            absSender.execute(answer);
        } catch (Exception e) {
            log.error("Ошибка возникла в /unsubscribe методе", e);
        }
    }
}