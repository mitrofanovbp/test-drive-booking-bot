package io.mitrofanovbp.testdrivebot.telegram.commands;

import io.mitrofanovbp.testdrivebot.model.User;
import io.mitrofanovbp.testdrivebot.telegram.utils.KeyboardUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * /start and /help commands.
 */
public class StartCommand {

    public static SendMessage greeting(Long chatId, User user) {
        String text = """
                Welcome, %s!
                
                I can help you book a car test drive.
                Use the menu below to browse cars or view your bookings.
                """.formatted((user.getName() == null || user.getName().isBlank()) ? "friend" : user.getName());
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(KeyboardUtils.mainMenuKeyboard())
                .build();
    }

    public static SendMessage help(Long chatId) {
        String text = """
                🤖 *Help*
                
                • Tap *Cars* to browse and book a test drive.
                • Tap *My bookings* to view or cancel your active bookings.
                
                All times are handled in UTC and slots are hourly between 09:00–18:00.
                """;
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("Markdown")
                .replyMarkup(KeyboardUtils.mainMenuKeyboard())
                .build();
    }

    public static SendMessage menu(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("Please choose an option:")
                .replyMarkup(KeyboardUtils.mainMenuKeyboard())
                .build();
    }
}
