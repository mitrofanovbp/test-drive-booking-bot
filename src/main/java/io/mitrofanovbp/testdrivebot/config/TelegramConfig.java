package io.mitrofanovbp.testdrivebot.config;

import io.mitrofanovbp.testdrivebot.service.BookingService;
import io.mitrofanovbp.testdrivebot.service.CarService;
import io.mitrofanovbp.testdrivebot.service.UserService;
import io.mitrofanovbp.testdrivebot.telegram.TestDriveBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramConfig {

    private final AppProperties props;

    public TelegramConfig(AppProperties props) {
        this.props = props;
    }

    @Bean
    public TestDriveBot testDriveBot(UserService userService,
                                     CarService carService,
                                     BookingService bookingService) throws Exception {
        if (!StringUtils.hasText(props.getTelegramBotToken()) ||
                !StringUtils.hasText(props.getTelegramBotUsername())) {
            throw new TelegramApiException("Bot token and username can't be empty");
        }

        TestDriveBot bot = new TestDriveBot(props, userService, carService, bookingService);

        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
        bot.registerCommands();

        return bot;
    }
}
