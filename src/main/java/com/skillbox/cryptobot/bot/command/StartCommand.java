package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.SubscriberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;


/**
 * Обработка команды начала работы с ботом
 */
@Service
@AllArgsConstructor
@Slf4j
public class StartCommand implements IBotCommand {

    private final SubscriberService subscriberService;

    @Override
    public String getCommandIdentifier() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Запускает бота";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        try {
            SendMessage answer = new SendMessage();
            answer.setChatId(message.getChatId());
            if (arguments.length != 0) {
                log.error("Incorrect usage of start command.");
                answer.setText("""
                        Неверный вызов команды /start.
                        Правильное использование: /start
                        """);
                absSender.execute(answer);
                return;
            }
            long userId = message.getFrom().getId();
            subscriberService.save(userId);
            answer.setText("""
                    Привет! Данный бот помогает отслеживать стоимость биткоина.
                    Поддерживаемые команды:
                     /get_price - получить стоимость биткоина
                    """);
            absSender.execute(answer);
        } catch (Exception e) {
            log.error("Error occurred in /start command", e);
        }
    }
}