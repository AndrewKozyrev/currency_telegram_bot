package com.skillbox.cryptobot.bot;

import com.skillbox.cryptobot.dao.SubscriberDao;
import com.skillbox.cryptobot.service.impl.CryptoCurrencyService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class CryptoBot extends TelegramLongPollingCommandBot {

    private final String botUsername;
    private final SubscriberDao subscriberDao;
    private final CryptoCurrencyService currencyService;

    @Value("${telegram.bot.notify.delay.value}")
    private int notifyDelayValue;

    @Value("${telegram.bot.notify.delay.unit}")
    private TimeUnit notifyDelayTimeUnit;

    public CryptoBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            List<IBotCommand> commandList,
            SubscriberDao subscriberDao, CryptoCurrencyService currencyService) {
        super(botToken);
        this.botUsername = botUsername;
        this.subscriberDao = subscriberDao;
        this.currencyService = currencyService;

        commandList.forEach(this::register);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
    }

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    public void notifySubscribers() throws IOException {
        double currentPrice = currencyService.getBitcoinPrice();
        subscriberDao.findAll().stream()
                .filter(user -> user.getPrice() != null)
                .filter(user -> {
                    try {
                        return user.getPrice().doubleValue() > currentPrice;
                    } catch (Exception e) {
                        log.error("Error occurred while getting current rate from currency service.", e);
                        return false;
                    }
                })
                .filter(user -> isBeforeOrEqual(user.getLastNotified()))
                .forEach(user -> {
                    Long chatId = user.getChatId();
                    SendMessage notification = new SendMessage();
                    notification.setChatId(chatId);
                    notification.setText("Пора покупать, стоимость биткоина %s USD.".formatted(TextUtil.toString(currentPrice)));
                    try {
                        execute(notification);
                    } catch (Exception e) {
                        log.error("Error occurred while notifying user with id={}", user.getId(), e);
                    }
                    user.setLastNotified(LocalDateTime.now());
                    subscriberDao.save(user);
                });
    }

    private boolean isBeforeOrEqual(LocalDateTime time) {
        if (time == null) {
            return true;
        }
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime elapsedTime = time.plus(notifyDelayValue, notifyDelayTimeUnit.toChronoUnit());
        return elapsedTime.isBefore(currentTime) || elapsedTime.isEqual(currentTime);
    }
}
