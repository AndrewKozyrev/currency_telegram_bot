package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.entity.Subscriber;
import com.skillbox.cryptobot.service.SubscriberService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Service
@Slf4j
@AllArgsConstructor
public class GetSubscriptionCommand implements IBotCommand {

    private final SubscriberService subscriberService;

    @Override
    public String getCommandIdentifier() {
        return "get_subscription";
    }

    @Override
    public String getDescription() {
        return "Возвращает текущую подписку";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        try {
            SendMessage answer = new SendMessage();
            answer.setChatId(message.getChatId());
            if (arguments.length != 0) {
                log.error("Incorrect usage of get_subscription command.");
                answer.setText("""
                        Неверный вызов команды /get_subscription.
                        Правильное использование: /get_subscription
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
                answer.setText("Вы подписаны на стоимость биткоина %s USD".formatted(TextUtil.toString(subscriber.getPrice())));
            } else {
                answer.setText("Активные подписки отсутствуют.");
            }
            absSender.execute(answer);
        } catch (Exception e) {
            log.error("Ошибка возникла в /get_subscription методе", e);
        }
    }
}