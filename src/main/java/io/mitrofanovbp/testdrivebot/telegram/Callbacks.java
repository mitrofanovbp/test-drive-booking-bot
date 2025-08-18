package io.mitrofanovbp.testdrivebot.telegram;

public final class Callbacks {
    private Callbacks() {
    }

    // main menu
    public static final String START = "START";
    public static final String CARS = "CARS";
    public static final String MY = "MY";

    // booking flow
    public static final String CAR = "CAR";      // CAR|{carId}
    public static final String DAY = "DAY";      // DAY|{carId}|{yyyy-MM-dd}
    /**
     * TIME callback supports:
     * 1) TIME|{carId}|{ISO-8601 offset datetime} e.g. 2025-08-16T13:00Z
     * 2) (optional) TIME|{carId}|{yyyy-MM-dd}|{HH}
     */
    public static final String TIME = "TIME";
    public static final String CONFIRM = "CONFIRM";  // CONFIRM|{carId}|{ISO-8601 offset datetime}

    // navigation
    public static final String BACK = "BACK";        // BACK|TARGET|...

    // cancel
    public static final String CANCEL_FLOW = "CANCEL_FLOW";
    public static final String CANCEL_BOOK = "CANCEL_BOOK";

    // compatibility aliases
    @Deprecated
    public static final String CANCEL = CANCEL_FLOW;
    @Deprecated
    public static final String CANCEL_BOOKING = CANCEL_BOOK;
}
