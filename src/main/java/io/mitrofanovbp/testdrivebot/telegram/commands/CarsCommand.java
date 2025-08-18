package io.mitrofanovbp.testdrivebot.telegram.commands;

import io.mitrofanovbp.testdrivebot.dto.CarDto;
import io.mitrofanovbp.testdrivebot.telegram.utils.KeyboardUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

/**
 * Shows car list to start booking flow.
 */
public class CarsCommand {
    public static SendMessage carsList(Long chatId, List<CarDto> cars) {
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(cars.isEmpty() ? "No cars yet." : "Choose a car:")
                .replyMarkup(KeyboardUtils.carsKeyboard(cars))
                .build();
    }
}
