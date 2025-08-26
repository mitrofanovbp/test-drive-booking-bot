package io.mitrofanovbp.testdrivebot.telegram.commands;

import io.mitrofanovbp.testdrivebot.dto.BookingDto;
import io.mitrofanovbp.testdrivebot.telegram.utils.KeyboardUtils;
import io.mitrofanovbp.testdrivebot.telegram.utils.TextUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

/**
 * Shows user's active bookings with inline cancel buttons.
 */
public class MyBookingsCommand {

    /**
     * The old compatible method is to immediately collect SendMessage.
     */
    public static SendMessage listMine(Long chatId, List<BookingDto> list) {
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(buildText(list))
                .replyMarkup(buildKeyboard(list))
                .build();
    }

    /**
     * Text only is convenient when editing an existing message.
     */
    public static String buildText(List<BookingDto> list) {
        if (list.isEmpty()) {
            return "You have no active bookings.";
        }
        StringBuilder sb = new StringBuilder("Your active bookings:\n\n");
        for (BookingDto b : list) {
            sb.append("#").append(b.getId())
                    .append(" — ").append(b.getCarModel())
                    .append(" — ").append(TextUtils.formatSlot(b.getDatetime()))
                    .append("\n");
        }
        return sb.toString();
    }

    /**
     * Keyboard only — cancel + back buttons.
     */
    public static InlineKeyboardMarkup buildKeyboard(List<BookingDto> list) {
        if (list.isEmpty()) {
            return KeyboardUtils.mainMenu();
        }
        return KeyboardUtils.cancelKeyboard(list);
    }
}
