package io.mitrofanovbp.testdrivebot.telegram.utils;

import io.mitrofanovbp.testdrivebot.dto.CarDto;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Helpers for composing user-facing texts.
 */
public class TextUtils {

    /**
     * Sat, 16 Aug 2025, 13:00 UTC
     */
    private static final DateTimeFormatter HUMAN_UTC =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy, HH:mm 'UTC'")
                    .withLocale(Locale.ENGLISH);

    /**
     * Formats the slot in UTC with a beautiful view.
     */
    public static String formatSlot(OffsetDateTime slotUtc) {
        return HUMAN_UTC.format(slotUtc.withOffsetSameInstant(ZoneOffset.UTC));
    }

    public static String carSelected(CarDto car) {
        String desc = (car.getDescription() == null || car.getDescription().isBlank())
                ? ""
                : ("\n\n" + car.getDescription());
        return "Selected: " + car.getModel() + desc + "\n\nPick a day (UTC):";
    }

    public static String confirmText(CarDto car, OffsetDateTime slotUtc) {
        return "Please confirm your booking:\n\n" + bookingSummary(car, slotUtc);
    }

    public static String bookingSummary(CarDto car, OffsetDateTime slotUtc) {
        return "Car: " + car.getModel() +
                "\nTime (UTC): " + formatSlot(slotUtc) +
                "\nSlot length: 1 hour";
    }

    public static String bookingConfirmedText(CarDto car, OffsetDateTime slotUtc) {
        return "✅ Booking confirmed!\n\n" + bookingSummary(car, slotUtc);
    }
}
