package io.mitrofanovbp.testdrivebot.telegram;

import io.mitrofanovbp.testdrivebot.config.AppProperties;
import io.mitrofanovbp.testdrivebot.dto.BookingDto;
import io.mitrofanovbp.testdrivebot.dto.CarDto;
import io.mitrofanovbp.testdrivebot.exception.BadRequestException;
import io.mitrofanovbp.testdrivebot.exception.ConflictException;
import io.mitrofanovbp.testdrivebot.exception.NotFoundException;
import io.mitrofanovbp.testdrivebot.model.User;
import io.mitrofanovbp.testdrivebot.service.BookingService;
import io.mitrofanovbp.testdrivebot.service.CarService;
import io.mitrofanovbp.testdrivebot.service.UserService;
import io.mitrofanovbp.testdrivebot.telegram.commands.CarsCommand;
import io.mitrofanovbp.testdrivebot.telegram.commands.MyBookingsCommand;
import io.mitrofanovbp.testdrivebot.telegram.commands.StartCommand;
import io.mitrofanovbp.testdrivebot.telegram.utils.KeyboardUtils;
import io.mitrofanovbp.testdrivebot.telegram.utils.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static io.mitrofanovbp.testdrivebot.telegram.Callbacks.*;

public class TestDriveBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TestDriveBot.class);

    private final AppProperties props;
    private final UserService userService;
    private final CarService carService;
    private final BookingService bookingService;

    public TestDriveBot(AppProperties props,
                        UserService userService,
                        CarService carService,
                        BookingService bookingService) {
        super(props.getTelegramBotToken());
        this.props = props;
        this.userService = userService;
        this.carService = carService;
        this.bookingService = bookingService;
    }

    public void registerCommands() {
        List<BotCommand> cmds = List.of(
                new BotCommand("/start", "Start and see menu"),
                new BotCommand("/cars", "Browse cars and book"),
                new BotCommand("/my", "My bookings"),
                new BotCommand("/help", "Help")
        );
        try {
            execute(new SetMyCommands(cmds, new BotCommandScopeDefault(), null));
            log.info("Telegram bot commands registered");
        } catch (TelegramApiException e) {
            log.warn("Failed to set bot commands after registration: {}", e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return props.getTelegramBotUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                handleCallback(update.getCallbackQuery());
            }
        } catch (Exception e) {
            log.error("Error processing update", e);
        }
    }

    /* ============== text messages ============== */

    private void handleMessage(Message msg) throws TelegramApiException {
        Long chatId = msg.getChatId();
        String text = msg.getText();
        User user = ensureUser(msg.getFrom());

        switch (text.toLowerCase()) {
            case "/start" -> execute(StartCommand.greeting(chatId, user));
            case "/help" -> execute(StartCommand.help(chatId));
            case "/cars", "cars" -> execute(CarsCommand.carsList(chatId, carService.listAll()));
            case "/my", "my bookings" ->
                    execute(MyBookingsCommand.listMine(chatId, bookingService.getActiveForUser(user)));
            default -> execute(StartCommand.menu(chatId));
        }
    }

    /* ============== callbacks ============== */

    private void handleCallback(CallbackQuery cb) {
        final String data = cb.getData();
        final Long chatId = cb.getMessage().getChatId();
        final Integer msgId = cb.getMessage().getMessageId();

        ackQuiet(cb);

        try {
            if (START.equals(data)) {
                editOrSendSafe(chatId, msgId, "What would you like to do?", KeyboardUtils.mainMenu());
                return;
            }

            if (CARS.equals(data)) {
                var cars = carService.listAll();
                editOrSendSafe(chatId, msgId, "Choose a car:", KeyboardUtils.carsKeyboard(cars));
                return;
            }

            String[] p = data.split("\\|");
            switch (p[0]) {
                case CAR -> {
                    long carId = Long.parseLong(p[1]);
                    editOrSendSafe(chatId, msgId,
                            TextUtils.carSelected(carService.get(carId)),
                            KeyboardUtils.daysKeyboard(carId, LocalDate.now(ZoneOffset.UTC), 7));
                }
                case DAY -> {
                    long carId = Long.parseLong(p[1]);
                    LocalDate day = LocalDate.parse(p[2]);
                    var slots = bookingService.freeSlotsUtc(carId, day);
                    editOrSendSafe(chatId, msgId,
                            "Pick a time (UTC) for " + day + ":",
                            KeyboardUtils.timeSlotsKeyboard(carId, day, slots));
                }
                case TIME -> {
                    long carId = Long.parseLong(p[1]);
                    OffsetDateTime slotUtc;
                    // поддержка двух форматов: 1) 2025-08-14T16:00Z  2) DAY + HOUR
                    if (p.length == 3) {
                        slotUtc = OffsetDateTime.parse(p[2]).withOffsetSameInstant(ZoneOffset.UTC);
                    } else if (p.length == 4) {
                        LocalDate day = LocalDate.parse(p[2]);
                        int hour = Integer.parseInt(p[3]);
                        slotUtc = day.atTime(hour, 0).atOffset(ZoneOffset.UTC);
                    } else {
                        editOrSendSafe(chatId, msgId,
                                "Could not parse time, please pick a day again:",
                                KeyboardUtils.daysKeyboard(carId, LocalDate.now(ZoneOffset.UTC), 7));
                        return;
                    }

                    CarDto car = carService.get(carId);
                    editOrSendSafe(chatId, msgId, TextUtils.confirmText(car, slotUtc),
                            KeyboardUtils.confirmKeyboard(carId, slotUtc));
                }
                case CONFIRM -> {
                    long carId = Long.parseLong(p[1]);
                    OffsetDateTime slot = OffsetDateTime.parse(p[2]).withOffsetSameInstant(ZoneOffset.UTC);
                    User user = ensureUser(cb.getFrom());

                    try {
                        bookingService.createBooking(user, carId, slot);

                        CarDto car = carService.get(carId);
                        editOrSendSafe(chatId, msgId, TextUtils.bookingConfirmedText(car, slot), null);

                        execute(SendMessage.builder()
                                .chatId(chatId.toString())
                                .text("What would you like to do next?")
                                .replyMarkup(KeyboardUtils.mainMenu())
                                .build());

                    } catch (BadRequestException ex) {
                        LocalDate day = slot.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate();
                        var slots = bookingService.freeSlotsUtc(carId, day);
                        editOrSendSafe(chatId, msgId,
                                "❌ " + ex.getMessage() + "\nPlease choose another time:",
                                KeyboardUtils.timeSlotsKeyboard(carId, day, slots));
                    } catch (ConflictException ex) {
                        LocalDate day = slot.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate();
                        var slots = bookingService.freeSlotsUtc(carId, day);
                        editOrSendSafe(chatId, msgId,
                                "⚠️ This slot was just booked by someone else. Pick another time:",
                                KeyboardUtils.timeSlotsKeyboard(carId, day, slots));
                    } catch (NotFoundException ex) {
                        var cars = carService.listAll();
                        editOrSendSafe(chatId, msgId,
                                "The selected car is no longer available. Please choose another:",
                                KeyboardUtils.carsKeyboard(cars));
                    }
                }
                case BACK -> {
                    String target = p[1];
                    switch (target) {
                        case "START" ->
                                editOrSendSafe(chatId, msgId, "What would you like to do?", KeyboardUtils.mainMenu());
                        case "CARS" -> {
                            var cars = carService.listAll();
                            editOrSendSafe(chatId, msgId, "Choose a car:", KeyboardUtils.carsKeyboard(cars));
                        }
                        case "DAY" -> {
                            long carId = Long.parseLong(p[2]);
                            editOrSendSafe(chatId, msgId,
                                    "Pick a day (UTC):",
                                    KeyboardUtils.daysKeyboard(carId, LocalDate.now(ZoneOffset.UTC), 7));
                        }
                        case "TIME" -> {
                            long carId = Long.parseLong(p[2]);
                            LocalDate day = LocalDate.parse(p[3]);
                            var slots = bookingService.freeSlotsUtc(carId, day);
                            editOrSendSafe(chatId, msgId,
                                    "Pick a time (UTC) for " + day + ":",
                                    KeyboardUtils.timeSlotsKeyboard(carId, day, slots));
                        }
                    }
                }
                case MY -> {
                    User user = ensureUser(cb.getFrom());
                    List<BookingDto> list = bookingService.getActiveForUser(user);
                    editOrSendSafe(chatId, msgId, MyBookingsCommand.buildText(list), MyBookingsCommand.buildKeyboard(list));
                }
                case CANCEL_BOOKING -> {
                    long bookingId = Long.parseLong(p[1]);
                    User user = ensureUser(cb.getFrom());
                    try {
                        bookingService.cancelByUser(user, bookingId);
                    } catch (NotFoundException ex) {
                        log.debug("Cancel requested for missing booking {} by user {}", bookingId, user.getId());
                    }
                    List<BookingDto> list = bookingService.getActiveForUser(user);
                    editOrSendSafe(chatId, msgId, MyBookingsCommand.buildText(list), MyBookingsCommand.buildKeyboard(list));
                }
                case CANCEL -> {
                    editOrSendSafe(chatId, msgId, "❌ Booking flow canceled.", null);
                    execute(SendMessage.builder()
                            .chatId(chatId.toString())
                            .text("What would you like to do next?")
                            .replyMarkup(KeyboardUtils.mainMenu())
                            .build());
                }
                default -> editOrSendSafe(chatId, msgId, "What would you like to do?", KeyboardUtils.mainMenu());
            }
        } catch (Exception e) {
            log.error("Callback handling failed: {}", data, e);
            try {
                execute(SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Something went wrong. Here's the menu:")
                        .replyMarkup(KeyboardUtils.mainMenu())
                        .build());
            } catch (TelegramApiException ignore) {
            }
        }
    }

    /* ============== helpers ============== */

    private void editOrSendSafe(Long chatId, Integer messageId, String text, InlineKeyboardMarkup kb) throws TelegramApiException {
        try {
            if (messageId != null) {
                execute(EditMessageText.builder()
                        .chatId(chatId.toString())
                        .messageId(messageId)
                        .text(text)
                        .replyMarkup(kb)
                        .build());
                return;
            }
        } catch (TelegramApiRequestException ex) {
            log.debug("Edit failed, falling back to send: {}", ex.getMessage());
        }
        execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(kb)
                .build());
    }

    private void ackQuiet(CallbackQuery cb) {
        try {
            execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(cb.getId())
                    .showAlert(false)
                    .build());
        } catch (TelegramApiException ignored) {
        }
    }

    private User ensureUser(org.telegram.telegrambots.meta.api.objects.User tgUser) {
        Long tgId = tgUser.getId();
        String username = tgUser.getUserName();
        String name = ((tgUser.getFirstName() == null) ? "" : tgUser.getFirstName()) +
                ((tgUser.getLastName() == null) ? "" : " " + tgUser.getLastName());
        return userService.findOrCreate(tgId, username, name.trim());
    }
}
