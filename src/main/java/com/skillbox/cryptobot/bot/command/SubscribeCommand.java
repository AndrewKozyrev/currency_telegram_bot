package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.entity.Subscriber;
import com.skillbox.cryptobot.service.SubscriberService;
import com.skillbox.cryptobot.service.impl.CryptoCurrencyService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.math.BigDecimal;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SubscribeCommand implements IBotCommand {

    private final CryptoCurrencyService currencyService;

    private final SubscriberService subscriberService;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        try {
            SendMessage answer = new SendMessage();
            answer.setChatId(message.getChatId());
            if (arguments.length != 1) {
                log.error("Incorrect usage of subscribe command.");
                answer.setText("""
                        Неверный вызов команды /subscribe.
                        Правильное использование: /subscribe <цена>
                        Пример: /subscribe 34600
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
            BigDecimal price = new BigDecimal(arguments[0]);
            subscriber.setPrice(price);
            subscriber.setChatId(message.getChatId());
            subscriberService.update(subscriber);
            answer.setText("""
                            Текущая цена биткоина %s USD.
                            Новая подписка создана на стоимость %s USD.
                            """.formatted(
                            TextUtil.toString(currencyService.getBitcoinPrice()),
                            TextUtil.toString(price)
                    )
            );
            absSender.execute(answer);
        } catch (Exception e) {
            log.error("Ошибка возникла в /subscribe методе", e);
        }
    }
}