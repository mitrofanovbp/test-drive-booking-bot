package io.mitrofanovbp.testdrivebot.telegram.utils;

import io.mitrofanovbp.testdrivebot.dto.BookingDto;
import io.mitrofanovbp.testdrivebot.dto.CarDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static io.mitrofanovbp.testdrivebot.telegram.Callbacks.*;

public final class KeyboardUtils {
    private KeyboardUtils() {
    }

    /* ---------- Formatting ---------- */
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final DateTimeFormatter DAY_FMT =
            DateTimeFormatter.ofPattern("EEE, dd MMM", LOCALE);
    private static final DateTimeFormatter HOUR_FMT =
            DateTimeFormatter.ofPattern("HH:mm", LOCALE);
    private static final DateTimeFormatter HUMAN_DT_FMT =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy, HH:mm 'UTC'", LOCALE);

    /**
     * Human readable datetime in UTC.
     */
    public static String human(OffsetDateTime dt) {
        return dt.withOffsetSameInstant(ZoneOffset.UTC).format(HUMAN_DT_FMT);
    }

    /* ---------- Helpers ---------- */
    private static InlineKeyboardButton btn(String text, String data) {
        return InlineKeyboardButton.builder().text(text).callbackData(data).build();
    }

    private static InlineKeyboardButton backTo(String target) {
        return btn("⬅️ Back", BACK + "|" + target);
    }

    /* ---------- Main menu ---------- */
    public static InlineKeyboardMarkup mainMenu() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(
                btn("Cars 🚗", CARS),
                btn("My bookings 📅", MY)
        ));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    // legacy alias
    public static InlineKeyboardMarkup mainMenuKeyboard() {
        return mainMenu();
    }

    /* ---------- Cars list ---------- */
    public static InlineKeyboardMarkup carsKeyboard(List<CarDto> cars) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (CarDto c : cars) {
            rows.add(List.of(btn(c.getModel(), CAR + "|" + c.getId())));
        }
        rows.add(List.of(backTo("START")));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    /* ---------- Day picker ---------- */
    public static InlineKeyboardMarkup daysKeyboard(long carId, LocalDate start, int days) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate d = start.plusDays(i);
            rows.add(List.of(btn(d.format(DAY_FMT), DAY + "|" + carId + "|" + d)));
        }
        rows.add(List.of(backTo("CARS")));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    /* ---------- Time slots (hour picker) ---------- */
    public static InlineKeyboardMarkup timeSlotsKeyboard(long carId, LocalDate dayUtc, List<OffsetDateTime> slots) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (OffsetDateTime slot : slots) {
            OffsetDateTime z = slot.withOffsetSameInstant(ZoneOffset.UTC)
                    .withMinute(0).withSecond(0).withNano(0);
            String label = HOUR_FMT.format(z) + " UTC";
            String data = TIME + "|" + carId + "|" + z.toString();
            rows.add(List.of(btn(label, data)));
        }
        rows.add(List.of(backTo("DAY|" + carId)));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    /* ---------- Confirm screen ---------- */
    public static InlineKeyboardMarkup confirmKeyboard(long carId, OffsetDateTime slotUtc) {
        OffsetDateTime z = slotUtc.withOffsetSameInstant(ZoneOffset.UTC)
                .withMinute(0).withSecond(0).withNano(0);
        String day = z.toLocalDate().toString();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(btn("✅ Confirm", CONFIRM + "|" + carId + "|" + z)));
        rows.add(List.of(
                btn("⬅️ Back", BACK + "|TIME|" + carId + "|" + day),
                btn("✖️ Cancel", CANCEL_FLOW)
        ));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    /* ---------- My bookings ---------- */
    public static InlineKeyboardMarkup myBookingsKeyboard(List<BookingDto> list) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (BookingDto b : list) {
            rows.add(List.of(btn("Cancel #" + b.getId(), CANCEL_BOOK + "|" + b.getId())));
        }
        rows.add(List.of(backTo("START")));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public static InlineKeyboardMarkup cancelKeyboard(List<BookingDto> list) {
        return myBookingsKeyboard(list);
    }

    public static InlineKeyboardMarkup backKeyboard() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(backTo("START"))))
                .build();
    }

    public static InlineKeyboardMarkup backAndCancelKeyboard() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(backTo("START"), btn("✖️ Cancel", CANCEL_FLOW))))
                .build();
    }

    public static InlineKeyboardMarkup cancelKeyboard() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(btn("✖️ Cancel", CANCEL_FLOW))))
                .build();
    }
}
