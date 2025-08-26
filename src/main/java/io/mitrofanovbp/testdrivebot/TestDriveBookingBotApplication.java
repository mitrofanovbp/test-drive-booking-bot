package io.mitrofanovbp.testdrivebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for Test Drive Booking Bot application.
 * Enables scheduling for future cron/maintenance tasks.
 */
@SpringBootApplication
@EnableScheduling
public class TestDriveBookingBotApplication {

    /**
     * Main method.
     *
     * @param args CLI args
     */
    public static void main(String[] args) {
        SpringApplication.run(TestDriveBookingBotApplication.class, args);
    }
}
